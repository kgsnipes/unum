package com.unum;

import com.unum.impl.UniqueLongNumberGeneratorImpl;
import com.unum.impl.UniqueNumberGeneratorImpl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


import java.util.ArrayList;

import java.util.List;

import java.util.logging.Logger;


public class UniqueLongNumberGeneratorTest {

    private Logger log=Logger.getLogger("UniqueNumberGeneratorTest");


    private UniqueLongNumberGenerator getGenerator(int identifier,int instance,int pool)
    {
        UniqueLongNumberGenerator generator=null;
        try
        {
            generator=new UniqueLongNumberGeneratorImpl(identifier,instance,pool);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }

        return generator;
    }


    @Test
    public void getNextLongTest() throws Exception {
        UniqueLongNumberGenerator generator=getGenerator(1001,1,1000);
        Assertions.assertNotNull(generator.getNextLong());
    }

    @Test
    public void getNextLongSingleThreadTest()throws  Exception
    {
        int poolSize=50000;
        LongNumberFetchingProcess process=new LongNumberFetchingProcess(poolSize,getGenerator(3001,1,poolSize));
       process.start();
       process.join();
       //log.info("Acquired Numbers "+process.getAcquiredNumbers().size());
       //log.info("Poolsize "+poolSize);
       Assertions.assertEquals(poolSize,process.getAcquiredNumbers().size(),"Not equal unique numbers");
    }

    @Test
    public void getNextLongWithSingleGeneratorAndMultipleThreadsTest()throws  Exception
    {
        int poolSize=45000;
        int distributedPool=15000;

        UniqueLongNumberGenerator generator=getGenerator(3001,1,poolSize);

        LongNumberFetchingProcess process1=new LongNumberFetchingProcess(distributedPool,generator);
        LongNumberFetchingProcess process2=new LongNumberFetchingProcess(distributedPool,generator);
        LongNumberFetchingProcess process3=new LongNumberFetchingProcess(distributedPool,generator);

        process1.start();
        process2.start();
        process3.start();

        process1.join();
        process2.join();
        process3.join();

        //log.info("Acquired Numbers "+process.getAcquiredNumbers().size());
        //log.info("Poolsize "+poolSize);
        Assertions.assertEquals(distributedPool,process1.getAcquiredNumbers().size(),"Not equal unique numbers");
        Assertions.assertEquals(distributedPool,process2.getAcquiredNumbers().size(),"Not equal unique numbers");
        Assertions.assertEquals(distributedPool,process3.getAcquiredNumbers().size(),"Not equal unique numbers");
    }

    @Test
    public void getNextLongMultipleThreadsTest()throws  Exception
    {

        int poolSize=1000;
        UniqueLongNumberGenerator generator1=getGenerator(1001,1,poolSize);
        UniqueLongNumberGenerator generator2=getGenerator(1002,1,poolSize);
        LongNumberFetchingProcess process1=new LongNumberFetchingProcess(poolSize,generator1);
        LongNumberFetchingProcess process2=new LongNumberFetchingProcess(poolSize,generator2);
        process1.start();
        process2.start();
        process1.join();
        process2.join();
        log.info("Acquired Numbers "+process1.getAcquiredNumbers().size());
        log.info("Acquired Numbers "+process2.getAcquiredNumbers().size());

        Assertions.assertEquals(process1.getAcquiredNumbers().size(),poolSize,"Not equal unique numbers");
        Assertions.assertEquals(process2.getAcquiredNumbers().size(),poolSize,"Not equal unique numbers");
    }
}

