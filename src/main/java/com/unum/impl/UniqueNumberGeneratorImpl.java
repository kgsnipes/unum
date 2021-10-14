package com.unum.impl;

import com.unum.UniqueNumberGenerator;

import java.util.concurrent.locks.ReentrantLock;

public class UniqueNumberGeneratorImpl implements UniqueNumberGenerator {

    private int instanceNumber=1;
    private int generatorIdentifier;
    private int counter=0;
    private int upperLimit=Integer.MAX_VALUE;
    private int lowerLimit=Integer.MIN_VALUE;
    private ReentrantLock lock=new ReentrantLock();

    public UniqueNumberGeneratorImpl(int generatorIdentifier,int instanceNumber) {
        this.generatorIdentifier=generatorIdentifier;
        this.instanceNumber = instanceNumber;
        this.upperLimit=Integer.MAX_VALUE/instanceNumber;

    }

    private long generate()
    {
        long retVal=this.generatorIdentifier;
        displayLongInBinary(retVal);
        retVal=retVal<<32;
        displayLongInBinary(retVal);
        retVal=retVal|counter;
        displayLongInBinary(retVal);
        if(this.counter<this.upperLimit)
        {
            counter++;
        }
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
