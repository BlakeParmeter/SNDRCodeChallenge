/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blakeparmeter.sndrcodechallenge.CorporaReader;

/**
 *
 * @author Blake
 */
public abstract class CorporaReader {
    
    protected final long CORPORA_SIZE;
    
    public CorporaReader(long size){
        CORPORA_SIZE = size;
    }
    
    public abstract String getCorporaAtIndex(long index);
}
