# two-level-cache

Create in-memory cache (for caching Objects) with configurable max size and eviction strategy. Two strategies should be implemented: LRU and LFU. For this task it is assumed that only one thread will access the cache, so there is no need to make it thread-safe. Please provide an example of usage of the cache as a unit test(s).


Create a configurable two-level cache (for caching Objects).  Level 1 is memory, level 2 is file system.  Config params should let one specify the cache strategies and max sizes of level 1 and 2.