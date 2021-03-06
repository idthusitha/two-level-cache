package com.cache.twolevelcache;

import java.io.IOException;
import java.io.Serializable;

import com.cache.twolevelcache.strategies.LFUStrategy;
import com.cache.twolevelcache.strategies.LRUStrategy;
import com.cache.twolevelcache.strategies.MRUStrategy;
import com.cache.twolevelcache.strategies.Storage;

/**
 * 
 * @author thusitha
 *
 * @param <K>
 * @param <V>
 */
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
			firstLevelCache.put(newKey, newValue);
			if (secondLevelCache.isObjectPresent(newKey)) {
				secondLevelCache.remove(newKey);
			}
		} else if (secondLevelCache.isObjectPresent(newKey) || secondLevelCache.hasEmptyPlace()) {
			secondLevelCache.put(newKey, newValue);
		} else {
			replaceObject(newKey, newValue);
		}

		if (!strategy.isObjectPresent(newKey)) {
			strategy.put(newKey);
		}
	}

	private void replaceObject(K key, V value) throws IOException {
		K replacedKey = strategy.getReplacedKey();
		if (firstLevelCache.isObjectPresent(replacedKey)) {
			firstLevelCache.remove(replacedKey);
			firstLevelCache.put(key, value);
		} else if (secondLevelCache.isObjectPresent(replacedKey)) {
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
			firstLevelCache.remove(key);
		}
		if (secondLevelCache.isObjectPresent(key)) {
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
