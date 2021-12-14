package de.anjunar.introspector.bean;

import com.google.common.reflect.TypeToken;
import de.anjunar.introspector.type.resolved.ResolvedType;
import javassist.util.proxy.ProxyFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Function;

/**
 * @author Patrick Bittner on 12.04.2014.
 */
public class BeanModel<B> extends AbstractAnnotatedBeanModel<B> {

    private final ProxyFactory factory = new ProxyFactory();

    private final LinkedHashMap<String, BeanProperty<B, ?>> properties;

    public BeanModel(final ResolvedType<B> type,
                     final LinkedHashMap<String, BeanProperty<B, ?>> properties) {
        super(type);
        this.properties = properties;
    }

    @Override
    public List<? extends BeanProperty<B, ?>> getProperties() {
        return new ArrayList<>(properties.values());
    }

    @Override
    public BeanProperty<B, ?> get(final String key) {
        return properties.get(key);
    }

    @Override
    public <V> BeanProperty<B, V> get(final String key, final Class<V> valueClass) {
        return get(key, TypeToken.of(valueClass));
    }

    @Override
    public <V> BeanProperty<B, V> get(final String key, final TypeToken<V> valueType) {
        return getInternal(key, valueType);
    }

    @Override
    public <V> List<? extends BeanProperty<B, V>> find(final Class<V> valueClass) {
        return find(TypeToken.of(valueClass));
    }

    @Override
    public <V> List<? extends BeanProperty<B, V>> find(final TypeToken<V> valueType) {
        return findInternal(valueType);
    }

    @SuppressWarnings("unchecked")
    public <V> BeanProperty<B, V> get(final Function<B, V> function) {

        final BeanPropertyResolver<B, V> proxy = new BeanPropertyResolver<>(factory, getType(), function);

        final String propertyName = proxy.getPropertyName();

        return (BeanProperty<B, V>) properties.get(propertyName);
    }

}
