package com.unum.impl;



import com.unum.UniqueNumberGenerator;
import com.unum.exception.UnumException;

import java.util.concurrent.locks.ReentrantLock;

public class UniqueNumberGeneratorImpl implements UniqueNumberGenerator {

    private int generatorIdentifier;
    protected int counter=0;
    private int instance;
    protected int upperLimit;
    private ReentrantLock lock=new ReentrantLock();

    public UniqueNumberGeneratorImpl(int generatorIdentifier, int instance,int startPoint, int poolsize) throws UnumException {

        if(generatorIdentifier<1 || generatorIdentifier>MAX_IDENTIFIER_VALUE)
        {
            throw new UnumException("Identifier can be between 1 and "+MAX_IDENTIFIER_VALUE);
        }
        if(instance<0 || instance>INSTANCE_MAX_VALUE)
        {
            throw new UnumException("Instance number can range from 0 to "+INSTANCE_MAX_VALUE);
        }
        this.generatorIdentifier=generatorIdentifier;
        this.instance = instance;

        if(startPoint>0 && startPoint+poolsize<=COUNTER_MAX_VALUE)
        {
            this.counter=startPoint;
        }
        else if(startPoint>0 && startPoint+poolsize>COUNTER_MAX_VALUE)
        {
            throw new UnumException("the starting point should be greater than 0 and less than startpoint+poolsize less than "+COUNTER_MAX_VALUE);
        }


        if(poolsize>COUNTER_MAX_VALUE)
        {
            throw new UnumException("The pool size cannot be more than "+COUNTER_MAX_VALUE);
        }
        this.upperLimit=poolsize==-1?COUNTER_MAX_VALUE:poolsize;
    }

    protected int generate() throws UnumException {
        int retVal=generateInt();

        if(this.counter<this.upperLimit)
        {
            this.counter++;
        }
        else
        {
            throw new UnumException("Cannot produce more than the pool size of "+this.upperLimit);
        }
        return retVal;
    }

    private int generateInt()
    {
        int retVal=this.generatorIdentifier;
        retVal=retVal<<8;
        retVal=retVal|instance;
        retVal=retVal<<16;
        retVal=retVal|counter;
        return retVal;
    }

    public int getNext() throws UnumException {
        lock.lock();
        try
        {
            return generate();
        } catch (Exception e) {
            throw new UnumException(e.getMessage(),e);
        } finally {
            lock.unlock();
        }

    }


    @Override
    public void resumeFrom(int number) throws UnumException {
        lock.lock();
        try
        {
            resumeLogic(number);


        } catch (Exception e) {
            throw new UnumException(e.getMessage(),e);
        } finally {
            lock.unlock();
        }
    }

    protected void resumeLogic(int number)throws UnumException
    {
        int identifier= (int) (number>>24);
        int tinstance=number<<8;
        tinstance=tinstance>>24;
        int instance= (int) tinstance;
        int tempCounter=number;
        //System.out.println(displayInBinary(tempCounter));
        tempCounter=tempCounter<<16;
        //System.out.println(displayInBinary(tempCounter));
        tempCounter=tempCounter>>16;
        // System.out.println(displayInBinary(tempCounter));
        //this.counter=tempCounter;

        if(identifier==this.generatorIdentifier && instance==this.instance && tempCounter<this.upperLimit)
        {
            this.counter=tempCounter;
            this.counter++;
        }
        else
        {
            throw new UnumException("The identifier/instance/poolsize is not matching");
        }
    }
}
