/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blakeparmeter.sndrcodechallenge;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import java.io.File;
import java.net.URL;
import java.util.Scanner;

/**
 *
 * @author Blake
 */
public class CorporaReader {
    
    public final long CORPORA_SIZE;
    public final String CORPORA_NAME;
    public final String CHECKSUM;
    private final JsonParser JSON_PARSER;
    
    public CorporaReader(File file) throws Exception{
        this(new JsonFactory().createParser(file));
    }
    
    public CorporaReader(URL url) throws Exception{
        Scanner s = new Scanner(url.openStream());
        System.out.println("Reading from URL:" + url);
        while(s.hasNext()){
            System.out.println(s.next());
        }
        
        CORPORA_SIZE = -1;
        CORPORA_NAME = null;
        CHECKSUM = null;
        JSON_PARSER = null;
        //this(new JsonFactory().createParser(url));
    }
    
    public CorporaReader(JsonParser parser) throws Exception{
        
        while(parser.nextFieldName() != null){
            System.out.println(parser.getCurrentName() + ":" + parser.getCurrentValue());
        }
        
        CORPORA_SIZE = -1;
        CORPORA_NAME = null;
        CHECKSUM = null;
        JSON_PARSER = parser;
    }
    
    public String getCorporaAtIndex(long index){
        return "test";
    }
}
