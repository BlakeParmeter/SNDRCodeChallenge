/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blakeparmeter.sndrcodechallenge;

import com.blakeparmeter.sndrcodechallenge.Beans.IteratorConfig;
import com.blakeparmeter.sndrcodechallenge.CorporaReader.CorporaFileReader;
import com.blakeparmeter.sndrcodechallenge.CorporaReader.CorporaReader;
import com.blakeparmeter.sndrcodechallenge.CorporaReader.CorporaURLReader;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author Blake
 */
public class Application {
    
    private static IteratorConfig iteratorConfig = new IteratorConfig();
    private static final List<CorporaReader> CORPORA_READERS = new ArrayList();
    private static final Scanner INPUT_SCANNER = new Scanner(System.in);
    public static final String INDEX_FILE_PATH = "index";
    
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
        
        //calculate the total number of possible phrases
        long maxPhrases = 1;
        for(CorporaReader reader : CORPORA_READERS){
            maxPhrases *= reader.CORPORA_SIZE;
        }
        
        //Test for index file and load if found. 
        try{
            ObjectMapper mapper = new ObjectMapper();
            File indexFile = new File(INDEX_FILE_PATH);
            iteratorConfig = mapper.readValue(indexFile, iteratorConfig.getClass());
            if(iteratorConfig.getMaxPhrases() == maxPhrases){
                System.out.println("Index file read!");
            }else{
                iteratorConfig = buildIteratorConfig(maxPhrases);
                System.out.println("An index file was found but the corpora files used to create it "
                        + "are not the same filed used in this run. The index file will be recreated.");
            }
            
        //Sets the initial values for the iterator variables
        }catch (UnrecognizedPropertyException upex){
            System.out.println("The index file is invalid, recreating index file...");
            iteratorConfig = buildIteratorConfig(maxPhrases);
        }catch(IOException ioex){
            System.out.println("The index file could not be found, recreating index file...");
            iteratorConfig = buildIteratorConfig(maxPhrases);
        }finally{
            new File(INDEX_FILE_PATH).delete();//Always delete the index file on load.
        }
        
        
        //Print welcome text TODO: Format numbers and fix bug with the generated phrases for the overlap case.
        System.out.println("Welcome to the disparate corpora generator!\n" + 
                CORPORA_READERS.size() + " corpora files have been read into the system. " + 
                "These files allow for " + maxPhrases + " possible phrases. There are: " + 
                iteratorConfig.getNumRemaining() + " phrases remaing to be generated.");
        
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
        if(iteratorConfig.isFinished()){
            System.err.println("You have excausted all possible unique iterations "
                    + "for the data set! Thank you for using this program! To continue "
                    + "using this program you must specify new input files.");
            System.exit(0);
        }
        
        //Determine index values
        List<Long> indexes = getCorporaIndexes(iteratorConfig.getCurrentIndex(), iteratorConfig.getShuffleValues());
        
        //get the values from the corpora readers and format.
        String retVal = "";
        for(int i = 0; i < indexes.size(); i++){
            String str = CORPORA_READERS.get(i).getCorporaAtIndex(indexes.get(i)).toLowerCase();
            retVal += Character.toUpperCase(str.charAt(0));
            if(str.length() > 2){
                retVal += str.substring(1);
            }
        }
        
        //iterates the current index
        iteratorConfig.incrementCurrentIndex();
        return retVal;
    }
    
    private static List<Long> getCorporaIndexes(long currentIndex, List<Long> shuffleVars) {
        
        //Sets inital values. 
        List<Long> retVal = new ArrayList();
        long divisor = 1;
        long indexToUse = shuffle(shuffleVars, currentIndex);
        
        //determines which indexes to get from the readers based on the index generated by the shuffle method
        for(CorporaReader reader : CORPORA_READERS){
            retVal.add(indexToUse/divisor%reader.CORPORA_SIZE);
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
    
    private static IteratorConfig buildIteratorConfig(long maxPhrases){
        IteratorConfig retVal = new IteratorConfig();
        retVal.setStartIndex((long)(Math.random() * maxPhrases));
        retVal.setShuffleValues(getFactors(maxPhrases));
        retVal.setMaxPhrases(maxPhrases);
        Collections.shuffle(retVal.getShuffleValues());
        return retVal;
    }
    
    private static List<Long> getFactors(long num){
        List<Long> retVal = new ArrayList();
        for(long i = 2; i <= Math.sqrt(num); i++) {
            if(num % i == 0) {
                retVal.add(i);
                if(i != num/i) {
                    retVal.add(num/i);
                }
            }
        }
        return retVal;
    }
    
    private static long shuffle(List<Long> seeds, long index){
        for(long mag : seeds){
            index = shuffle(mag, index);
        }
        return index;
    }
    
    private static long shuffle(long magnitude, long index){
        long modulo = index % magnitude;
        long bucket = index / magnitude;
        return bucket * magnitude + (magnitude - modulo);
    }
    
    public static IteratorConfig getIteratorConfig(){
        return iteratorConfig;
    }
}

