import java.util.Iterator;
import java.util.LinkedList;

public class HashMap<K, V> implements Map61BL<K, V> {

    LinkedList<Entry>[] hMap;
    int size;
    float loadFactor = (float) 0.75;

    public HashMap() {
        hMap = (LinkedList[]) new LinkedList[16];
        loadFactor = (float) 0.75;
        size = 0;
        for (int i = 0; i < 16; i++) {
            hMap[i] = new LinkedList<Entry>();
        }
    }

    HashMap(int initialCapacity) {
        hMap = (LinkedList[]) new LinkedList[initialCapacity];
        size = 0;
        for (int i = 0; i < initialCapacity; i++) {
            hMap[i] = new LinkedList<Entry>();
        }
    }

    HashMap(int initialCapacity, float loadFactor) {
        hMap = (LinkedList[]) new LinkedList[initialCapacity];
        this.loadFactor = loadFactor;
        size = 0;
        for (int i = 0; i < initialCapacity; i++) {
            hMap[i] = new LinkedList<Entry>();
        }
    }

    public int size() {
        return size;
    }

    int capacity() {
        return hMap.length;
    }
    /* Returns true if the given KEY is a valid name that starts with A - Z. */
//    private static boolean isValidName(String key) {
//        return 'A' <= key.charAt(0) && key.charAt(0) <= 'Z';
//    }

    /* Returns true if the map contains the KEY. */
    public boolean containsKey(K key) {
//        if (isValidName(key)) {
        Entry getCode = new Entry(key, null);
        LinkedList<Entry> lis = hMap[Math.floorMod(key.hashCode(), hMap.length)];
        if (lis != null) {
            for (int i = 0; i < lis.size(); i++) {
                if (lis.get(i).keyEquals(getCode)) {
                    return true;
                }
            }
        }
//        }
        return false;
    }

    /* Returns the value for the specified KEY. If KEY is not found, return
       null. */
    public V get(K key) {
//        if (isValidName(key)) {
        Entry getCode = new Entry(key, null);
        LinkedList<Entry> lis = hMap[Math.floorMod(key.hashCode(), hMap.length)];
        if (lis != null) {
            for (int i = 0; i < lis.size(); i++) {
                if (lis.get(i).keyEquals(getCode)) {
                    return (V) lis.get(i).value;
                }
            }
//            }
        }
        return null;
    }

    /* Puts a (KEY, VALUE) pair into this map. If the KEY already exists in the
       SimpleNameMap, replace the current corresponding value with VALUE. */
    public void put(K key, V value) {
//        if (isValidName(key)) {
        Entry getCode = new Entry(key, value);
        LinkedList<Entry> lis = hMap[Math.floorMod(key.hashCode(), hMap.length)];
        if (lis.size() == 0) {
//                hMap[Math.floorMod(key.hashCode(), hMap.length)] = new LinkedList<>();
            lis.addFirst(getCode);
            size++;
//                System.out.println(this.size()*1.0/this.capacity()*1.00);
            if (this.size() * 1.0 / this.capacity() * 1.00 > loadFactor) {
                this.resize();
            }
            return;
        }
        for (int i = 0; i < lis.size(); i++) {
            if (lis.get(i).keyEquals(getCode)) {
                lis.remove(i);
                lis.add(i, getCode);
                return;
            }
        }
        size++;
        lis.addLast(getCode);
        if (this.size() / hMap.length > loadFactor) {
            this.resize();
        }
//        }
    }

    void resize() {
        LinkedList<Entry>[] newlis = new LinkedList[hMap.length * 2];
        for (LinkedList<Entry> en : hMap) {
            for (int i = 0; i < en.size(); i++) {
                if (newlis[Math.floorMod(en.get(i).hashCode(), newlis.length)] == null) {
                    newlis[Math.floorMod(en.get(i).hashCode(), newlis.length)] =
                            new LinkedList<Entry>();
                }
                newlis[Math.floorMod(en.get(i).hashCode(), newlis.length)].addLast(en.get(i));
            }
        }
        hMap = newlis;
    }

    /* Removes a single entry, KEY, from this table and return the VALUE if
       successful or NULL otherwise. */
    @Override
    public V remove(K key) {
//        if (isValidName(key)) {
        Entry getCode = new Entry(key, null);
        LinkedList<Entry> lis = hMap[Math.floorMod(key.hashCode(), hMap.length)];
        if (lis != null) {
            for (int i = 0; i < lis.size(); i++) {
                if (lis.get(i).keyEquals(getCode)) {
                    V toReturn = (V) lis.get(i).value;
                    lis.remove(i);
                    size--;
                    return toReturn;
                }
            }
        }

//        }
        return null;
    }

    public void clear() {
        for (int i = 0; i < hMap.length; i++) {
            hMap[i] = null;
        }
        size = 0;
    }

    public boolean remove(K key, V value) {
        Entry getCode = new Entry(key, null);
        LinkedList<Entry> lis = hMap[Math.floorMod(key.hashCode(), hMap.length)];
        if (lis != null) {
            for (int i = 0; i < lis.size(); i++) {
                if (lis.get(i).keyEquals(getCode)) {
                    //                        V toReturn = (V) lis.get(i).value;
                    lis.remove(i);
                    size--;
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Iterator<K> iterator() {
        return new HashMapIterator();
    }

    private static class Entry<K, V> {

        private K key;
        private V value;

        Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        /* Returns true if this key matches with the OTHER's key. */
        public boolean keyEquals(Entry other) {
            return key.equals(other.key);
        }

        /* Returns true if both the KEY and the VALUE match. */
        @Override
        public boolean equals(Object other) {
            return (other instanceof Entry
                    && key.equals(((Entry) other).key)
                    && value.equals(((Entry) other).value));
        }

        @Override
        public int hashCode() {
            return key.hashCode();
        }
    }

    class HashMapIterator<K> implements Iterator<K> {
        int list;
        int index;
        int count;
//            LinkedList<Entry>[] iter;

        public HashMapIterator() {
//                iter = hMap;
            list = 0;
            index = 0;
            count = 0;
        }

        public boolean hasNext() {
            return count < size();
        }

        public K next() {
            if (hasNext()) {
//                    System.out.println(hMap[list].get(index).key);
                while (hMap[list].size() == 0) {
                    list++;
                }
                K toReturn = (K) hMap[list].get(index).key;
                index++;
                if (index >= hMap[list].size()) {
                    list++;
                    index = 0;
                }
                count++;
                return toReturn;
            }
            return null;
        }
    }


}
