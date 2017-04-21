/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.blakeparmeter.sndrcodechallenge.Application;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Blake
 */
public class UniquenessTest {
    
    private static final int NUM_THREADS = 4;
    private final ExecutorService THREAD_POOL = Executors.newFixedThreadPool(NUM_THREADS);
    private final long TEST_DURATION = 20 * 1000;//ms
        
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        Application.main(new String[]{
            "https://raw.githubusercontent.com/dariusk/corpora/master/data/words/adjs.json", 
            "https://raw.githubusercontent.com/dariusk/corpora/master/data/colors/crayola.json", 
            "https://raw.githubusercontent.com/dariusk/corpora/master/data/humans/occupations.json"});       
    }
    
    @Test
    public void testForUnique() {
        
        //Start the application
        
        long start = System.currentTimeMillis();
        Set<String> values = Collections.synchronizedSet(new HashSet(10_000_000));
        for(int i = 0; i < NUM_THREADS; i++){
            THREAD_POOL.submit(new DisparateCorporaTestRunnable(values));
        }
        
        try{
            Thread.sleep(TEST_DURATION);
            THREAD_POOL.shutdown();
            long numRecords = values.size();
            long duration = System.currentTimeMillis() - start;
            System.out.println("The test has run for " + duration / 1000 + "." + duration%1000+" seconds.");
            System.out.println("~"+numRecords / 1000 + "k phrases were generated.");
            System.out.println((numRecords/(duration/1000)/1000) + "k phrases / sec.");
        }catch(Exception ex){
            
        }
    }
    
    private class DisparateCorporaTestRunnable implements Runnable{

        private final Set<String> commonHashSet;
        
        public DisparateCorporaTestRunnable(Set<String> set){
            commonHashSet = set;
        }
        
        @Override
        public void run() {
            try{
                
                //adds to the hashset
                String text = Application.getNextDisparateCorpora();
                if(commonHashSet.add(text) == false){
                    System.err.println("There has been a collision of values: " + text);
                }
                
                //increments the counter and prints a status message
                if(commonHashSet.size() % 10000 == 0){
                    System.out.println((commonHashSet.size() / 1000) + "k phrases have been generated.");
                }
            }catch(Exception ex){
                ex.printStackTrace(System.err);
            }finally{
                THREAD_POOL.submit(new DisparateCorporaTestRunnable(commonHashSet));
            }
        }
    }
}
