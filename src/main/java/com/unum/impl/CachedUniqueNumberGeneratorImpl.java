package com.unum.impl;

import com.google.common.collect.EvictingQueue;

import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Logger;
import java.util.stream.IntStream;

public class CachedUniqueNumberGeneratorImpl extends UniqueNumberGeneratorImpl{

    private final static Logger log=Logger.getLogger("CachedUniqueNumberGeneratorImpl");

    private int cacheSize=1000;
    private Queue<Long> queue;
    private ReadWriteLock lock=new ReentrantReadWriteLock();
    private Lock rLock=lock.readLock();
    private Lock wLock=lock.writeLock();
    public CachedUniqueNumberGeneratorImpl(int generatorIdentifier, int instance, int poolsize) throws Exception {
        super(generatorIdentifier, instance, poolsize);

        if (poolsize < this.cacheSize)
        {
            this.cacheSize=poolsize;
        }
        this.initQueue();
        this.fillQueue(this.cacheSize);
    }

    private void initQueue()
    {
        this.queue= EvictingQueue.create(this.cacheSize);
    }

    private void fillQueue(int size)
    {
       // this.wLock.lock();
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
           // this.wLock.unlock();
        }


    }

    @Override
    public long getNextLong() throws Exception {
        long retVal=-1l;
        wLock.lock();
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
            wLock.unlock();
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

//            //log.info("Trying to fill up the queue");
//            Thread t=new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        if(upperLimit-counter>cacheSize-queue.size())
//                        {
//                            log.info("fill 1");
//                            fillQueue(cacheSize-queue.size());
//                        }
//                        else
//                        {
//                            log.info("fill 2");
//                            fillQueue((int) (upperLimit-counter));
//                        }
//                       // log.info("Queue size is :"+queue.size());
//
//                            //log.info("The queue is filled up");
//                    }
//                    catch (Exception ex)
//                    {
//                        log.severe(ex.getMessage());
//                    }
//                }
//            });
//
//            t.start();
//            t.join();
        }
    }
}
