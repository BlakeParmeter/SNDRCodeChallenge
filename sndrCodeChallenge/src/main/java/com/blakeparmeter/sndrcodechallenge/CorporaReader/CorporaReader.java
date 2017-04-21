/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blakeparmeter.sndrcodechallenge.CorporaReader;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import java.io.IOException;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

/**
 * Reads the Json Parser.
 * @author Blake
 */
public abstract class CorporaReader {
    
    public final long CORPORA_SIZE;
    public final long CHECKSUM;
    
    public CorporaReader(JsonParser parser) throws Exception{
        moveParserToStartOfFirstArray(parser);
        
        //calculates the size of the list and the checksum.
        Checksum checksum = new CRC32();
        String val = getNextValidString(parser);
        long size = 0;
        while(val != null){
            byte[] bytes = val.getBytes();
            checksum.update(bytes, 0, bytes.length);
            val = getNextValidString(parser);
            size ++;
        }
        
        CORPORA_SIZE = size;
        CHECKSUM = checksum.getValue();
        parser.close();
    }
    
    public abstract JsonParser createParser() throws IOException;
    
    public String getCorporaAtIndex(long index) throws IOException{
        if(index > CORPORA_SIZE-1){
            throw new RuntimeException("Corpra index " + index + " is out of bounds.");
        }
        JsonParser parser = createParser();
        moveParserToStartOfFirstArray(parser);
        
        String val = null;
        for(int i=0; i <= index; i++){
            val = getNextValidString(parser);
        }
        
        parser.close();
        return val;
    }
    
    /**
     * Finds the first array, this is what we're going to start reading data.
     */
    private void moveParserToStartOfFirstArray(JsonParser parser) throws IOException{
        while(parser.currentToken() != JsonToken.START_ARRAY){
            JsonToken token = parser.nextToken();
            if(token == JsonToken.END_OBJECT || token == null){
                throw new RuntimeException("JSON documnet not valid, an array could not be found.");
            }
        }
    }
    
    /**
     * Returns the first value string found and moves the parser such that it will be on a valid value string. 
     * @param parser 
     */
    private String getNextValidString(JsonParser parser) throws IOException{
        String retVal = null;
        while(parser.currentToken() != null){
            
            switch (parser.currentToken()) {
                case END_ARRAY:
                    parser.clearCurrentToken();
                    return null;
                    
                //If we find a value string then we'll assume the next token is also a value string
                case VALUE_STRING:
                    retVal = parser.getText();
                    parser.nextToken();
                    return retVal;
                    
                //If we find a start object then return the first value string and run until an end object.
                case START_OBJECT:
                    while(parser.nextToken() != JsonToken.END_OBJECT){
                        if(retVal == null && parser.currentToken() == JsonToken.VALUE_STRING){
                            retVal = parser.getText();
                        }
                    }
                    return retVal;
                    
                //Look for the next value     
                default:               
                    parser.nextToken();

            }
        }
        return null;
    }
}
