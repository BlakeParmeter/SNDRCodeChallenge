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
    
    private static long currentIndex = 0;
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
        
        //Print welcome text
        System.err.println("System successfully started!\n"
                + "Welcome to the disparate corpora generator!\n"
                + CORPORA_READERS.size() + " corpora files have been read into the system.");
        
        //begin the user input read function
        USER_INPUT_EXECUTOR.submit(new UserInputTask());
        Runtime.getRuntime().addShutdownHook(new ShutdownHook());
    }
    
    /**
     * 
     * @return 
     * @throws java.io.IOException
     */
    public static synchronized String getNextDisparateCorpora() throws IOException{
        
        long start = System.currentTimeMillis();
        
        String retVal = "";
        for(CorporaReader reader : CORPORA_READERS){
            String str = reader.getCorporaAtIndex(1).toLowerCase();
            retVal += Character.toUpperCase(str.charAt(0));
            if(str.length() > 2){
                retVal += str.substring(1);
            }
        }
        
        System.out.println("The operation took " + (System.currentTimeMillis() - start) + " ms.");
        
        currentIndex++;
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
            }catch (IOException ex){
                ex.printStackTrace(System.err);
            }
            USER_INPUT_EXECUTOR.submit(new UserInputTask());
        }
    }
}

