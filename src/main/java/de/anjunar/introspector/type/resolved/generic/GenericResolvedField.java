package de.anjunar.introspector.type.resolved.generic;

import com.google.common.base.Objects;
import com.google.common.reflect.TypeToken;
import de.anjunar.introspector.type.resolved.ResolvedField;
import de.anjunar.introspector.type.resolved.ResolvedType;

import java.lang.annotation.ElementType;
import java.util.StringJoiner;

/**
 * @author Patrick Bittner on 04.05.2014.
 */
public class GenericResolvedField<X> extends AbstractGenericResolvedAnnotatedElement implements ResolvedField<X> {

    private final ResolvedType<X> enclosingType;

    private final ResolvedField<X> resolvedField;

    private TypeToken<?> type;

    public GenericResolvedField(final ResolvedType<X> enclosingType,
                                final ResolvedField<X> resolvedField) {
        super(resolvedField);
        this.enclosingType = enclosingType;
        this.resolvedField = resolvedField;
    }

    @Override
    public Class<? super X> getDeclaringClass() {
        return resolvedField.getDeclaringClass();
    }

    @Override
    public ElementType getElementType() {
        return resolvedField.getElementType();
    }

    @Override
    public Object invoke(final X instance) {
        return resolvedField.invoke(instance);
    }

    @Override
    public String getName() {
        return resolvedField.getName();
    }

    @Override
    public int getModifiers() {
        return resolvedField.getModifiers();
    }

    @Override
    public boolean isSynthetic() {
        return resolvedField.isSynthetic();
    }

    @Override
    public TypeToken<?> getType() {
        if (type == null) {
            type = getEnclosingType().getType().resolveType(resolvedField.getType().getType());
        }
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
        return new StringJoiner(", ", GenericResolvedField.class.getSimpleName() + "[", "]")
                .add("enclosingType=" + enclosingType)
                .add("resolvedField=" + resolvedField)
                .add("type=" + type)
                .toString();
    }
}
