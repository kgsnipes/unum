package com.unum;

import com.unum.exception.UnumException;

/**
 * @author kaushik.ganguly
 * The UniqueLongNumberGenerator interface defines methods and constraints that are applicable for using this generator.
 *  There can be a max of 128 instances for a given identifier.
 *  There can be a max of 35000 or 35k as the generator identifier.
 *  There can be a max of 1099511627775 or 1.9 trillion numbers that can be generated from the generator.
 */
public interface UniqueLongNumberGenerator {

    long LONG_COUNTER_MAX_VALUE=1099511627775l; //this value consumes 40 bits.
    long LONG_MAX_IDENTIFIER_VALUE=35000;// upper limit 35k - 16 bits
    long LONG_INSTANCE_MAX_VALUE=128;//upper limit 128 - 8 bits


    long getNext() throws UnumException;

    void resumeFrom(long number) throws UnumException;

    /**
     * this helps print numbers in binary representation
     * @param value
     * @return a string binary representation of the number
     */
    default String displayInBinary(long value)
    {
        int[] arr=new int[64];
        StringBuilder builder=new StringBuilder();
        for(int i=arr.length-1;i>-1;i--)
        {
            arr[i]=(int)value%2;
            value=value/2;
            builder.append(arr[i]==1?"1":"0");
        }
        return builder.reverse().toString();
    }
}
