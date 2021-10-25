package com.unum;

import com.unum.exception.UnumException;
import com.unum.impl.UniqueLongNumberGeneratorImpl;
import com.unum.impl.UniqueNumberGeneratorImpl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


import java.util.ArrayList;

import java.util.List;

import java.util.logging.Logger;
import java.util.stream.IntStream;


class UniqueLongNumberGeneratorTest {

    private Logger log=Logger.getLogger("UniqueNumberGeneratorTest");


    private UniqueLongNumberGenerator getGenerator(int identifier,int instance,long startPoint,long pool) throws Exception {
        return new UniqueLongNumberGeneratorImpl(identifier,instance,startPoint,pool);
    }


    @Test
     void getNextLongTest() throws Exception {
        UniqueLongNumberGenerator generator=getGenerator(1001,1,-1,1000);
        Assertions.assertNotEquals(-1l,generator.getNext());
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

        int poolSize=1000;
        UniqueLongNumberGenerator generator1=getGenerator(1001,1,-1,poolSize);
        UniqueLongNumberGenerator generator2=getGenerator(1002,1,-1,poolSize);
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


    @Test
     void getNextLongTestWithHigherStartPoint(){

        Assertions.assertThrows(UnumException.class,()->{
            UniqueLongNumberGenerator generator=getGenerator(1001,1,UniqueLongNumberGenerator.LONG_COUNTER_MAX_VALUE+1,1000);
        });


    }

    @Test
    void getNextLongTestWithIdentifierMoreThanLimit(){

        Assertions.assertThrows(UnumException.class,()->{
            UniqueLongNumberGenerator generator=getGenerator(35001,1,UniqueLongNumberGenerator.LONG_COUNTER_MAX_VALUE,1000);
        });


    }

    @Test
    void getNextLongTestWithIdentifierAndStartPointMoreThanLimit(){

        Assertions.assertThrows(UnumException.class,()->{
            UniqueLongNumberGenerator generator=getGenerator(35001,1,UniqueLongNumberGenerator.LONG_COUNTER_MAX_VALUE+1,1000);
        });


    }

    @Test
    void getNextLongTestWithPoolThanLimit(){

        Assertions.assertThrows(UnumException.class,()->{
            UniqueLongNumberGenerator generator=getGenerator(20000,1,1000,UniqueLongNumberGenerator.LONG_COUNTER_MAX_VALUE+1);
        });


    }

    @Test
    void getNextLongTestWithPoolAndStartPointMoreThanLimit(){

        Assertions.assertThrows(UnumException.class,()->{
            UniqueLongNumberGenerator generator=getGenerator(20000,1,UniqueLongNumberGenerator.LONG_COUNTER_MAX_VALUE,1);
        });


    }

    @Test
    void getNextLongTestWithInstanceMoreThanLimit(){

        Assertions.assertThrows(UnumException.class,()->{
            UniqueLongNumberGenerator generator=getGenerator(20000,300,1000,1);
        });


    }

    @Test
    void getNextLongTestWithAllParamsThanLimit(){

        Assertions.assertThrows(UnumException.class,()->{
            UniqueLongNumberGenerator generator=getGenerator(35001,300,UniqueLongNumberGenerator.LONG_COUNTER_MAX_VALUE+1,UniqueLongNumberGenerator.LONG_COUNTER_MAX_VALUE+1);
        });


    }

    @Test
    void resumeFromTest() throws Exception {
        UniqueLongNumberGenerator generator=getGenerator(1001,1,10,10000);
        generator.getNext();
        generator.getNext();
        long number=generator.getNext();
        long nextNumber=generator.getNext();
        generator.getNext();
        generator.resumeFrom(number);
        long testNumber=generator.getNext();
        Assertions.assertEquals(testNumber,nextNumber,"Values dont match");

    }

    @Test
    void resumeFromExceptionForIncorrectIdentifierTest() throws Exception {

        Assertions.assertThrows(UnumException.class,()->{
        UniqueLongNumberGenerator generator=getGenerator(1001,1,10,10000);
        UniqueLongNumberGenerator generator2=getGenerator(1002,1,10,10000);
        generator.resumeFrom(generator2.getNext());
        });

    }

    @Test
    void resumeFromExceptionForIncorrectInstanceTest() throws Exception {

        Assertions.assertThrows(UnumException.class,()->{
            UniqueLongNumberGenerator generator=getGenerator(1001,1,10,10000);
            UniqueLongNumberGenerator generator2=getGenerator(1001,2,10,10000);
            generator.resumeFrom(generator2.getNext());
        });

    }

    @Test
    void resumeFromExceptionForCrossingPoolsizeTest() throws Exception {

        Assertions.assertThrows(UnumException.class,()->{
            UniqueLongNumberGenerator generator=getGenerator(1001,1,1,3);
            generator.getNext();
            generator.getNext();
            long number=generator.getNext();
            generator.resumeFrom(number);
        });

    }

    @Test
    void resumeFromTestWithBulkOperation() throws Exception {
        UniqueLongNumberGenerator generator=getGenerator(1001,1,10,10000);
        for(int i=0;i<2000;i++)
        {
            generator.getNext();
        }
        long number=generator.getNext();
        long nextNumber=generator.getNext();

        for(int i=0;i<2000;i++)
        {
            generator.getNext();
        }
        generator.resumeFrom(number);
        long testNumber=generator.getNext();
        Assertions.assertEquals(testNumber,nextNumber,"Values dont match");

    }
}

