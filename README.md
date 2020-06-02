# Two Level Cache System

Create a configurable two-level cache (for caching Objects).  Level 1 is memory, level 2 is file system.  Config params should let one specify the cache strategies and max sizes of level 1 and 2.


### High-level architecture diagram of the system, identifying all major components

![Test Image 1](https://github.com/idthusitha/two-level-cache/blob/master/doc/two-level-cache.png)


### Prerequisites:
   * Java 1.8
   * Gradle 5.0
   * Sprint Boot 2.2.7 
   * Junit Testing
   
   
### API 1.0.0

Constuctor definition:
TwoLevelCache(int memoryCapacity, int fileCapacity)

	TwoLevelCache twoLevelCache = new TwoLevelCache<>(1, 1);
	String VALUE1 = "value1";
	twoLevelCache.put(0, VALUE1);
	twoLevelCache.get(0); // Output should be "value1"
	twoLevelCache.getSize();  // Output should be "1"
	twoLevelCache.remove(0);
	twoLevelCache.get(0); // Output should be null



