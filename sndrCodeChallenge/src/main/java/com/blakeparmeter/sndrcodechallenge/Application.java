/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blakeparmeter.sndrcodechallenge;

import com.blakeparmeter.sndrcodechallenge.CorporaReader.CorporaFileReader;
import com.blakeparmeter.sndrcodechallenge.CorporaReader.CorporaReader;
import com.blakeparmeter.sndrcodechallenge.CorporaReader.CorporaURLReader;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author Blake
 */
public class Application {
    
    private static long currentIndex = -1L;
    private static long startIndex = -1L;
    private static long maxPhrases = -1L;
    private static final List<CorporaReader> CORPORA_READERS = new ArrayList();
    private static final Scanner INPUT_SCANNER = new Scanner(System.in);
    
    //Using an executor service so we have a named thread for reading and so
    //subsequent calls are not loaded on the call stack
    private static final ExecutorService USER_INPUT_EXECUTOR = 
            Executors.newSingleThreadExecutor((Runnable r) -> new Thread(r, "User Input Thread."));
    
    public static void main(String[] args){
     
        //create corpora readers
        if(args.length < 1){
            throw new RuntimeException("You must specify at least one Corpora File.");
        }

        //loop through each argument, first check for URL if not URL then try file.
        for(String arg : args){
            try{
                try{
                    CORPORA_READERS.add(new CorporaURLReader(new URL(arg)));
                }catch(MalformedURLException mex){
                    CORPORA_READERS.add(new CorporaFileReader(new File(arg)));
                }
            }catch(Exception ex){
                System.err.println("Error reading from source: " + arg);
                ex.printStackTrace(System.err);
                System.exit(-1);
            }
        }
        
        //Test for index file and load if found. 
        //TODO:
        
        //calculate the total number of possible phrases
        maxPhrases = 1;
        for(CorporaReader reader : CORPORA_READERS){
            maxPhrases *= reader.CORPORA_SIZE;
        }
        
        //initalizes the current index if it's not been laoded from a file.
        if(currentIndex == -1L){
            //currentIndex = ((long)(Math.random() * maxPhrases) + System.currentTimeMillis()) % maxPhrases;
            currentIndex = 0;
        }
        
        //Print welcome text TODO: Format numbers and fix bug with the generated phrases for the overlap case.
        System.out.println("Welcome to the disparate corpora generator!\n" + 
                CORPORA_READERS.size() + " corpora files have been read into the system. " + 
                "These files allow for " + maxPhrases + " possible phrases. So far: " + 
                (currentIndex - (startIndex == -1L ? 0L : startIndex)) + " phrases have been generated.");
        
        //begin the user input read function
        USER_INPUT_EXECUTOR.submit(new UserInputTask());
        Runtime.getRuntime().addShutdownHook(new ShutdownHook());
    }
    
    /**
     * @return The next unique phrase available to the system.
     * @throws java.io.IOException
     */
    public static synchronized String getNextDisparateCorpora() throws IOException{
        
        //Handles ensuring that we havent went over the allowed files
        if(startIndex == currentIndex){
            System.err.println("You have excausted all possible unique iterations "
                    + "for the data set! Thank you for using this program! To continue "
                    + "using this program you must specify new input files.");
            System.exit(0);
            
        //Initalizes the start index if this is the first run
        }else if(startIndex == -1){
            startIndex = currentIndex;
        }
        
        //Determine index values
        List<Long> indexes = getCorporaIndexes();
        String retVal = "";
        
        //get the values from the corpora readers and format.
        for(int i = 0; i < indexes.size(); i++){
            String str = CORPORA_READERS.get(i).getCorporaAtIndex(indexes.get(i)).toLowerCase();
            retVal += Character.toUpperCase(str.charAt(0));
            if(str.length() > 2){
                retVal += str.substring(1);
            }
        }
        
        //iterates the current index
        currentIndex = (currentIndex + 1) % maxPhrases;
        return retVal;
    }
    
    private static List<Long> getCorporaIndexes() {
        List<Long> retVal = new ArrayList();
        long divisor = 1;
        for(CorporaReader reader : CORPORA_READERS){
            retVal.add(currentIndex/divisor%reader.CORPORA_SIZE);
            divisor *= reader.CORPORA_SIZE;
        }
        return retVal;
    }
    
    private static class UserInputTask implements Runnable{

        @Override
        public void run() {
            System.out.println("\t<waiting for user keystroke>");
            
            //We dont do anything with the input just wait for it, in production 
            //we'd probably store this and the generated value in some type of DB.
            String input = INPUT_SCANNER.nextLine(); 
            
            try{
                System.out.println(getNextDisparateCorpora());
            }catch (Exception ex){
                ex.printStackTrace(System.err);
            }
            USER_INPUT_EXECUTOR.submit(new UserInputTask());
        }
    }
}

