package de.bitvale.introspector.meta;

import com.google.common.reflect.TypeToken;

import java.util.List;

/**
 * @author Patrick Bittner on 18.04.2014.
 */
public interface MetaModel<B, K> {

    MetaProperty<B, K, ?> get(K key);

    List<? extends MetaProperty<B, K, ?>> getProperties();

    interface Normal<B, K> extends MetaModel<B, K> {

        <V> MetaProperty<B, K, V> get(K key, Class<V> valueClass);

        <V> MetaProperty<B, K, V> get(K key, TypeToken<V> valueType);

        <V> List<? extends MetaProperty<B, K, V>> find(Class<V> valueClass);

        <V> List<? extends MetaProperty<B, K, V>> find(TypeToken<V> valueType);

    }


    interface Strict<B, K, V> extends MetaModel<B, K> {

        @Override
        MetaProperty<B, K, V> get(K key);

        List<? extends MetaProperty<B, K, V>> find();

    }

}
