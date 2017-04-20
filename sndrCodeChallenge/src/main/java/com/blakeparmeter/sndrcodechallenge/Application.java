/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blakeparmeter.sndrcodechallenge;

import java.io.File;
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
    private static final List<File> CORPORA_FILES = new ArrayList();
    private static final Scanner INPUT_SCANNER = new Scanner(System.in);
    
    //Using an executor service so we have a named thread for reading and so
    //subsequent calls are not loaded on the call stack
    private static final ExecutorService USER_INPUT_EXECUTOR = 
            Executors.newSingleThreadExecutor((Runnable r) -> new Thread(r, "User Input Thread."));
    
    public static void main(String[] args){
     
        //validate arguments
        for(String file : args){
            File f = new File(file);
            if(f.canRead()){
                CORPORA_FILES.add(f);
            }else{
                System.err.println("The file:"+f.getAbsolutePath()+" cannot be read. "
                        + "Please specify valid files as the arguments.");
                System.exit(-1);
            }
        }
        
        //Test for index file and load if found. 
        //TODO:
        
        //Print welcome text
        System.err.println("System successfully started!\n"
                + "Welcome to the disparate corpora generator!\n"
                + CORPORA_FILES.size() + " corpora files have been read into the system.");
        
        //begin the user input read function
        USER_INPUT_EXECUTOR.submit(new UserInputTask());
        Runtime.getRuntime().addShutdownHook(new ShutdownHook());
    }
    
    /**
     * 
     */
    public static synchronized String getNextCorpora(){
        
        currentIndex++;
        return "TEST CORPORA";
    }
    
    private static class UserInputTask implements Runnable{

        @Override
        public void run() {
            System.out.println("\t<waiting for user keystroke>");
            
            //We dont do anything with the input just wait for it, in production 
            //we'd probably store this and the generated value in some type of DB.
            String input = INPUT_SCANNER.nextLine(); 
            
            System.out.println(getNextCorpora());
            USER_INPUT_EXECUTOR.submit(new UserInputTask());
        }
    }
}
