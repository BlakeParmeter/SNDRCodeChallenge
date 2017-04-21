/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blakeparmeter.sndrcodechallenge;

/**
 *
 * @author Blake
 */
public class ShutdownHook extends Thread{
    
    @Override
    public void run(){
        System.out.println("System terminated, building the index file...");
        //TODO: Build the index file
        System.out.println("Index file successfully created, it will be used next time the program is run.");
    }
}
