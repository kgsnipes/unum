package com.unum;

import com.unum.impl.UniqueNumberGeneratorImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;


public class UniqueNumberGeneratorTest {

    private Logger log=Logger.getLogger("UniqueNumberGeneratorTest");


    private UniqueNumberGenerator getGenerator(int identifier,int instance,int pool)
    {
        UniqueNumberGenerator generator=null;
        try
        {
            generator=new UniqueNumberGeneratorImpl(identifier,instance,pool);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }

        return generator;
    }


    @Test
    public void getNextLongTest() throws Exception {
        UniqueNumberGenerator generator=getGenerator(1001,1,1000);
        Assertions.assertNotNull(generator.getNextLong());
    }

    @Test
    public void getNextLongSingleThreadTest()throws  Exception
    {
        int poolSize=50000;
       NumberFetchingProcess process=new NumberFetchingProcess(poolSize,getGenerator(3001,1,poolSize));
       process.start();
       process.join();
       log.info("Acquired Numbers "+process.getAcquiredNumbers().size());
       log.info("Poolsize "+poolSize);
       Assertions.assertEquals(process.getAcquiredNumbers().size(),poolSize,"Not equal unique numbers");
    }

    @Test
    public void getNextLongMultipleThreadsTest()throws  Exception
    {

        int poolSize=1000;
        UniqueNumberGenerator generator1=getGenerator(1001,1,poolSize);
        UniqueNumberGenerator generator2=getGenerator(1002,1,poolSize);
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
