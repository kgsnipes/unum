package com.unum.impl;



import com.unum.UniqueNumberGenerator;

import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

public class UniqueNumberGeneratorImpl implements UniqueNumberGenerator {

    private final static Logger log=Logger.getLogger("UniqueNumberGeneratorImpl");

    private int generatorIdentifier;// upper limit 35k - 16 bits
    protected int counter;
    private int instance;//upper limit 128 - 8 bits
    protected int upperLimit;
    //private long LONG_LONG_COUNTER_MAX_VALUE=1099511627775l; //this value consumes 40 bits.
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
        //displayLongInBinary(retVal);
        retVal=retVal<<8;
        //displayLongInBinary(retVal);
        retVal=retVal|instance;
       // displayLongInBinary(retVal);
        retVal=retVal<<8;
       // displayLongInBinary(retVal);
        retVal=retVal|counter;
       displayInBinary(retVal);
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

//    private void displayInBinary(long value)
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
