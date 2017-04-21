/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blakeparmeter.sndrcodechallenge.CorporaReader;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ValueNode;
import java.io.IOException;

/**
 * Reads the Json Parser.
 * @author Blake
 */
public abstract class CorporaReader {
    
    public final int CORPORA_SIZE;
    public final TreeNode ROOT_NODE;  
    
    public CorporaReader(JsonParser parser) throws Exception{
        moveParserToStartOfFirstArray(parser);
        ObjectMapper mapper = new ObjectMapper();
        ROOT_NODE = mapper.readTree(parser);   
        CORPORA_SIZE = ROOT_NODE.size();
    }
    
    /**
     * Gets the index form the Root Node found in the constructor
     * @param index The index to get
     * @return The value at the index
     */
    public String getCorporaAtIndex(long index){
        TreeNode val = ROOT_NODE.get((int) index);
        if(val.isValueNode()){
            return ((ValueNode)val).asText();
        }else if(val.isObject()){
            return ((ValueNode)val.get(val.fieldNames().next())).asText();
        }else{
            throw new RuntimeException("JSON Token type: " + val.asToken() + " is not supported.");
        }
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
}
