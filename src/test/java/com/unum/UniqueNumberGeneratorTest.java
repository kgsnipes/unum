package com.unum;

import com.unum.impl.UniqueNumberGeneratorImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;


public class UniqueNumberGeneratorTest {

    private Logger log=Logger.getLogger("UniqueNumberGeneratorTest");

    UniqueNumberGenerator generator=new UniqueNumberGeneratorImpl(1001,1);

    @Test
    public void getNextLongTest()
    {
        Assertions.assertNotNull(generator.getNextLong());
    }

    @Test
    public void getNextLongSingleThreadTest()throws  Exception
    {
        int poolSize=50000;
       NumberFetchingProcess process=new NumberFetchingProcess(poolSize,generator);
       process.start();
       process.join();
       log.info("Acquired Numbers "+process.getAcquiredNumbers().size());
       log.info("Poolsize "+poolSize);
       Assertions.assertEquals(process.getAcquiredNumbers().size(),poolSize,"Not equal unique numbers");
    }
}

class NumberFetchingProcess extends Thread{

    private Logger log=Logger.getLogger("NumberFetchingProcess");
    private long poolSize;
    private UniqueNumberGenerator generator;
    private Set<Long> acquiredNumbers=new HashSet<>();

    public NumberFetchingProcess(long poolSize, UniqueNumberGenerator generator) {
        this.poolSize = poolSize;
        this.generator = generator;
    }


    public Set<Long> getAcquiredNumbers() {
        return acquiredNumbers;
    }

    @Override
    public void run() {
        try
        {
            while(this.poolSize>0)
            {
                long num=this.generator.getNextLong();
                this.acquiredNumbers.add(num);
                log.info("Fetching next number : "+ this.generator.getNextLong());
               // Thread.sleep(100);
                this.poolSize--;
            }
        }
        catch (Exception ex)
        {
            log.severe(ex.getMessage());
            ex.printStackTrace();
        }
    }
}
