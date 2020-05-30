package com.cache.twolevelcache.strategies;

public class MRUStrategy<K> extends Storage<K> {
	@Override
	public void put(K key) {
		getObjectsStorage().put(key, System.nanoTime());
	}

	@Override
	public K getReplacedKey() {
		getSortedObjectsStorage().putAll(getObjectsStorage());
		return getSortedObjectsStorage().lastKey();
	}
}
