package de.bitvale.introspector.type.internal;

import java.util.*;

/**
 * @author Patrick Bittner on 03.05.2014.
 */
public class TypeHierarchy<K, V> {

    private final Map<K, List<V>> map = new LinkedHashMap<>();

    private final List<V> list = new ArrayList<>();

    public V key(final K key) {
        final List<V> value = map.get(key);

        if (value == null) {
            return head();
        }

        return value.get(0);
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public V head() {
        return list.get(0);
    }


    public void add(final K key, final V value) {

        List<V> mapList = map.get(key);

        if (mapList == null) {
            mapList = new LinkedList<>();
            map.put(key, mapList);
        }

        mapList.add(value);

        list.add(value);

    }

    public List<V> values() {
        return Collections.unmodifiableList(list);
    }


}
