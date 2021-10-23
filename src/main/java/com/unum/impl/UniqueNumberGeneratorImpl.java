package com.unum.impl;



import com.unum.UniqueNumberGenerator;
import java.util.concurrent.locks.ReentrantLock;

public class UniqueNumberGeneratorImpl implements UniqueNumberGenerator {

    private int generatorIdentifier;
    protected int counter;
    private int instance;
    protected int upperLimit;
    private ReentrantLock lock=new ReentrantLock();

    public UniqueNumberGeneratorImpl(int generatorIdentifier, int instance, int poolsize) throws Exception {

        if(generatorIdentifier<1 || generatorIdentifier>MAX_IDENTIFIER_VALUE)
        {
            throw new Exception("Identifier can be between 1 and 35,000");
        }
        if(instance<0 || instance>INSTANCE_MAX_VALUE)
        {
            throw new Exception("Instance number can range from 0 to 128");
        }
        this.generatorIdentifier=generatorIdentifier;
        this.instance = instance;

        if(poolsize>COUNTER_MAX_VALUE)
        {
            throw new Exception("The pool size cannot be more than "+COUNTER_MAX_VALUE);
        }
        this.upperLimit=poolsize==-1?COUNTER_MAX_VALUE:poolsize;
    }

    protected int generate() throws Exception {
        int retVal=generateLong();

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

    private int generateLong()
    {
        int retVal=this.generatorIdentifier;
        retVal=retVal<<8;
        retVal=retVal|instance;
        retVal=retVal<<8;
        retVal=retVal|counter;
        return retVal;
    }

    public int getNext() throws Exception {
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
