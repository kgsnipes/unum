package com.unum;

import com.unum.exception.UnumException;
import com.unum.impl.UniqueNumberGeneratorImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;
import java.util.logging.Logger;
import java.util.stream.IntStream;

public class UniqueNumberGeneratorImplTest {
    private Logger log=Logger.getLogger("UniqueNumberGeneratorImplTest");

    private UniqueNumberGenerator getGenerator(int identifier,int instance,int startPoint,int pool) throws Exception {
        return new UniqueNumberGeneratorImpl(identifier,instance,startPoint,pool);
    }

    private UniqueNumberGenerator getGenerator(int resume,int pool) throws Exception {
        return new UniqueNumberGeneratorImpl(resume,pool);
    }

    @Test
    void getNextTest() throws Exception {
        UniqueNumberGenerator generator=getGenerator(123,1,-1,1000);
        Assertions.assertNotEquals(-1l,generator.getNext());
    }

    @Test
    void getNextSingleThreadTest()throws  Exception
    {
        int poolSize=50_000;
        NumberFetchingProcess process=new NumberFetchingProcess(poolSize,getGenerator(123,1,-1,poolSize));
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

        UniqueNumberGenerator generator=getGenerator(200,1,-1,poolSize);

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
    void getNextLongMultipleThreadsTest()throws  Exception
    {

        int poolSize=1000;
        UniqueNumberGenerator generator1=getGenerator(201,1,-1,poolSize);
        UniqueNumberGenerator generator2=getGenerator(202,1,-1,poolSize);
        NumberFetchingProcess process1=new NumberFetchingProcess(poolSize,generator1);
        NumberFetchingProcess process2=new NumberFetchingProcess(poolSize,generator2);
        process1.start();
        process2.start();
        process1.join();
        process2.join();
        log.info("Acquired Numbers "+process1.getAcquiredNumbers().size());
        log.info("Acquired Numbers "+process2.getAcquiredNumbers().size());

        Assertions.assertEquals(process1.getAcquiredNumbers().size(),poolSize,"Not equal unique numbers");
        Assertions.assertEquals(process2.getAcquiredNumbers().size(),poolSize,"Not equal unique numbers");
    }


    @Test
    void getNextLongTestWithHigherStartPoint(){

        Assertions.assertThrows(UnumException.class,()->{
            UniqueNumberGenerator generator=getGenerator(101,1,UniqueNumberGenerator.COUNTER_MAX_VALUE+1,1000);
        });


    }

    @Test
    void getNextLongTestWithIdentifierMoreThanLimit(){

        Assertions.assertThrows(UnumException.class,()->{
            UniqueNumberGenerator generator=getGenerator(35001,1,UniqueNumberGenerator.COUNTER_MAX_VALUE,1000);
        });


    }

    @Test
    void getNextLongTestWithIdentifierAndStartPointMoreThanLimit(){

        Assertions.assertThrows(UnumException.class,()->{
            UniqueNumberGenerator generator=getGenerator(35001,1,UniqueNumberGenerator.COUNTER_MAX_VALUE+1,1000);
        });


    }

    @Test
    void getNextLongTestWithPoolThanLimit(){

        Assertions.assertThrows(UnumException.class,()->{
            UniqueNumberGenerator generator=getGenerator(20000,1,1000,UniqueNumberGenerator.COUNTER_MAX_VALUE+1);
        });


    }

    @Test
    void getNextLongTestWithPoolAndStartPointMoreThanLimit(){

        Assertions.assertThrows(UnumException.class,()->{
            UniqueNumberGenerator generator=getGenerator(20000,1,UniqueNumberGenerator.COUNTER_MAX_VALUE,1);
        });


    }

    @Test
    void getNextLongTestWithInstanceMoreThanLimit(){

        Assertions.assertThrows(UnumException.class,()->{
            UniqueNumberGenerator generator=getGenerator(20000,300,1000,1);
        });


    }

    @Test
    void getNextLongTestWithAllParamsThanLimit(){

        Assertions.assertThrows(UnumException.class,()->{
            UniqueNumberGenerator generator=getGenerator(35001,300,UniqueNumberGenerator.COUNTER_MAX_VALUE+1,UniqueNumberGenerator.COUNTER_MAX_VALUE+1);
        });


    }

    @Test
    void resumeFromTest() throws Exception {
        UniqueNumberGenerator generator=getGenerator(101,1,10,10000);
        generator.getNext();
        generator.getNext();
        int number=generator.getNext();
        int nextNumber=generator.getNext();
        generator.getNext();
        generator.resumeFrom(number);
        int testNumber=generator.getNext();
        Assertions.assertEquals(testNumber,nextNumber,"Values dont match");

    }

    @Test
    void resumeFromExceptionForIncorrectIdentifierTest() throws Exception {

        Assertions.assertThrows(UnumException.class,()->{
            UniqueNumberGenerator generator=getGenerator(101,1,10,10000);
            UniqueNumberGenerator generator2=getGenerator(102,1,10,10000);
            generator.resumeFrom(generator2.getNext());
        });

    }

    @Test
    void resumeFromExceptionForIncorrectInstanceTest() throws Exception {

        Assertions.assertThrows(UnumException.class,()->{
            UniqueNumberGenerator generator=getGenerator(100,1,10,10000);
            UniqueNumberGenerator generator2=getGenerator(100,2,10,10000);
            generator.resumeFrom(generator2.getNext());
        });

    }

    @Test
    void resumeFromExceptionForCrossingPoolsizeTest() throws Exception {

        Assertions.assertThrows(UnumException.class,()->{
            UniqueNumberGenerator generator=getGenerator(100,1,1,3);
            generator.getNext();
            generator.getNext();
            int number=generator.getNext();
            generator.resumeFrom(number);
        });

    }

    @Test
    void resumeFromTestWithBulkOperation() throws Exception {
        UniqueNumberGenerator generator=getGenerator(100,1,10,10000);
        for(int i=0;i<2000;i++)
        {
            generator.getNext();
        }
        int number=generator.getNext();
        int nextNumber=generator.getNext();

        for(int i=0;i<2000;i++)
        {
            generator.getNext();
        }
        generator.resumeFrom(number);
        long testNumber=generator.getNext();
        Assertions.assertEquals(testNumber,nextNumber,"Values dont match");

    }

    @Test
    void constructorBasedResume()throws Exception{

        UniqueNumberGenerator generator=getGenerator(100,1,10,10000);
        for(int i=0;i<2000;i++)
        {
            generator.getNext();
        }

        int number=generator.getNext();
        int nextNumber=generator.getNext();

        UniqueNumberGenerator generator2=getGenerator(number,10000);
        Assertions.assertEquals(generator2.getNext(),nextNumber,"Values dont match");

    }

    @Test
    void constructorBasedResumeTest2()throws Exception{

        UniqueNumberGenerator generator=getGenerator(100,1,10,10000);
        for(int i=0;i<2000;i++)
        {
            generator.getNext();
        }

        int number=generator.getNext();

        UniqueNumberGenerator generator2=getGenerator(number,10000);
        Assertions.assertNotEquals(generator2.getNext(),number,"Values dont match");

    }

    @Test
    @Disabled
    void numberGenerationCheckWithLotsOfThreads()throws Exception
    {
        int threads=100;
        int numbersToFetch=UniqueNumberGenerator.COUNTER_MAX_VALUE/threads;
        ExecutorService executorService=Executors.newFixedThreadPool(threads);
        List<Future<List<Integer>>> futures=new ArrayList<>();
        UniqueNumberGenerator generator=getGenerator(1,0,0,-1);
        Callable<List<Integer>> numberConsumingTask=()->{

            log.info("Fetching "+numbersToFetch+" numbers.");
            List<Integer> numbersFetched=new ArrayList<>();
            for(int i=0;i<numbersToFetch;i++) {
                numbersFetched.add(generator.getNext());
            }
            log.info("Done");
            return numbersFetched;

        };

        for(int i=0;i<threads;i++)
        {
            futures.add(executorService.submit(numberConsumingTask));
        }

        int count=0;

        for(Future<List<Integer>> f :futures)
        {
            List<Integer> result=f.get();
            if(f.isDone())
            {
                if(result.size()<numbersToFetch)
                {
                    Assertions.fail("Was not able to fetch enough numbers "+result.size());
                }
                count++;
            }

        }

        Assertions.assertEquals(100,count,"All threads did not execute");
        Assertions.assertEquals(35,UniqueNumberGenerator.COUNTER_MAX_VALUE-(numbersToFetch*threads),"The remaining do not match");
    }


    @Test
    void writeToDb()throws Exception
    {
        Connection conn = DriverManager.getConnection("jdbc:h2:~/test");
        Statement statement=
        conn.close();
    }

}
