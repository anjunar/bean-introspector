package de.bitvale.introspector.type.resolved.raw;

import com.google.common.base.Objects;
import com.google.common.reflect.TypeToken;
import de.bitvale.introspector.type.internal.TypeHierarchy;
import de.bitvale.introspector.type.resolved.ResolvedExecutable;
import de.bitvale.introspector.type.resolved.ResolvedParameter;

import java.lang.annotation.ElementType;
import java.lang.reflect.Executable;
import java.lang.reflect.Parameter;
import java.util.StringJoiner;

/**
 * @author Patrick Bittner on 13.04.2014.
 */
public class RawResolvedParameter<X> extends AbstractRawResolvedAnnotatedElement implements ResolvedParameter<X> {

    private final int index;

    private final ResolvedExecutable<X> enclosingExecutable;

    private final TypeToken<?> type;

    RawResolvedParameter(final int index,
                         final TypeToken<?> type,
                         final ResolvedExecutable<X> enclosingExecutable,
                         final TypeHierarchy<? extends Executable, Parameter> parameters) {
        super(parameters);
        this.index = index;
        this.type = type;
        this.enclosingExecutable = enclosingExecutable;
    }

    public static <X> ResolvedParameter<X> create(final int index,
                                                  final ResolvedExecutable<X> enclosingExecutable,
                                                  final TypeHierarchy<? extends Executable, Parameter> parameters) {

        TypeToken<?> type;
        try {

            try {
                type = enclosingExecutable
                        .getEnclosingType()
                        .getType()
                        .resolveType(parameters.head().getParameterizedType());
            } catch (ArrayIndexOutOfBoundsException e) {
                // Java 7 has no Parameter Object, workaround
                type = new TypeToken<Object>() {
                };
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return new RawResolvedParameter<>(index, type, enclosingExecutable, parameters);

    }

    @Override
    public ElementType getElementType() {
        return ElementType.TYPE_PARAMETER;
    }

    @Override
    public int getPosition() {
        return index;
    }

    @Override
    public ResolvedExecutable<X> getEnclosingExecutable() {
        return enclosingExecutable;
    }

    @Override
    public TypeToken<?> getType() {
        return type;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(type, enclosingExecutable, index);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof ResolvedParameter) {
            final ResolvedParameter<?> other = (ResolvedParameter<?>) obj;

            return Objects.equal(type, other.getType())
                    && Objects.equal(enclosingExecutable, other.getEnclosingExecutable())
                    && Objects.equal(index, other.getPosition());
        }
        return false;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", RawResolvedParameter.class.getSimpleName() + "[", "]")
                .add("index=" + index)
                .add("enclosingExecutable=" + enclosingExecutable)
                .add("type=" + type)
                .toString();
    }
}
