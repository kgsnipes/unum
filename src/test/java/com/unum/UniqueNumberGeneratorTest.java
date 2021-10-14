package com.unum;

import com.unum.impl.UniqueNumberGeneratorImpl;
import org.junit.jupiter.api.Test;


public class UniqueNumberGeneratorTest {

    UniqueNumberGenerator generator=new UniqueNumberGeneratorImpl(1001,1);

    @Test
    public void getNextLongTest()
    {
        generator.getNextLong();
    }
}
