package com.cache.twolevelcache.strategies;

public class LFUStrategy<K> extends Storage<K> {

	@Override
	public void put(K key) {
		long frequency = 1;
		if (getObjectsStorage().containsKey(key)) {
			frequency = getObjectsStorage().get(key) + 1;
		}
		getObjectsStorage().put(key, frequency);
	}
}
