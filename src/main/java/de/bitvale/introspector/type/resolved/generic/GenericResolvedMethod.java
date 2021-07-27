package de.bitvale.introspector.type.resolved.generic;

import com.google.common.base.Objects;
import com.google.common.reflect.TypeToken;
import de.bitvale.introspector.type.resolved.ResolvedMethod;
import de.bitvale.introspector.type.resolved.ResolvedParameter;
import de.bitvale.introspector.type.resolved.ResolvedType;

import java.lang.annotation.ElementType;
import java.lang.reflect.Method;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * @author Patrick Bittner on 04.05.2014.
 */
public class GenericResolvedMethod<X> extends AbstractGenericResolvedExecutable<X> implements ResolvedMethod<X> {

    private final ResolvedMethod<X> resolvedMethod;

    private TypeToken<?> returnType;

    public GenericResolvedMethod(final ResolvedType<X> enclosingType,
                                 final ResolvedMethod<X> resolvedMethod) {
        super(enclosingType, resolvedMethod);
        this.resolvedMethod = resolvedMethod;
    }

    @Override
    public ElementType getElementType() {
        return resolvedMethod.getElementType();
    }

    @Override
    public Object invoke(final X instance, final Object... args) {
        return resolvedMethod.invoke(instance, args);
    }

    @Override
    public TypeToken<?> getReturnType() {
        if (returnType == null) {
            returnType = getEnclosingType().getType().resolveType(resolvedMethod.getReturnType().getType());
        }
        return returnType;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(
                getReturnType(),
                getName(),
                getParameters()
                        .stream()
                        .map(ResolvedParameter::getType)
                        .collect(Collectors.toList()), getEnclosingType()
        );
    }

    @Override
    public boolean equalSignature(Method method) {
        return resolvedMethod.equalSignature(method);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj instanceof ResolvedMethod) {
            final ResolvedMethod<?> other = (ResolvedMethod<?>) obj;

            return Objects.equal(getReturnType(), other.getReturnType())
                    && Objects.equal(getName(), other.getName())
                    && Objects.equal(getParameters(), other.getParameters())
                    && Objects.equal(getEnclosingType(), other.getEnclosingType());
        }

        return false;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", GenericResolvedMethod.class.getSimpleName() + "[", "]")
                .add("resolvedMethod=" + resolvedMethod)
                .add("returnType=" + returnType)
                .toString();
    }
}
