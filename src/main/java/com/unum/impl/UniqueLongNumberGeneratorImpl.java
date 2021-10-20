package com.unum.impl;

import com.unum.UniqueLongNumberGenerator;

import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

public class UniqueLongNumberGeneratorImpl implements UniqueLongNumberGenerator {

    private final static Logger log=Logger.getLogger("UniqueNumberGeneratorImpl");

    private int generatorIdentifier;// upper limit 35k - 16 bits
    protected int counter;
    private int instance;//upper limit 128 - 8 bits
    protected long upperLimit;
    //private long LONG_LONG_COUNTER_MAX_VALUE=1099511627775l; //this value consumes 40 bits.
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
        //displayLongInBinary(retVal);
        retVal=retVal<<8;
        //displayLongInBinary(retVal);
        retVal=retVal|instance;
       // displayLongInBinary(retVal);
        retVal=retVal<<40;
       // displayLongInBinary(retVal);
        retVal=retVal|counter;
       // displayLongInBinary(retVal);
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

//    private void displayLongInBinary(long value)
//    {
//        int arr[]=new int[64];
//
//        for(int i=arr.length-1;i>-1;i--)
//        {
//            arr[i]=(int)value%2;
//            value=value/2;
//        }
//
//        StringBuilder builder=new StringBuilder();
//
//
//        for(int i=0;i<arr.length;i++)
//        {
//            builder.append(arr[i]==1?"1":"0");
//        }
//        log.info(builder.toString());
//
//    }

}
