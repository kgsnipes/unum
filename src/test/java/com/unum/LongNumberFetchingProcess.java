package com.unum;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

class LongNumberFetchingProcess extends Thread{

    private Logger log=Logger.getLogger("NumberFetchingProcess");
    private long poolSize;
    private UniqueLongNumberGenerator generator;
    private List<Long> acquiredNumbers=new ArrayList<>();

    public LongNumberFetchingProcess(long poolSize, UniqueLongNumberGenerator generator) {
        this.poolSize = poolSize;
        this.generator = generator;
    }


    public List<Long> getAcquiredNumbers() {
        return acquiredNumbers;
    }

    @Override
    public void run() {
        try
        {
            LongStream.range(0,this.poolSize).forEach((e)->{
                long num= 0;
                try {
                    num = this.generator.getNext();
                } catch (Exception ex) {
                    log.severe(ex.getMessage());
                    ex.printStackTrace();
                }
                //FileUtils.write(new File("numbers.txt"),num+"\n","utf-8",true);
                if(!this.acquiredNumbers.contains(num))
                {
                    this.acquiredNumbers.add(num);
                    //log.info("Fetching next number : "+ this.generator.getNextLong());
                    // Thread.sleep(100);
                    this.poolSize--;
                }
                else
                {
                    log.info("Number exists "+ num);
                    return;
                }
            });
        }
        catch (Exception ex)
        {
            log.severe(ex.getMessage());
            ex.printStackTrace();
        }
    }
}
