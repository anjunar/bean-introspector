package de.bitvale.introspector.meta;

import com.google.common.reflect.TypeToken;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Patrick Bittner on 03.05.2014.
 */
public abstract class AbstractMetaModel<B, K> implements MetaModel.Normal<B, K> {

    @SuppressWarnings("unchecked")
    protected <V, M extends MetaProperty<B, K, V>> M getInternal(final K key,
                                                                 final TypeToken<V> valueType) {
        final MetaProperty<B, K, ?> property = get(key);

        if (valueType.isSubtypeOf(property.getType())) {
            return (M) property;
        }

        throw new IllegalStateException("Wrong Type");
    }

    @SuppressWarnings("unchecked")
    protected <V, M extends MetaProperty<B, K, V>> List<M> findInternal(final TypeToken<V> typeToken) {
        return getProperties().stream()
                .filter(property -> typeToken.isSubtypeOf(property.getType()))
                .map(property -> (M) property)
                .collect(Collectors.toList());

    }


}
