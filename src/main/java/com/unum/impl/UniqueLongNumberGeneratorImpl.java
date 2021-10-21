package com.unum.impl;

import com.unum.UniqueLongNumberGenerator;
import java.util.concurrent.locks.ReentrantLock;


public class UniqueLongNumberGeneratorImpl implements UniqueLongNumberGenerator {

    private int generatorIdentifier;
    protected int counter;
    private int instance;
    protected long upperLimit;
    private ReentrantLock lock=new ReentrantLock();

    public UniqueLongNumberGeneratorImpl(int generatorIdentifier,int instance,int poolsize) throws Exception {
        if(generatorIdentifier<1 || generatorIdentifier>LONG_MAX_IDENTIFIER_VALUE)
        {
            throw new Exception("Identifier can be between 1 and 35,000");
        }
        if(instance<0 || instance>LONG_INSTANCE_MAX_VALUE)
        {
            throw new Exception("Instance number can range from 0 to 128");
        }
        this.generatorIdentifier=generatorIdentifier;
        this.instance = instance;

        if(poolsize>LONG_COUNTER_MAX_VALUE)
        {
            throw new Exception("The pool size cannot be more than "+LONG_COUNTER_MAX_VALUE);
        }
        this.upperLimit=poolsize==-1?LONG_COUNTER_MAX_VALUE:poolsize;
    }

    protected long generate() throws Exception {
        long retVal=generateLong();

        if(this.counter<this.upperLimit)
        {
            this.counter++;
        }
        else
        {
            throw new Exception("Cannot produce more than the pool size of "+this.upperLimit);
        }
        return retVal;
    }

    private long generateLong()
    {
        long retVal=this.generatorIdentifier;
        retVal=retVal<<8;
        retVal=retVal|instance;
        retVal=retVal<<40;
        retVal=retVal|counter;
        return retVal;
    }

    public long getNextLong() throws Exception {
        try
        {
            lock.lock();
            return generate();
        } catch (Exception e) {
            throw e;
        } finally {
            lock.unlock();
        }

    }

}
