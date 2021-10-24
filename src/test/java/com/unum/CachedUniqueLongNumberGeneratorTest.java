package com.unum;

import com.unum.impl.CachedUniqueLongNumberGeneratorImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.logging.Logger;

class CachedUniqueLongNumberGeneratorTest {

    private Logger log=Logger.getLogger("CachedUniqueNumberGeneratorTest");


    private UniqueLongNumberGenerator getGenerator(int identifier,int instance,int startpoint,int pool)
    {
        UniqueLongNumberGenerator generator=null;
        try
        {
            generator=new CachedUniqueLongNumberGeneratorImpl(identifier,instance,startpoint,pool);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }

        return generator;
    }

    private UniqueLongNumberGenerator getGeneratorWithCacheSize(int identifier,int instance,int startpoint,int pool,int cacheSize)
    {
        UniqueLongNumberGenerator generator=null;
        try
        {
            generator=new CachedUniqueLongNumberGeneratorImpl(identifier,instance,startpoint,pool,cacheSize);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }

        return generator;
    }

    @Test
     void getNextLongSingleThreadTest()throws  Exception
    {
        int poolSize=50000;
        LongNumberFetchingProcess process=new LongNumberFetchingProcess(poolSize,getGenerator(3001,1,-1,poolSize));
        process.start();
        process.join();
        //log.info("Acquired Numbers "+process.getAcquiredNumbers().size());
        //log.info("Poolsize "+poolSize);
        Assertions.assertEquals(poolSize,process.getAcquiredNumbers().size(),"Not equal unique numbers");
    }

    @Test
     void getNextLongWithSingleGeneratorAndMultipleThreadsTest()throws  Exception
    {
        int poolSize=45000;
        int distributedPool=15000;

        UniqueLongNumberGenerator generator=getGenerator(3001,1,-1,poolSize);

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
     void getNextLongMultipleThreadsTest()throws  Exception
    {

        int poolSize=50000;
        UniqueLongNumberGenerator generator1=getGenerator(1001,1,-1,poolSize);
        UniqueLongNumberGenerator generator2=getGenerator(1002,1,-1,poolSize);
        UniqueLongNumberGenerator generator3=getGenerator(1003,1,-1,poolSize);

        LongNumberFetchingProcess process1=new LongNumberFetchingProcess(poolSize,generator1);
        LongNumberFetchingProcess process2=new LongNumberFetchingProcess(poolSize,generator2);
        LongNumberFetchingProcess process3=new LongNumberFetchingProcess(poolSize,generator3);

        process1.start();
        process2.start();
        process3.start();

        process1.join();
        process2.join();
        process3.join();

        log.info("Acquired Numbers "+process1.getAcquiredNumbers().size());
        log.info("Acquired Numbers "+process2.getAcquiredNumbers().size());
        log.info("Acquired Numbers "+process3.getAcquiredNumbers().size());

        Assertions.assertEquals(process1.getAcquiredNumbers().size(),poolSize,"Not equal unique numbers");
        Assertions.assertEquals(process2.getAcquiredNumbers().size(),poolSize,"Not equal unique numbers");
        Assertions.assertEquals(process3.getAcquiredNumbers().size(),poolSize,"Not equal unique numbers");
    }

    @Test
     void getNextLongSingleThreadWithCacheSizeTest()throws  Exception
    {
        int poolSize=50000;
        LongNumberFetchingProcess process=new LongNumberFetchingProcess(poolSize,getGeneratorWithCacheSize(3001,1,-1,poolSize,2000));
        process.start();
        process.join();
        //log.info("Acquired Numbers "+process.getAcquiredNumbers().size());
        //log.info("Poolsize "+poolSize);
        Assertions.assertEquals(poolSize,process.getAcquiredNumbers().size(),"Not equal unique numbers");
    }

    @Test
     void getNextLongWithSingleGeneratorAndMultipleThreadsWithCacheSizeTest()throws  Exception
    {
        int poolSize=45000;
        int distributedPool=15000;

        UniqueLongNumberGenerator generator=getGeneratorWithCacheSize(3001,1,-1,poolSize,2000);

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
     void getNextLongMultipleThreadsWithCacheSizeTest()throws  Exception
    {

        int poolSize=50000;
        UniqueLongNumberGenerator generator1=getGeneratorWithCacheSize(1001,1,-1,poolSize,2000);
        UniqueLongNumberGenerator generator2=getGeneratorWithCacheSize(1002,1,-1,poolSize,2000);
        UniqueLongNumberGenerator generator3=getGeneratorWithCacheSize(1003,1,-1,poolSize,2000);

        LongNumberFetchingProcess process1=new LongNumberFetchingProcess(poolSize,generator1);
        LongNumberFetchingProcess process2=new LongNumberFetchingProcess(poolSize,generator2);
        LongNumberFetchingProcess process3=new LongNumberFetchingProcess(poolSize,generator3);

        process1.start();
        process2.start();
        process3.start();

        process1.join();
        process2.join();
        process3.join();

        log.info("Acquired Numbers "+process1.getAcquiredNumbers().size());
        log.info("Acquired Numbers "+process2.getAcquiredNumbers().size());
        log.info("Acquired Numbers "+process3.getAcquiredNumbers().size());

        Assertions.assertEquals(process1.getAcquiredNumbers().size(),poolSize,"Not equal unique numbers");
        Assertions.assertEquals(process2.getAcquiredNumbers().size(),poolSize,"Not equal unique numbers");
        Assertions.assertEquals(process3.getAcquiredNumbers().size(),poolSize,"Not equal unique numbers");
    }
}
