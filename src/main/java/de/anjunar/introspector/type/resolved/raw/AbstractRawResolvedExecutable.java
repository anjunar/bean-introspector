package de.anjunar.introspector.type.resolved.raw;

import com.google.common.collect.ImmutableList;
import de.anjunar.introspector.type.internal.TypeHierarchy;
import de.anjunar.introspector.type.resolved.ResolvedAnnotatedElement;
import de.anjunar.introspector.type.resolved.ResolvedExecutable;
import de.anjunar.introspector.type.resolved.ResolvedParameter;
import de.anjunar.introspector.type.resolved.ResolvedType;

import java.lang.annotation.Annotation;
import java.lang.reflect.Executable;
import java.util.Collections;
import java.util.List;

/**
 * @author Patrick Bittner on 13.04.2014.
 */
public abstract class AbstractRawResolvedExecutable<X> extends AbstractRawResolvedAnnotatedElement implements ResolvedExecutable<X> {

    private final ResolvedType<X> enclosingType;

    private final List<ResolvedParameter<X>> parameters;

    private final TypeHierarchy<Class<?>, ? extends Executable> typeHierarchy;

    protected AbstractRawResolvedExecutable(final ResolvedType<X> enclosingType,
                                            final TypeHierarchy<Class<?>, ? extends Executable> typeHierarchy,
                                            final List<ResolvedParameter<X>> parameters) {
        super(typeHierarchy);
        this.enclosingType = enclosingType;
        this.typeHierarchy = typeHierarchy;
        this.parameters = parameters;
    }

    @Override
    public ImmutableList<ResolvedAnnotatedElement> find(final Class<? extends Annotation> annotationClass) {
        final ImmutableList<ResolvedAnnotatedElement> annotatedElements = super.find(annotationClass);

        final ImmutableList.Builder<ResolvedAnnotatedElement> builder = ImmutableList.builder();
        builder.addAll(annotatedElements);
        for (final ResolvedParameter<X> parameter : parameters) {
            builder.addAll(parameter.find(annotationClass));
        }
        return builder.build();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<? super X> getDeclaringClass() {
        return (Class<? super X>) typeHierarchy.head().getDeclaringClass();
    }

    @Override
    public String getName() {
        return typeHierarchy.head().getName();
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
    public ResolvedType<X> getEnclosingType() {
        return enclosingType;
    }

    @Override
    public List<ResolvedParameter<X>> getParameters() {
        return Collections.unmodifiableList(parameters);
    }
}
