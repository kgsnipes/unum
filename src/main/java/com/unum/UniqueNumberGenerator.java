package com.unum;

public interface UniqueNumberGenerator {

    int COUNTER_MAX_VALUE=65535; //this value consumes 16 bits.
    int MAX_IDENTIFIER_VALUE=255;// upper limit 255 - 8 bits
    int INSTANCE_MAX_VALUE=255;//upper limit 128 - 8 bits

    int getNext() throws Exception;

    default String displayInBinary(int value)
    {
        int arr[]=new int[64];

        for(int i=arr.length-1;i>-1;i--)
        {
            arr[i]=value%2;
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
