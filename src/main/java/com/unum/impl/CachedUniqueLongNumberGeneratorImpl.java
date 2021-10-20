package com.unum.impl;

import com.google.common.collect.EvictingQueue;

import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;
import java.util.stream.IntStream;

public class CachedUniqueLongNumberGeneratorImpl extends UniqueLongNumberGeneratorImpl{

    private final static Logger log=Logger.getLogger("CachedUniqueNumberGeneratorImpl");

    private int cacheSize=1000;
    private Queue<Long> queue;
    private Lock lock=new ReentrantLock();

    public CachedUniqueLongNumberGeneratorImpl(int generatorIdentifier, int instance, int poolsize) throws Exception {
        super(generatorIdentifier, instance, poolsize);

        if (poolsize < this.cacheSize)
        {
            this.cacheSize=poolsize;
        }
        this.initQueue();
        this.fillQueue(this.cacheSize);
    }

    public CachedUniqueLongNumberGeneratorImpl(int generatorIdentifier, int instance, int poolsize,int cacheSize) throws Exception {
        super(generatorIdentifier, instance, poolsize);
        this.cacheSize=cacheSize;
        this.initQueue();
        this.fillQueue(this.cacheSize);
    }

    private void initQueue()
    {
        this.queue= EvictingQueue.create(this.cacheSize);
    }

    private void fillQueue(int size)
    {

        try {
            IntStream.range(0,size).forEach((e)->{
                try {
                    this.queue.add(this.generate());
                } catch (Exception ex) {
                    log.severe(ex.getMessage());
                }
            });
        }
        catch (Exception ex)
        {
            log.severe(ex.getMessage());
        }
        finally {

        }


    }

    @Override
    public long getNextLong() throws Exception {
        long retVal=-1l;
        lock.lock();
        try
        {
            Long headValue=this.queue.peek();
            if(this.queue.size()>0 && Objects.nonNull(headValue))
            {
                retVal=this.queue.remove();
                tryFillingTheQueue();
                //log.info("Getting a long value (counter) :"+this.counter);
            }
            else
            {
                //System.out.println("hello");
                throw new Exception("Not able to generate a number.");
            }

        } catch (Exception e) {
            throw e;
        } finally {
            lock.unlock();
        }
        return retVal;
    }

    private void tryFillingTheQueue() throws InterruptedException {


        if(this.queue.size()<this.cacheSize/2 && this.counter<this.upperLimit)
        {

            if(upperLimit-counter>cacheSize-queue.size())
            {
                //log.info("fill 1 - "+(cacheSize-queue.size()));
                fillQueue(cacheSize-queue.size());
            }
            else
            {
                //log.info("fill 2 - "+(upperLimit-counter));
                fillQueue((int) (upperLimit-counter));
            }
        }
    }
}
