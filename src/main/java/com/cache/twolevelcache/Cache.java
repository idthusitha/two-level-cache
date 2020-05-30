package com.cache.twolevelcache;

interface Cache<K, V> {
	void put(K key, V value) throws Exception;

	V get(K key);

	void remove(K key);

	int getSize();

	boolean isObjectPresent(K key);

	boolean hasEmptyPlace();

	void clear() throws Exception;
}
