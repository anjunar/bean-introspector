package de.anjunar.introspector.type.resolved.raw;

import com.google.common.base.Objects;
import com.google.common.reflect.TypeToken;
import de.anjunar.introspector.type.internal.TypeHierarchy;
import de.anjunar.introspector.type.resolved.ResolvedField;
import de.anjunar.introspector.type.resolved.ResolvedType;

import java.lang.annotation.ElementType;
import java.lang.reflect.Field;
import java.util.StringJoiner;

/**
 * @author Patrick Bittner on 13.04.2014.
 */
public class RawResolvedField<X> extends AbstractRawResolvedAnnotatedElement implements ResolvedField<X> {

    private final String name;

    private final TypeToken<?> type;

    private final ResolvedType<X> enclosingType;

    private final TypeHierarchy<Class<?>, Field> typeHierarchy;


    RawResolvedField(final String name,
                     final TypeToken<?> type,
                     final ResolvedType<X> enclosingType,
                     final TypeHierarchy<Class<?>, Field> typeHierarchy) {
        super(typeHierarchy);
        this.name = name;
        this.enclosingType = enclosingType;
        this.typeHierarchy = typeHierarchy;
        this.type = type;
    }

    public static <X> ResolvedField<X> create(final String name,
                                              final ResolvedType<X> enclosingType,
                                              final TypeHierarchy<Class<?>, Field> fields) {

        final TypeToken<?> type = enclosingType
                .getType()
                .resolveType(fields.head().getGenericType());

        return new RawResolvedField<>(name, type, enclosingType, fields);

    }

    @Override
    public ElementType getElementType() {
        return ElementType.FIELD;
    }

    @Override
    public Object invoke(final X instance) {
        try {
            return typeHierarchy.key(instance.getClass()).get(instance);
        } catch (final IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<? super X> getDeclaringClass() {
        return (Class<? super X>) typeHierarchy.head().getDeclaringClass();
    }

    @Override
    public int getModifiers() {
        return typeHierarchy.head().getModifiers();
    }

    @Override
    public boolean isSynthetic() {
        return typeHierarchy.head().isSynthetic();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public TypeToken<?> getType() {
        return type;
    }

    @Override
    public ResolvedType<X> getEnclosingType() {
        return enclosingType;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getName(), getType(), getEnclosingType());
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof ResolvedField) {
            final ResolvedField<?> other = (ResolvedField<?>) obj;
            return Objects.equal(getName(), other.getName())
                    && Objects.equal(getType(), other.getType())
                    && Objects.equal(getEnclosingType(), other.getEnclosingType());

        }
        return false;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", RawResolvedField.class.getSimpleName() + "[", "]")
                .add("name='" + name + "'")
                .add("type=" + type)
                .add("enclosingType=" + enclosingType)
                .add("typeHierarchy=" + typeHierarchy)
                .toString();
    }
}
