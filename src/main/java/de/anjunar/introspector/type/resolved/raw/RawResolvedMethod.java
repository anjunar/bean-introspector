package de.anjunar.introspector.type.resolved.raw;

import com.google.common.base.Objects;
import com.google.common.reflect.TypeToken;
import de.anjunar.introspector.type.internal.TypeHierarchy;
import de.anjunar.introspector.type.resolved.ResolvedMethod;
import de.anjunar.introspector.type.resolved.ResolvedParameter;
import de.anjunar.introspector.type.resolved.ResolvedType;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Patrick Bittner on 13.04.2014.
 */
public class RawResolvedMethod<X> extends AbstractRawResolvedExecutable<X> implements ResolvedMethod<X> {

    private final String name;

    private final TypeHierarchy<Class<?>, Method> typeHierarchy;

    private final TypeToken<?> returnType;


    RawResolvedMethod(final String name,
                      final TypeToken<?> returnType,
                      final ResolvedType<X> enclosingType,
                      final TypeHierarchy<Class<?>, Method> typeHierarchy,
                      final List<ResolvedParameter<X>> parameters) {
        super(enclosingType, typeHierarchy, parameters);
        this.name = name;
        this.typeHierarchy = typeHierarchy;
        this.returnType = returnType;
    }

    public static <X> ResolvedMethod<X> create(final String name,
                                               final ResolvedType<X> enclosingType,
                                               final TypeHierarchy<Class<?>, Method> typeHierarchy) {

        final TypeToken<?> returnType = enclosingType
                .getType()
                .resolveType(typeHierarchy.head().getGenericReturnType());


        final List<ResolvedParameter<X>> parameters = new ArrayList<>();

        final ResolvedMethod<X> resolvedMethod = new RawResolvedMethod<X>(name, returnType, enclosingType, typeHierarchy, parameters);

        final int parameterCount = typeHierarchy.head().getParameterCount();
        for (int parameterIndex = 0; parameterIndex < parameterCount; parameterIndex++) {

            final TypeHierarchy<Method, Parameter> parameterList = new TypeHierarchy<>();
            for (final Method method : typeHierarchy.values()) {
                parameterList.add(method, method.getParameters()[parameterIndex]);
            }

            parameters.add(RawResolvedParameter.create(parameterIndex, resolvedMethod, parameterList));
        }

        return resolvedMethod;
    }

    @Override
    public ElementType getElementType() {
        return ElementType.METHOD;
    }

    @Override
    public Object invoke(final X instance, final Object... args) {
        try {
            return typeHierarchy.key(instance.getClass()).invoke(instance, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public TypeToken<?> getReturnType() {
        return returnType;
    }

    @Override
    public boolean equalSignature(Method method) {
        return typeHierarchy.key(method.getDeclaringClass()).equals(method);
    }

    @Override
    public boolean isAnnotationPresent(Class<? extends Annotation> annotationClass) {
        if (getParameters().stream().anyMatch((parameter) -> parameter.isAnnotationPresent(annotationClass))) {
            return true;
        }
        return super.isAnnotationPresent(annotationClass);
    }

    @Override
    public <A extends Annotation> A getAnnotation(Class<A> annotationClass) {
        for (ResolvedParameter<X> parameter : getParameters()) {
            A annotation = parameter.getAnnotation(annotationClass);
            if (annotation != null) {
                return annotation;
            }
        }
        return super.getAnnotation(annotationClass);
    }

    @Override
    public Annotation[] getAnnotations() {
        List<Annotation> annotations = new ArrayList<>();
        for (ResolvedParameter<X> parameter : getParameters()) {
            annotations.addAll(Arrays.asList(parameter.getAnnotations()));
        }
        annotations.addAll(Arrays.asList(super.getAnnotations()));
        return annotations.toArray(new Annotation[annotations.size()]);
    }

    @Override
    public <A extends Annotation> A[] getAnnotationsByType(Class<A> annotationClass) {
        List<A> annotations = new ArrayList<>();
        for (ResolvedParameter<X> parameter : getParameters()) {
            annotations.addAll(Arrays.asList(parameter.getAnnotationsByType(annotationClass)));
        }
        annotations.addAll(Arrays.asList(super.getAnnotationsByType(annotationClass)));
        return (A[]) annotations.toArray(annotations.toArray(new Object[annotations.size()]));
    }

    @Override
    public <A extends Annotation> A getDeclaredAnnotation(Class<A> annotationClass) {
        for (ResolvedParameter<X> parameter : getParameters()) {
            A declaredAnnotation = parameter.getDeclaredAnnotation(annotationClass);
            if (declaredAnnotation != null) {
                return declaredAnnotation;
            }
        }
        return super.getDeclaredAnnotation(annotationClass);
    }

    @Override
    public <A extends Annotation> A[] getDeclaredAnnotationsByType(Class<A> annotationClass) {
        List<A> annotations = new ArrayList<>();
        for (ResolvedParameter<X> parameter : getParameters()) {
            annotations.addAll(Arrays.asList(parameter.getDeclaredAnnotationsByType(annotationClass)));
        }
        annotations.addAll(Arrays.asList(super.getDeclaredAnnotationsByType(annotationClass)));
        return (A[]) annotations.toArray(annotations.toArray(new Object[annotations.size()]));
    }

    @Override
    public Annotation[] getDeclaredAnnotations() {
        List<Annotation> annotations = new ArrayList<>();
        for (ResolvedParameter<X> parameter : getParameters()) {
            annotations.addAll(Arrays.asList(parameter.getDeclaredAnnotations()));
        }
        annotations.addAll(Arrays.asList(super.getDeclaredAnnotations()));
        return annotations.toArray(new Annotation[annotations.size()]);
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
        return new StringJoiner(", ", RawResolvedMethod.class.getSimpleName() + "[", "]")
                .add("name='" + name + "'")
                .add("typeHierarchy=" + typeHierarchy)
                .add("returnType=" + returnType)
                .toString();
    }
}
