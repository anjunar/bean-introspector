package de.bitvale.introspector.meta;

import com.google.common.reflect.TypeToken;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * @author Patrick Bittner on 16.04.2014.
 */
public interface MetaProperty<B, K, V> extends BiConsumer<B, V>, Function<B, V> {

    MetaModel<B, K> getModel();

    K getKey();

    TypeToken<V> getType();

    boolean isReadOnly();
}
