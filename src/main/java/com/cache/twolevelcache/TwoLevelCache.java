package com.cache.twolevelcache;

import java.io.IOException;
import java.io.Serializable;

import com.cache.twolevelcache.strategies.LFUStrategy;
import com.cache.twolevelcache.strategies.LRUStrategy;
import com.cache.twolevelcache.strategies.MRUStrategy;
import com.cache.twolevelcache.strategies.Storage;

public class TwoLevelCache<K extends Serializable, V extends Serializable> implements Cache<K, V> {

	private final MemoryCache<K, V> firstLevelCache;
	private final FileSystemCache<K, V> secondLevelCache;
	private final Storage<K> strategy;

	public static final String LRU_STRATEGY = "LRU";
	public static final String LFU_STRATEGY = "LFU";
	public static final String MRU_STRATEGY = "MRU";

	public TwoLevelCache(final int memoryCapacity, final int fileCapacity, final String strategyType)
			throws IOException {
		this.firstLevelCache = new MemoryCache<>(memoryCapacity);
		this.secondLevelCache = new FileSystemCache<>(fileCapacity);
		this.strategy = getStrategy(memoryCapacity, strategyType);
	}

	public TwoLevelCache(final int memoryCapacity, final int fileCapacity) throws IOException {
		this.firstLevelCache = new MemoryCache<>(memoryCapacity);
		this.secondLevelCache = new FileSystemCache<>(fileCapacity);
		this.strategy = getStrategy(memoryCapacity, "LFU");
	}

	private Storage<K> getStrategy(int memoryCapacity, String strategyType) {
		switch (strategyType) {
		case LRU_STRATEGY:
			return new LRUStrategy<>();
		case LFU_STRATEGY:
			return new MRUStrategy<>();
		case MRU_STRATEGY:
		default:
			return new LFUStrategy<>();
		}
	}

	@Override
	public synchronized void put(K newKey, V newValue) throws IOException {
		if (firstLevelCache.isObjectPresent(newKey) || firstLevelCache.hasEmptyPlace()) {
			// log.debug(format("Put object with key %s to the 1st level", newKey));
			firstLevelCache.put(newKey, newValue);
			if (secondLevelCache.isObjectPresent(newKey)) {
				secondLevelCache.remove(newKey);
			}
		} else if (secondLevelCache.isObjectPresent(newKey) || secondLevelCache.hasEmptyPlace()) {
			// log.debug(format("Put object with key %s to the 2nd level", newKey));
			secondLevelCache.put(newKey, newValue);
		} else {
			// Here we have full cache and have to replace some object with new one
			// according to cache strategy.
			replaceObject(newKey, newValue);
		}

		if (!strategy.isObjectPresent(newKey)) {
			// log.debug(format("Put object with key %s to strategy", newKey));
			strategy.put(newKey);
		}
	}

	private void replaceObject(K key, V value) throws IOException {
		K replacedKey = strategy.getReplacedKey();
		if (firstLevelCache.isObjectPresent(replacedKey)) {
			// log.debug(format("Replace object with key %s from 1st level", replacedKey));
			firstLevelCache.remove(replacedKey);
			firstLevelCache.put(key, value);
		} else if (secondLevelCache.isObjectPresent(replacedKey)) {
			// log.debug(format("Replace object with key %s from 2nd level", replacedKey));
			secondLevelCache.remove(replacedKey);
			secondLevelCache.put(key, value);
		}
	}

	@Override
	public synchronized V get(K key) {
		if (firstLevelCache.isObjectPresent(key)) {
			strategy.put(key);
			return firstLevelCache.get(key);
		} else if (secondLevelCache.isObjectPresent(key)) {
			strategy.put(key);
			return secondLevelCache.get(key);
		}
		return null;
	}

	@Override
	public synchronized void remove(K key) {
		if (firstLevelCache.isObjectPresent(key)) {
			// log.debug(format("Remove object with key %s from 1st level", key));
			firstLevelCache.remove(key);
		}
		if (secondLevelCache.isObjectPresent(key)) {
			// log.debug(format("Remove object with key %s from 2nd level", key));
			secondLevelCache.remove(key);
		}
		strategy.remove(key);
	}

	@Override
	public int getSize() {
		return firstLevelCache.getSize() + secondLevelCache.getSize();
	}

	@Override
	public boolean isObjectPresent(K key) {
		return firstLevelCache.isObjectPresent(key) || secondLevelCache.isObjectPresent(key);
	}

	@Override
	public void clear() throws IOException {
		firstLevelCache.clear();
		secondLevelCache.clear();
		strategy.clear();
	}

	@Override
	public synchronized boolean hasEmptyPlace() {
		return firstLevelCache.hasEmptyPlace() || secondLevelCache.hasEmptyPlace();
	}

	public MemoryCache<K, V> getFirstLevelCache() {
		return firstLevelCache;
	}

	public FileSystemCache<K, V> getSecondLevelCache() {
		return secondLevelCache;
	}
	
	public Storage<K> getStrategy() {
		return strategy;
	}
}
