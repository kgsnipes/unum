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

        this.validateArguments(generatorIdentifier,instance,startPoint,poolsize);
    }

    public UniqueNumberGeneratorImpl(int resumePoint,int poolsize)  throws UnumException {

        int identifier=this.extractIdentifier(resumePoint);
        int inst=this.extractInstance(resumePoint);
        int count=this.extractCounter(resumePoint);
        this.validateArguments(identifier,inst,count+1,poolsize);

    }

    protected void validateArguments(int generatorIdentifier, int instance,int startPoint, int poolsize) throws UnumException
    {
        if(generatorIdentifier<1 || generatorIdentifier>MAX_IDENTIFIER_VALUE)
        {
            throw new UnumException("Identifier can be between 1 and "+MAX_IDENTIFIER_VALUE);
        }
        if(instance<0 || instance>INSTANCE_MAX_VALUE)
        {
            throw new UnumException("Instance number can range from 0 to "+INSTANCE_MAX_VALUE);
        }

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

        this.generatorIdentifier=generatorIdentifier;
        this.instance = instance;
        this.upperLimit=poolsize==-1?COUNTER_MAX_VALUE:poolsize;
    }

    protected int generate() throws UnumException {
        int retVal=generateInt(this.generatorIdentifier,this.instance,this.counter);

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

    /**
     * This method performs the bit packing to generate the next number based on identifier, instance and counter.
     * @param identifier
     * @param instance
     * @param counter
     * @return
     */
    private int generateInt(int identifier,int instance, int counter)
    {
        int retVal=identifier;
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

    @Override
    public int getUpperLimit() throws UnumException {
        lock.lock();
        try
        {
            return this.generateInt(this.generatorIdentifier,this.instance,this.upperLimit);
        } catch (Exception e) {
            throw new UnumException(e.getMessage(),e);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int getCapacityAvailable() throws UnumException {
        lock.lock();
        try
        {
            return this.upperLimit-this.counter;
        } catch (Exception e) {
            throw new UnumException(e.getMessage(),e);
        } finally {
            lock.unlock();
        }
    }

    protected void resumeLogic(int number)throws UnumException
    {
        int identifier= this.extractIdentifier(number);
        int tinstance=this.extractInstance(number);
        int tempCounter=this.extractCounter(number);
        tempCounter=tempCounter<<16;
        tempCounter=tempCounter>>16;

        if(identifier==this.generatorIdentifier && tinstance==this.instance && tempCounter<this.upperLimit)
        {
            this.counter=tempCounter;
            this.counter++;
        }
        else
        {
            throw new UnumException("The identifier/instance/poolsize is not matching");
        }
    }

    protected int extractIdentifier(int number)
    {
        return number>>24;
    }

    protected int extractInstance(int number)
    {
        int tinstance=number<<8;
        tinstance=tinstance>>24;
        return tinstance;
    }

    protected int extractCounter(int number)
    {
        int tempCounter=number;
        tempCounter=tempCounter<<16;
        tempCounter=tempCounter>>16;
        return tempCounter;
    }

}
