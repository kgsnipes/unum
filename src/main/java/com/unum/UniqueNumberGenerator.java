package com.unum;

import com.unum.exception.UnumException;

/**
 * @author kaushik.ganguly
 * The UniqueNumberGenerator interface defines methods and constraints that are applicable for using this generator.
 * There can be a max of 255 instances for a given identifier.
 * There can be a max of 255 as the generator identifier.
 * There can be a max of 65535 or 65k numbers that can be generated from the generator.
 */
public interface UniqueNumberGenerator {

    int COUNTER_MAX_VALUE=65535; //this value consumes 16 bits.
    int MAX_IDENTIFIER_VALUE=255;// upper limit 255 - 8 bits
    int INSTANCE_MAX_VALUE=255;//upper limit 128 - 8 bits

    /**
     * This method fetches the next generated Integer number.
     * @return Next Integer Number
     * @throws UnumException
     */
    int getNext() throws UnumException;

    /**
     * We can use this to resume the number generation from a specific generated number.
     * @param number
     * @throws UnumException
     */
    void resumeFrom(int number) throws UnumException;

    /**
     * getting the Maximum numbers that can be generated in the particular instance.
     * @return
     * @throws UnumException
     */
    int getUpperLimit()throws UnumException;

    /**
     * returns the current available numbers that can be generated in the particular instance.
     * @return
     * @throws UnumException
     */
    int getCapacityAvailable()throws UnumException;

    /**
     * this helps print numbers in binary representation
     * @param value
     * @return a string binary representation of the number
     */
    default String displayInBinary(int value)
    {
        int[] arr=new int[32];
        StringBuilder builder=new StringBuilder();

        for(int i=arr.length-1;i>-1;i--)
        {
            arr[i]=value%2;
            value=value/2;
            builder.append(arr[i]==1?"1":"0");
        }

        return builder.reverse().toString();
    }
}
