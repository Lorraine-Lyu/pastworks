
import java.util.LinkedList;

public class SimpleNameMap {
    LinkedList<Entry>[] nameMap;
    int size;
    Double loadFactor;
    /* Instance variables here? */


    public SimpleNameMap(int cap, Double load) {
        nameMap = (LinkedList[]) new LinkedList[cap];
        loadFactor = load;
        size = 0;
    }

    int size() {
        return size;
    }
    /* Returns true if the given KEY is a valid name that starts with A - Z. */
    private static boolean isValidName(String key) {
        return 'A' <= key.charAt(0) && key.charAt(0) <= 'Z';
    }

    /* Returns true if the map contains the KEY. */
    boolean containsKey(String key) {
        if (isValidName(key)) {
            Entry getCode = new Entry(key, null);
            LinkedList<Entry> lis = nameMap[Math.floorMod(key.hashCode(), nameMap.length)];
            if (lis != null) {
                for (int i = 0; i < lis.size(); i++) {
                    if (lis.get(i).keyEquals(getCode)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /* Returns the value for the specified KEY. If KEY is not found, return
       null. */
    String get(String key) {
        if (isValidName(key)) {
            Entry getCode = new Entry(key, null);
            LinkedList<Entry> lis = nameMap[Math.floorMod(key.hashCode(), nameMap.length)];
            if (lis != null) {
                for (int i = 0; i < lis.size(); i++) {
                    if (lis.get(i).keyEquals(getCode)) {
                        return lis.get(i).value;
                    }
                }
            }
        }
        return null;
    }

    /* Puts a (KEY, VALUE) pair into this map. If the KEY already exists in the
       SimpleNameMap, replace the current corresponding value with VALUE. */
    void put(String key, String value) {
        if (isValidName(key)) {
            Entry getCode = new Entry(key, value);
            LinkedList<Entry> lis = nameMap[Math.floorMod(key.hashCode(), nameMap.length)];
            if (lis == null) {
                lis = new LinkedList<Entry>();
                lis.addLast(getCode);
                size++;
                if (this.size() / nameMap.length > loadFactor) {
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

        }
    }

    void resize() {
        LinkedList<Entry>[] newlis = new LinkedList[nameMap.length * 2];
        for (LinkedList<Entry> en : nameMap) {
            for (int i = 0; i < en.size(); i++) {
                if (newlis[Math.floorMod(en.get(i).hashCode(), newlis.length)] == null) {
                    newlis[Math.floorMod(en.get(i).hashCode(), newlis.length)] =
                            new LinkedList<Entry>();
                }
                newlis[Math.floorMod(en.get(i).hashCode(), newlis.length)].addLast(en.get(i));
            }
        }
    }
    /* Removes a single entry, KEY, from this table and return the VALUE if
       successful or NULL otherwise. */
    String remove(String key) {
        if (isValidName(key)) {
            Entry getCode = new Entry(key, null);
            LinkedList<Entry> lis = nameMap[Math.floorMod(key.hashCode(), nameMap.length)];
            if (lis != null) {
                for (int i = 0; i < lis.size(); i++) {
                    if (lis.get(i).keyEquals(getCode)) {
                        String toReturn = lis.get(i).value;
                        lis.remove(i);
                        size--;
                        return toReturn;
                    }
                }
            }

        }
        return null;
    }

    private static class Entry {

        private String key;
        private String value;

        Entry(String key, String value) {
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
            return (int) key.charAt(0) - 'A';
        }
    }
}
