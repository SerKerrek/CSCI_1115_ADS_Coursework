
/* Author: Renee Linford
 * Date: 10-29-19
 * ADS Exercise 27-3: Implement MyMap using open addressing with double hashing.
 */

public class Exercise27_3 {
	// Modify a class that implements MyMap so that it uses open addressing with double hashing.

	/** Main method */
	public static void main(String[] args) {
		MyHashMap<Integer, Integer> map = new MyHashMap<>();
		map.put(2, 2);
		System.out.println("Is key 2 in the map? " + map.containsKey(2));
		map.remove(2);
		System.out.println("Is key 2 in the map? " + map.containsKey(2));
	}

	static class MyHashMap<K, V> implements MyMap<K, V> {
		// Define the default hash table size. 
		private static int DEFAULT_INITIAL_CAPACITY = 4;

		// Define the maximum hash table size. 1 << 30 is same as 2^30
		private static int MAXIMUM_CAPACITY = 1 << 30; 

		// Current hash table capacity. 
		private int capacity;

		// Define default load factor
		private static float DEFAULT_MAX_LOAD_FACTOR = 0.5f; 

		// Specify a load factor used in the hash table
		private float loadFactorThreshold; 

		// The number of entries in the map
		private int size = 0;

		// Hash table is an array with each cell that is a linked list
		MyMap.Entry<K,V>[] table;

		/** Construct a map with the default capacity and load factor */
		public MyHashMap() {  
			this(DEFAULT_INITIAL_CAPACITY, DEFAULT_MAX_LOAD_FACTOR);    
		}

		/** Construct a map with the specified initial capacity and 
		 * default load factor */
		public MyHashMap(int initialCapacity) { 
			this(initialCapacity, DEFAULT_MAX_LOAD_FACTOR);    
		}

		/** Construct a map with the specified initial capacity 
		 * and load factor */
		public MyHashMap(int initialCapacity, float loadFactorThreshold) { 
			this.capacity = initialCapacity;     
			this.loadFactorThreshold = loadFactorThreshold;    
			table = new MyMap.Entry[capacity];
		}

		/** Remove all of the entries from this map */ 
		public void clear() {
			size = 0;
			removeEntries();
		}

		/** Return true if the specified key is in the map */
		public boolean containsKey(K key) {
			if (get(key) != null)
				return true;
			else
				return false;
		}

		/** Return true if this map contains the specified value */ 
		public boolean containsValue(V value) {
			for (int i = 0; i < table.length; i++)
				if (table[i] != null && table[i].value.equals(value)) 
					return true;

			return false;
		}

		/** Return a set of entries in the map */
		public java.util.Set<MyMap.Entry<K,V>> entrySet() {
			java.util.Set<MyMap.Entry<K, V>> set = 
					new java.util.HashSet<MyMap.Entry<K, V>>();

			for (int i = 0; i < capacity; i++) 
				if (table[i] != null) 
					set.add(table[i]); 

			return set;
		}

		// Return the first value that matches the specified key */
		public V get(K key) {
			// Perform double hashing.
			int k = hash(key.hashCode());

			int i = k;
			// int j = 1;
			while (table[i].key != null) {
				if (table[i].key.equals(key)) 
					return table[i].value;
				// Double hashing
				// hash1(key) + j * hash2(key)) % TABLE_SIZE
				i = ((k % size) + i * (7 - k % 7)) % table.length; // Modified table.length to never have zero value.
			}

			return null;
		}

		/** Return all values for the specified key in this map */
		public java.util.Set<V> getAll(K key) {
			java.util.Set<V> set = new java.util.HashSet<V>();

			for (int i = 0; i < capacity; i++) 
				if (table[i] != null && table[i].key.equals(key)) 
					set.add(table[i].value); 

			return set;
		}

		/** Return true if this map contains no entries */
		public boolean isEmpty() {
			return size == 0;
		}  

		/** Return a set consisting of the keys in this map */
		public java.util.Set<K> keySet() {
			java.util.Set<K> set = new java.util.HashSet<K>();

			for (int i = 0; i < capacity; i++) 
				if (table[i] != null) 
					set.add(table[i].key); 

			return set;
		}

		// Add an entry (key, value) into the map */
		public V put(K key, V value) {
			if (size >= capacity * loadFactorThreshold) {
				if (capacity == MAXIMUM_CAPACITY)
					throw new RuntimeException("Exceeding maximum capacity");

				rehash();
			}

			int k = hash(key.hashCode());      
			// int j = 1;
			int i = k;
			while (table[i] != null && table[i].key != null) {
				// Double hashing
				// hash1(key) + i * hash2(key)) % TABLE_SIZE
				i = ((k % size) + i * (7 - k % 7)) % table.length; // Modified table.length to never have zero value.
			}

			// Add an entry (key, value) to the table
			table[i] = new MyMap.Entry<K, V>(key, value);

			size++; // Increase size

			return value;  
		} 

		// Remove the element for the specified key */
		public void remove(K key) {
			int k = hash(key.hashCode());
			// int j = 1;
			int i = k;
			while (table[i] != null && (table[i].key == null || !table[i].key.equals(key))) {
				i = ((k % size) + i * (7 - k % 7)) % table.length; // Modified table.length to never have zero value.
			}

			if (table[i] != null && table[i].key.equals(key)) {
				// A special marker Entry(null, null) is placed for the deleted entry
				table[i] = new MyMap.Entry<K, V>(null, null);
				size--;
			}
		}

		/** Return the number of mappings in this map */
		public int size() {
			return size;
		}

		/** Return a set consisting of the values in this map */
		public java.util.Set<V> values() {
			java.util.Set<V> set = new java.util.HashSet<V>();

			for (int i = 0; i < capacity; i++) 
				if (table[i] != null) 
					set.add(table[i].value); 

			return set;
		}

		/* Hash function 
		 * @param <E>*/
		private int hash(int hashCode) {
			int key = hashCode % capacity;
			return key;
			// supplementalHash(hashCode) & (capacity - 1); 
		}

		/** Ensure the hashing is evenly distributed */
		private static int supplementalHash(int h) {
			// g(key) = prime - key % prime
			h ^= (h >>> 20) ^ (h >>> 12);
			return h ^ (h >>> 7) ^ (h >>> 4);
		}

		/** Remove all entries from each bucket */
		private void removeEntries() {
			for (int i = 0; i < capacity; i++) 
				table[i] = null;
		}

		/** Rehash the map */
		private void rehash() {
			java.util.Set<Entry<K, V>> set = entrySet(); // Get entries
			capacity <<= 1; // Double capacity    
			table = new Entry[capacity]; // Create a new hash table

			size = 0; // Clear size

			for (Entry<K, V> entry: set) {
				put(entry.getKey(), entry.getValue()); // Store to new table
			}
		}

		@Override /** Return a string representation for this map */
		public String toString() {
			StringBuilder builder = new StringBuilder("[");

			for (int i = 0; i < capacity; i++) {
				if (table[i] != null && table[i].key != null) 
					builder.append(table[i].toString());
			}

			return builder.append("]").toString();
		}
	}

	interface MyMap<K, V> {
		/** Remove all of the entries from this map */ 
		public void clear();

		/** Return true if the specified key is in the map */
		public boolean containsKey(K key);

		/** Return true if this map contains the specified value */ 
		public boolean containsValue(V value);

		/** Return a set of entries in the map */
		public java.util.Set<Entry<K, V>> entrySet();

		/** Return the first value that matches the specified key */
		public V get(K key);

		/** Return all values for the specified key in this map */
		public java.util.Set<V> getAll(K key);

		/** Return true if this map contains no entries */
		public boolean isEmpty();

		/** Return a set consisting of the keys in this map */
		public java.util.Set<K> keySet();

		/** Add an entry (key, value) into the map */
		public V put(K key, V value);

		/** Remove the entries for the specified key */
		public void remove(K key);

		/** Return the number of mappings in this map */
		public int size();

		/** Return a set consisting of the values in this map */
		public java.util.Set<V> values();

		/** Define inner class for Entry */
		public static class Entry<K, V> {
			K key;
			V value;

			public Entry(K key, V value) {
				this.key = key;
				this.value = value;
			}

			public K getKey() {
				return key;
			}

			public V getValue() {
				return value;
			}

			@Override
			public String toString() {
				return "[" + key + ", " + value + "]";
			}
		}
	}    
}
