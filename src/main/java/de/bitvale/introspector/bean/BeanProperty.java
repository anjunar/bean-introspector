package de.bitvale.introspector.bean;

import com.google.common.base.Objects;
import com.google.common.reflect.TypeToken;
import de.bitvale.introspector.meta.MetaProperty;
import de.bitvale.introspector.type.internal.TypeHierarchy;
import de.bitvale.introspector.type.resolved.ResolvedField;
import de.bitvale.introspector.type.resolved.ResolvedMethod;
import de.bitvale.introspector.type.resolved.raw.AbstractRawResolvedAnnotatedElement;

import java.lang.annotation.ElementType;
import java.lang.reflect.AnnotatedElement;
import java.util.Optional;
import java.util.StringJoiner;

/**
 * @author Patrick Bittner on 14.04.2014.
 */
public class BeanProperty<X, V> extends AbstractRawResolvedAnnotatedElement implements MetaProperty<X, String, V> {

    private final String key;

    private final BeanModel<X> model;

    private final Optional<ResolvedField<X>> field;

    private final Optional<ResolvedMethod<X>> getMethod;
    private final Optional<ResolvedMethod<X>> setMethod;

    BeanProperty(final String key,
                 final BeanModel<X> model,
                 final TypeHierarchy<BeanModel<?>, ? extends AnnotatedElement> typeHierarchy,
                 final Optional<ResolvedField<X>> field,
                 final Optional<ResolvedMethod<X>> getMethod,
                 final Optional<ResolvedMethod<X>> setMethod) {
        super(typeHierarchy);
        this.model = model;
        this.key = key;
        this.field = field;
        this.getMethod = getMethod;
        this.setMethod = setMethod;
    }

    public static <X, T> BeanProperty<X, T> create(final String key,
                                                   final BeanModel<X> model,
                                                   final Optional<ResolvedField<X>> field,
                                                   final Optional<ResolvedMethod<X>> getMethod,
                                                   final Optional<ResolvedMethod<X>> setMethod) {

        final TypeHierarchy<BeanModel<?>, AnnotatedElement> typeHierarchy = new TypeHierarchy<>();

        if (field.isPresent()) {
            typeHierarchy.add(model, field.get());
        }

        if (getMethod.isPresent()) {
            typeHierarchy.add(model, getMethod.get());
        }

        if (setMethod.isPresent()) {
            typeHierarchy.add(model, setMethod.get());
        }

        return new BeanProperty<>(key, model, typeHierarchy, field, getMethod, setMethod);


    }

    @Override
    public BeanModel<X> getModel() {
        return model;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public TypeToken<V> getType() {
        if (getMethod.isPresent()) {
            //noinspection unchecked
            return (TypeToken<V>) getMethod.get().getReturnType();
        } else {
            throw new IllegalStateException("No Getter available");
        }
    }

    @Override
    public void accept(final X bean, final V parameter) {
        if (setMethod.isPresent()) {
            setMethod.get().invoke(bean, parameter);
        } else {
            throw new IllegalStateException("No Setter available");
        }
    }

    @Override
    public V apply(final X bean) {
        if (getMethod.isPresent()) {
            //noinspection unchecked
            return (V) getMethod.get().invoke(bean);
        } else {
            throw new IllegalStateException("No Getter available");
        }
    }

    @Override
    public boolean isReadOnly() {
        return !setMethod.isPresent();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(model, key, getType());
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof BeanProperty<?, ?>) {
            final BeanProperty<?, ?> other = (BeanProperty<?, ?>) obj;

            return Objects.equal(model, other.getModel())
                    && Objects.equal(key, other.getKey())
                    && Objects.equal(getType(), other.getType());
        }
        return false;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", BeanProperty.class.getSimpleName() + "[", "]")
                .add("key='" + key + "'")
                .toString();
    }

    @Override
    public ElementType getElementType() {
        return ElementType.METHOD;
    }
}
