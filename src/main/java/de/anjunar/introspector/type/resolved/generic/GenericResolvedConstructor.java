package de.anjunar.introspector.type.resolved.generic;

import com.google.common.base.Objects;
import de.anjunar.introspector.type.resolved.ResolvedConstructor;
import de.anjunar.introspector.type.resolved.ResolvedParameter;
import de.anjunar.introspector.type.resolved.ResolvedType;

import java.lang.annotation.ElementType;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * @author Patrick Bittner on 04.05.2014.
 */
public class GenericResolvedConstructor<X> extends AbstractGenericResolvedExecutable<X> implements ResolvedConstructor<X> {

    private final ResolvedConstructor<X> resolvedConstructor;

    public GenericResolvedConstructor(final ResolvedType<X> enclosingType,
                                      final ResolvedConstructor<X> resolvedConstructor) {
        super(enclosingType, resolvedConstructor);
        this.resolvedConstructor = resolvedConstructor;
    }

    @Override
    public ElementType getElementType() {
        return resolvedConstructor.getElementType();
    }

    @Override
    public X invoke(final Object... args) {
        return resolvedConstructor.invoke(args);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(
                getEnclosingType(),
                getParameters()
                        .stream()
                        .map(ResolvedParameter::getType)
                        .collect(Collectors.toList())
        );
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof ResolvedConstructor) {
            final ResolvedConstructor<?> other = (ResolvedConstructor<?>) obj;
            return Objects.equal(getParameters(), other.getParameters())
                    && Objects.equal(getEnclosingType(), other.getEnclosingType());
        }
        return false;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", GenericResolvedConstructor.class.getSimpleName() + "[", "]")
                .add("resolvedConstructor=" + resolvedConstructor)
                .toString();
    }
}
