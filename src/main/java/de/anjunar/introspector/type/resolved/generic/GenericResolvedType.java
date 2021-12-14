package de.anjunar.introspector.type.resolved.generic;

import com.google.common.base.Objects;
import com.google.common.reflect.TypeToken;
import de.anjunar.introspector.type.resolved.ResolvedConstructor;
import de.anjunar.introspector.type.resolved.ResolvedField;
import de.anjunar.introspector.type.resolved.ResolvedMethod;
import de.anjunar.introspector.type.resolved.ResolvedType;

import java.lang.annotation.ElementType;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * @author Patrick Bittner on 04.05.2014.
 */
public class GenericResolvedType<X> extends AbstractGenericResolvedAnnotatedElement implements ResolvedType<X> {

    private final TypeToken<X> typeToken;

    private final ResolvedType<X> resolvedType;

    private List<ResolvedConstructor<X>> constructors;

    private List<ResolvedField<X>> fields;

    private List<ResolvedMethod<X>> methods;

    public GenericResolvedType(final TypeToken<X> typeToken, final ResolvedType<X> resolvedType) {
        super(resolvedType);
        this.typeToken = typeToken;
        this.resolvedType = resolvedType;
    }

    @Override
    public ElementType getElementType() {
        return resolvedType.getElementType();
    }

    @Override
    public TypeToken<X> getType() {
        return typeToken;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<X> getRawType() {
        return (Class<X>) getType().getRawType();
    }

    @Override
    public ResolvedMethod<?> find(String name, Class<?>... parameters) {
        for (ResolvedMethod<X> method : methods) {
            if (method.getName().equals(name)) {
                List<? extends Class<?>> methodParameters = method.getParameters()
                        .stream()
                        .map((parameter -> parameter.getType().getRawType()))
                        .collect(Collectors.toList());
                if (methodParameters.equals(Arrays.asList(parameters))) {
                    return method;
                }
            }
        }
        return null;
    }

    @Override
    public List<ResolvedConstructor<X>> getConstructors() {
        if (constructors == null) {
            constructors = resolvedType.getConstructors().stream()
                    .map(constructor -> new GenericResolvedConstructor<>(this, constructor)).collect(Collectors.toList());
        }
        return Collections.unmodifiableList(constructors);
    }

    @Override
    public List<ResolvedField<X>> getFields() {
        if (fields == null) {
            fields = resolvedType.getFields().stream()
                    .map(field -> new GenericResolvedField<>(this, field)).collect(Collectors.toList());
        }
        return Collections.unmodifiableList(fields);
    }

    @Override
    public List<ResolvedMethod<X>> getMethods() {
        if (methods == null) {
            methods = resolvedType.getMethods().stream()
                    .map(method -> new GenericResolvedMethod<>(this, method)).collect(Collectors.toList());
        }
        return Collections.unmodifiableList(methods);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getType().hashCode());
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof ResolvedType<?>) {
            final ResolvedType<?> other = (ResolvedType<?>) obj;
            return Objects.equal(getType(), other.getType());
        }
        return false;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", GenericResolvedType.class.getSimpleName() + "[", "]")
                .add("typeToken=" + typeToken)
                .add("resolvedType=" + resolvedType)
                .add("constructors=" + constructors)
                .add("fields=" + fields)
                .add("methods=" + methods)
                .toString();
    }
}
