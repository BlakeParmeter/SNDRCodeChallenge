/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blakeparmeter.sndrcodechallenge;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author Blake
 */
public class ShutdownHook extends Thread{
    
    @Override
    public void run(){
        if(Application.getIteratorConfig().isFinished() == false){
            System.out.println("System terminated, building the index file...");
            ObjectMapper mapper = new ObjectMapper();
            try{
                mapper.writeValue(new File(Application.INDEX_FILE_PATH), Application.getIteratorConfig());
                System.out.println("Index file successfully created, it will be used next time the program is run.");
            }catch(IOException ex){
                System.out.println("The index file could not be created, "
                        + "next time the program run it will rebuild the index file.");
            }
        }else{
            File index = new File(Application.INDEX_FILE_PATH);
            index.delete();
            System.out.println("The index file has been deleted, it wiil be recreated before the next run.");
        }
    }
}
