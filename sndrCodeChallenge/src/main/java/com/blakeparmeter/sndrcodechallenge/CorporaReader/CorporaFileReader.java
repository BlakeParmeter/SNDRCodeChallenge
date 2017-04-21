/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blakeparmeter.sndrcodechallenge.CorporaReader;

import com.fasterxml.jackson.core.JsonFactory;
import java.io.File;

/**
 *
 * @author Blake
 */
public class CorporaFileReader extends CorporaReader{
    
    public CorporaFileReader(File file) throws Exception {
        super(new JsonFactory().createParser(file));
    }
}
