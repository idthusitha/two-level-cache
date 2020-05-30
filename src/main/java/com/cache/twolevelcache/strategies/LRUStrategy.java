package com.cache.twolevelcache.strategies;

public class LRUStrategy<K> extends Storage<K> {

	@Override
	public void put(K key) {
		getObjectsStorage().put(key, System.nanoTime());
	}
}
