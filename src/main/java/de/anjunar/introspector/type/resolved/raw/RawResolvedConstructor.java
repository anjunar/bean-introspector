package de.anjunar.introspector.type.resolved.raw;

import com.google.common.base.Objects;
import de.anjunar.introspector.type.internal.TypeHierarchy;
import de.anjunar.introspector.type.resolved.ResolvedConstructor;
import de.anjunar.introspector.type.resolved.ResolvedParameter;
import de.anjunar.introspector.type.resolved.ResolvedType;

import java.lang.annotation.ElementType;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * @author Patrick Bittner on 13.04.2014.
 */
public class RawResolvedConstructor<X> extends AbstractRawResolvedExecutable<X> implements ResolvedConstructor<X> {

    private final TypeHierarchy<Class<?>, Constructor<?>> typeHierarchy;

    public RawResolvedConstructor(final ResolvedType<X> enclosingType,
                                  final TypeHierarchy<Class<?>, Constructor<?>> typeHierarchy,
                                  final List<ResolvedParameter<X>> parameters) {
        super(enclosingType, typeHierarchy, parameters);
        this.typeHierarchy = typeHierarchy;
    }

    public static <X> ResolvedConstructor<X> create(final ResolvedType<X> enclosingType,
                                                    final TypeHierarchy<Class<?>, Constructor<?>> constructors) {
        final List<ResolvedParameter<X>> parameters = new ArrayList<>();
        final ResolvedConstructor<X> resolvedConstructor = new RawResolvedConstructor<>(enclosingType, constructors, parameters);
        final int parameterCount = constructors.head().getParameterCount();
        for (int parameterIndex = 0; parameterIndex < parameterCount; parameterIndex++) {
            final TypeHierarchy<Constructor<?>, Parameter> parameterList = new TypeHierarchy<>();
            for (final Constructor<?> constructor : constructors.values()) {
                parameterList.add(constructor, constructor.getParameters()[parameterIndex]);
            }
            parameters.add(RawResolvedParameter.create(parameterIndex, resolvedConstructor, parameterList));
        }
        return resolvedConstructor;
    }

    @Override
    public ElementType getElementType() {
        return ElementType.CONSTRUCTOR;
    }

    @Override
    @SuppressWarnings("unchecked")
    public X invoke(final Object... args) {
        try {
            return (X) typeHierarchy.head().newInstance(args);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
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
        return new StringJoiner(", ", RawResolvedConstructor.class.getSimpleName() + "[", "]")
                .add("typeHierarchy=" + typeHierarchy)
                .toString();
    }
}
