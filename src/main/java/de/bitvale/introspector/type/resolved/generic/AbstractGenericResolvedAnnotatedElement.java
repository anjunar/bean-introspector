package de.bitvale.introspector.type.resolved.generic;

import com.google.common.collect.ImmutableList;
import de.bitvale.introspector.type.resolved.ResolvedAnnotatedElement;

import java.lang.annotation.Annotation;

/**
 * @author Patrick Bittner on 04.05.2014.
 */
public abstract class AbstractGenericResolvedAnnotatedElement implements ResolvedAnnotatedElement {

    private final ResolvedAnnotatedElement annotatedElement;

    protected AbstractGenericResolvedAnnotatedElement(final ResolvedAnnotatedElement annotatedElement) {
        this.annotatedElement = annotatedElement;
    }

    @Override
    public ImmutableList<ResolvedAnnotatedElement> find(final Class<? extends Annotation> annotationClass) {
        return annotatedElement.find(annotationClass);
    }

    @Override
    public boolean isAnnotationPresent(final Class<? extends Annotation> annotationClass) {
        return annotatedElement.isAnnotationPresent(annotationClass);
    }

    @Override
    public <T extends Annotation> T getAnnotation(final Class<T> annotationClass) {
        return annotatedElement.getAnnotation(annotationClass);
    }

    @Override
    public Annotation[] getAnnotations() {
        return annotatedElement.getAnnotations();
    }

    @Override
    public <T extends Annotation> T[] getAnnotationsByType(final Class<T> annotationClass) {
        return annotatedElement.getAnnotationsByType(annotationClass);
    }

    @Override
    public <T extends Annotation> T getDeclaredAnnotation(final Class<T> annotationClass) {
        return annotatedElement.getDeclaredAnnotation(annotationClass);
    }

    @Override
    public <T extends Annotation> T[] getDeclaredAnnotationsByType(final Class<T> annotationClass) {
        return annotatedElement.getDeclaredAnnotationsByType(annotationClass);
    }

    @Override
    public Annotation[] getDeclaredAnnotations() {
        return annotatedElement.getDeclaredAnnotations();
    }
}
