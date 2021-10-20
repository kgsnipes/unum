package com.unum;

public interface UniqueLongNumberGenerator {

    long LONG_COUNTER_MAX_VALUE=1099511627775l; //this value consumes 40 bits.
    long LONG_MAX_IDENTIFIER_VALUE=35000;// upper limit 35k - 16 bits
    long LONG_INSTANCE_MAX_VALUE=128;//upper limit 128 - 8 bits

    long getNextLong() throws Exception;

    default String displayInBinary(long value)
    {
        int arr[]=new int[64];

        for(int i=arr.length-1;i>-1;i--)
        {
            arr[i]=(int)value%2;
            value=value/2;
        }

        StringBuilder builder=new StringBuilder();


        for(int i=0;i<arr.length;i++)
        {
            builder.append(arr[i]==1?"1":"0");
        }
        return builder.toString();
    }
}
