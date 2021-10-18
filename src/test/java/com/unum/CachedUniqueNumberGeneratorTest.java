package com.unum;

import com.unum.impl.CachedUniqueNumberGeneratorImpl;
import com.unum.impl.UniqueNumberGeneratorImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.logging.Logger;

public class CachedUniqueNumberGeneratorTest {

    private Logger log=Logger.getLogger("CachedUniqueNumberGeneratorTest");


    private UniqueNumberGenerator getGenerator(int identifier,int instance,int pool)
    {
        UniqueNumberGenerator generator=null;
        try
        {
            generator=new CachedUniqueNumberGeneratorImpl(identifier,instance,pool);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }

        return generator;
    }

    @Test
    public void getNextLongSingleThreadTest()throws  Exception
    {
        int poolSize=50000;
        NumberFetchingProcess process=new NumberFetchingProcess(poolSize,getGenerator(3001,1,poolSize));
        process.start();
        process.join();
        //log.info("Acquired Numbers "+process.getAcquiredNumbers().size());
        //log.info("Poolsize "+poolSize);
        Assertions.assertEquals(poolSize,process.getAcquiredNumbers().size(),"Not equal unique numbers");
    }
}
