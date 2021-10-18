package com.unum;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

class NumberFetchingProcess extends Thread{

    private Logger log=Logger.getLogger("NumberFetchingProcess");
    private long poolSize;
    private UniqueNumberGenerator generator;
    private List<Long> acquiredNumbers=new ArrayList<>();

    public NumberFetchingProcess(long poolSize, UniqueNumberGenerator generator) {
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
            while(this.poolSize>0)
            {
                long num=this.generator.getNextLong();
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
                    break;
                }

            }
        }
        catch (Exception ex)
        {
            log.severe(ex.getMessage());
            ex.printStackTrace();
        }
    }
}
