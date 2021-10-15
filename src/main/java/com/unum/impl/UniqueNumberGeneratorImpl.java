package com.unum.impl;

import com.unum.UniqueNumberGenerator;

import java.util.concurrent.locks.ReentrantLock;

public class UniqueNumberGeneratorImpl implements UniqueNumberGenerator {

    private int generatorIdentifier;// upper limit 35k - 16 binary places
    private int counter;
    private int instance;//upper limit 128 - 8 binary places
    private long upperLimit;
    private long COUNTER_MAX_VALUE=1099511627775l;
    private ReentrantLock lock=new ReentrantLock();

    public UniqueNumberGeneratorImpl(int generatorIdentifier,int instance) {
        this.generatorIdentifier=generatorIdentifier;
        this.instance = instance;
        this.upperLimit=COUNTER_MAX_VALUE/instance;
    }

    private long generate()
    {
        long retVal=generateLong();

        if(this.counter<this.upperLimit)
        {
            counter++;
        }
        return retVal;
    }

    private long generateLong()
    {
        long retVal=this.generatorIdentifier;
        displayLongInBinary(retVal);
        retVal=retVal<<8;
        displayLongInBinary(retVal);
        retVal=retVal|instance;
        displayLongInBinary(retVal);
        retVal=retVal<<40;
        displayLongInBinary(retVal);
        retVal=retVal|counter;
        displayLongInBinary(retVal);
        return retVal;
    }

    public long getNextLong() {

        lock.lock();
        try
        {
            return generate();
        }
        finally {
            lock.unlock();
        }

    }

    private void displayLongInBinary(long value)
    {
        int arr[]=new int[64];

        for(int i=arr.length-1;i>-1;i--)
        {
            arr[i]=(int)value%2;
            value=value/2;
        }

        for(int i=0;i<arr.length;i++)
        {
            System.out.print(arr[i]);
        }
        System.out.println();

    }

}
