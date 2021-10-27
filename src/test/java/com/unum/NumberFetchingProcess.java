package com.unum;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.IntStream;

class NumberFetchingProcess extends Thread{

    private Logger log=Logger.getLogger("NumberFetchingProcess");
    private int poolSize;
    private UniqueNumberGenerator generator;
    private List<Integer> acquiredNumbers=new ArrayList<>();

    public NumberFetchingProcess(int poolSize, UniqueNumberGenerator generator) {
        this.poolSize = poolSize;
        this.generator = generator;
    }


    public List<Integer> getAcquiredNumbers() {
        return acquiredNumbers;
    }

    @Override
    public void run() {
        try
        {
            IntStream.range(0,this.poolSize).forEach((e)->{
                int num= 0;
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
