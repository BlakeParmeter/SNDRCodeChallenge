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
    
    public ShutdownHook(){
        System.out.println("com.blakeparmeter.sndrcodechallenge.ShutdownHook.<init>()");
    }
    
    @Override
    public void run(){
        System.out.println("system shutdown, executing this code...");
    }
}
