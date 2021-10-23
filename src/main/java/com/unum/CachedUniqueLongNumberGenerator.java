package com.unum;

/**
 * @author kaushik.ganguly
 * This defines the cached long number generator.
 * The default cache size is a 1000 numbers.
 */
public interface CachedUniqueLongNumberGenerator extends UniqueLongNumberGenerator {

    int DEFAULT_CACHE_SIZE=1000;

}
