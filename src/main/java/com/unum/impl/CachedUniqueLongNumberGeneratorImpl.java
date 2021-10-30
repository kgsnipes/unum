package com.unum.impl;

import com.google.common.collect.EvictingQueue;
import com.unum.CachedUniqueLongNumberGenerator;
import com.unum.exception.UnumException;

import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CachedUniqueLongNumberGeneratorImpl extends UniqueLongNumberGeneratorImpl implements CachedUniqueLongNumberGenerator {


    private int cacheSize=DEFAULT_CACHE_SIZE;
    private Queue<Long> queue;
    private Lock lock=new ReentrantLock();

    public CachedUniqueLongNumberGeneratorImpl(int generatorIdentifier, int instance,long startPoint, long poolsize) throws UnumException {
        super(generatorIdentifier, instance,startPoint, poolsize);
        this.calculateCacheSize(poolsize);
        this.initQueue();
        this.fillQueue(this.cacheSize);
    }

    public CachedUniqueLongNumberGeneratorImpl(int generatorIdentifier, int instance,long startPoint, long poolsize,int cacheSize) throws UnumException {
        super(generatorIdentifier, instance,startPoint,  poolsize);
        this.cacheSize=cacheSize;
        this.initQueue();
        this.fillQueue(this.cacheSize);
    }

    public CachedUniqueLongNumberGeneratorImpl(long resumePoint,long poolsize) throws UnumException {
        super(resumePoint, poolsize);
        this.calculateCacheSize(poolsize);
        this.initQueue();
        this.fillQueue(this.cacheSize);
    }

    protected void calculateCacheSize(long poolsize)
    {
        if (poolsize < this.cacheSize)
        {
            this.cacheSize= (int) poolsize;
        }
    }

    private void initQueue()
    {
        this.queue= EvictingQueue.create(this.cacheSize);
    }

    private void fillQueue(int size) throws UnumException {

        try {

            for(int i=0;i<size;i++)
            {
                this.queue.add(this.generate());
            }

        }
        catch (Exception ex)
        {
            throw new UnumException(ex.getMessage(),ex);
        }

    }

    @Override
    public long getNext() throws UnumException {
        long retVal=-1l;
        lock.lock();
        try
        {
            Long headValue=this.queue.peek();
            if(!this.queue.isEmpty() && Objects.nonNull(headValue))
            {
                retVal=this.queue.remove();
                tryFillingTheQueue();
            }
            else
            {
                throw new UnumException("Not able to generate a number.");
            }

        } catch (Exception e) {
            throw new UnumException(e.getMessage(),e);
        } finally {
            lock.unlock();
        }
        return retVal;
    }

    private void tryFillingTheQueue() throws UnumException {


        if(this.queue.size()<this.cacheSize/2 && this.counter<this.upperLimit)
        {

            if(upperLimit-counter>cacheSize-queue.size())
            {
                fillQueue(cacheSize-queue.size());
            }
            else
            {
                fillQueue((int) (upperLimit-counter));
            }
        }
    }

    @Override
    public void resumeFrom(long number) throws UnumException {

        lock.lock();
        try
        {
            this.queue.clear();
            super.resumeLogic(number);
            fillQueue(this.cacheSize);
        } catch (Exception e) {
            throw new UnumException(e.getMessage(),e);
        } finally {
            lock.unlock();
        }
    }
}
