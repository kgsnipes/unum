package com.unum;

import com.unum.impl.CachedUniqueNumberGeneratorImpl;
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

    private UniqueNumberGenerator getGeneratorWithCacheSize(int identifier,int instance,int pool,int cacheSize)
    {
        UniqueNumberGenerator generator=null;
        try
        {
            generator=new CachedUniqueNumberGeneratorImpl(identifier,instance,pool,cacheSize);
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

    @Test
    public void getNextLongWithSingleGeneratorAndMultipleThreadsTest()throws  Exception
    {
        int poolSize=45000;
        int distributedPool=15000;

        UniqueNumberGenerator generator=getGenerator(3001,1,poolSize);

        NumberFetchingProcess process1=new NumberFetchingProcess(distributedPool,generator);
        NumberFetchingProcess process2=new NumberFetchingProcess(distributedPool,generator);
        NumberFetchingProcess process3=new NumberFetchingProcess(distributedPool,generator);

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

        int poolSize=50000;
        UniqueNumberGenerator generator1=getGenerator(1001,1,poolSize);
        UniqueNumberGenerator generator2=getGenerator(1002,1,poolSize);
        UniqueNumberGenerator generator3=getGenerator(1003,1,poolSize);

        NumberFetchingProcess process1=new NumberFetchingProcess(poolSize,generator1);
        NumberFetchingProcess process2=new NumberFetchingProcess(poolSize,generator2);
        NumberFetchingProcess process3=new NumberFetchingProcess(poolSize,generator3);

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
    public void getNextLongSingleThreadWithCacheSizeTest()throws  Exception
    {
        int poolSize=50000;
        NumberFetchingProcess process=new NumberFetchingProcess(poolSize,getGeneratorWithCacheSize(3001,1,poolSize,2000));
        process.start();
        process.join();
        //log.info("Acquired Numbers "+process.getAcquiredNumbers().size());
        //log.info("Poolsize "+poolSize);
        Assertions.assertEquals(poolSize,process.getAcquiredNumbers().size(),"Not equal unique numbers");
    }

    @Test
    public void getNextLongWithSingleGeneratorAndMultipleThreadsWithCacheSizeTest()throws  Exception
    {
        int poolSize=45000;
        int distributedPool=15000;

        UniqueNumberGenerator generator=getGeneratorWithCacheSize(3001,1,poolSize,2000);

        NumberFetchingProcess process1=new NumberFetchingProcess(distributedPool,generator);
        NumberFetchingProcess process2=new NumberFetchingProcess(distributedPool,generator);
        NumberFetchingProcess process3=new NumberFetchingProcess(distributedPool,generator);

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
    public void getNextLongMultipleThreadsWithCacheSizeTest()throws  Exception
    {

        int poolSize=50000;
        UniqueNumberGenerator generator1=getGeneratorWithCacheSize(1001,1,poolSize,2000);
        UniqueNumberGenerator generator2=getGeneratorWithCacheSize(1002,1,poolSize,2000);
        UniqueNumberGenerator generator3=getGeneratorWithCacheSize(1003,1,poolSize,2000);

        NumberFetchingProcess process1=new NumberFetchingProcess(poolSize,generator1);
        NumberFetchingProcess process2=new NumberFetchingProcess(poolSize,generator2);
        NumberFetchingProcess process3=new NumberFetchingProcess(poolSize,generator3);

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
