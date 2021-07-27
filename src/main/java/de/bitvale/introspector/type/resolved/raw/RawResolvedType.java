package de.bitvale.introspector.type.resolved.raw;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.reflect.TypeToken;
import de.bitvale.introspector.type.internal.TypeHierarchy;
import de.bitvale.introspector.type.resolved.*;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * @author Patrick Bittner on 13.04.2014.
 */
public class RawResolvedType<X> extends AbstractRawResolvedAnnotatedElement implements ResolvedType<X> {

    private final TypeToken<X> type;

    private final List<ResolvedConstructor<X>> constructors;
    private final List<ResolvedField<X>> fields;
    private final List<ResolvedMethod<X>> methods;

    RawResolvedType(final TypeToken<X> type,
                    final TypeHierarchy<Class<?>, Class<?>> typeHierarchy,
                    final List<ResolvedConstructor<X>> constructors,
                    final List<ResolvedField<X>> fields,
                    final List<ResolvedMethod<X>> methods) {
        super(typeHierarchy);
        this.type = type;
        this.constructors = constructors;
        this.fields = fields;
        this.methods = methods;
    }

    public static <X> ResolvedType<X> create(final Class<X> type,
                                             final List<Class<?>> hierarchy,
                                             final List<ResolvedConstructor<X>> constructors,
                                             final List<ResolvedField<X>> fields,
                                             final List<ResolvedMethod<X>> methods) {

        final TypeHierarchy<Class<?>, Class<?>> result = new TypeHierarchy<>();

        for (final Class<?> declaringClass : hierarchy) {
            for (final Class<?> declaringInterface : declaringClass.getInterfaces()) {
                result.add(declaringClass, declaringInterface);
            }
            result.add(declaringClass, declaringClass);
        }

        return new RawResolvedType<>(TypeToken.of(type), result, constructors, fields, methods);
    }

    @Override
    public ImmutableList<ResolvedAnnotatedElement> find(final Class<? extends Annotation> annotationClass) {
        final ImmutableList<ResolvedAnnotatedElement> annotatedElements = super.find(annotationClass);

        final ImmutableList.Builder<ResolvedAnnotatedElement> builder = ImmutableList.builder();
        builder.addAll(annotatedElements);
        for (final ResolvedConstructor<X> constructor : constructors) {
            builder.addAll(constructor.find(annotationClass));
        }
        for (final ResolvedField<X> field : fields) {
            builder.addAll(field.find(annotationClass));
        }
        for (final ResolvedMethod<X> method : methods) {
            builder.addAll(method.find(annotationClass));
        }

        return builder.build();
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
    public ElementType getElementType() {
        return ElementType.TYPE;
    }

    @Override
    public TypeToken<X> getType() {
        return type;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<X> getRawType() {
        return (Class<X>) getType().getRawType();
    }

    @Override
    public List<ResolvedConstructor<X>> getConstructors() {
        return Collections.unmodifiableList(constructors);
    }

    @Override
    public List<ResolvedField<X>> getFields() {
        return Collections.unmodifiableList(fields);
    }

    @Override
    public List<ResolvedMethod<X>> getMethods() {
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
        return new StringJoiner(", ", RawResolvedType.class.getSimpleName() + "[", "]")
                .add("type=" + type)
                .toString();
    }
}
