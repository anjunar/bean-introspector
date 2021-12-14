package de.anjunar.introspector.type.resolved.raw;

import com.google.common.collect.ImmutableList;
import de.anjunar.introspector.type.internal.TypeHierarchy;
import de.anjunar.introspector.type.resolved.ResolvedAnnotatedElement;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * @author Patrick Bittner on 13.04.2014.
 */
public abstract class AbstractRawResolvedAnnotatedElement implements ResolvedAnnotatedElement {

    private final TypeHierarchy<? extends AnnotatedElement, ? extends AnnotatedElement> annotatedElements;

    protected AbstractRawResolvedAnnotatedElement(final TypeHierarchy<? extends AnnotatedElement, ? extends AnnotatedElement> annotatedElements) {
        this.annotatedElements = annotatedElements;
    }

    @Override
    public ImmutableList<ResolvedAnnotatedElement> find(final Class<? extends Annotation> annotationClass) {
        if (isAnnotationPresent(annotationClass)) {
            return ImmutableList.of(this);
        } else {
            return ImmutableList.of();
        }
    }

    @Override
    public boolean isAnnotationPresent(final Class<? extends Annotation> annotationClass) {
        return annotatedElements.values().stream()
                .anyMatch(element -> element.isAnnotationPresent(annotationClass));
    }

    @Override
    public <A extends Annotation> A getAnnotation(final Class<A> annotationClass) {

        final Optional<? extends AnnotatedElement> optional
                = annotatedElements.values()
                .stream()
                .filter(annotatedElement -> annotatedElement.isAnnotationPresent(annotationClass))
                .findFirst();

        if (optional.isPresent()) {
            return optional.get().getAnnotation(annotationClass);
        }

        return null;
    }

    @Override
    public Annotation[] getAnnotations() {
        return annotatedElements.values().stream()
                .flatMap(element -> Stream.of(element.getAnnotations()))
                .distinct()
                .toArray(Annotation[]::new);
    }

    @Override
    @SuppressWarnings({"unchecked", "SuspiciousArrayCast"})
    public <A extends Annotation> A[] getAnnotationsByType(final Class<A> annotationClass) {

        final Annotation[] annotations = annotatedElements.values().stream()
                .flatMap(annotatedElement -> Stream.of(annotatedElement.getAnnotationsByType(annotationClass)))
                .distinct()
                .toArray(Annotation[]::new);

        return (A[]) annotations;

    }


    @Override
    public <A extends Annotation> A getDeclaredAnnotation(final Class<A> annotationClass) {
        return annotatedElements
                .head()
                .getDeclaredAnnotation(annotationClass);
    }

    @Override
    public <A extends Annotation> A[] getDeclaredAnnotationsByType(final Class<A> annotationClass) {
        return annotatedElements
                .head()
                .getDeclaredAnnotationsByType(annotationClass);
    }

    @Override
    public Annotation[] getDeclaredAnnotations() {
        return annotatedElements
                .head()
                .getDeclaredAnnotations();
    }


}
