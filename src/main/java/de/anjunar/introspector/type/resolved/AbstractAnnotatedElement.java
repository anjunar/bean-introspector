package de.anjunar.introspector.type.resolved;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

/**
 * @author Patrick Bittner on 04.05.2014.
 */
public abstract class AbstractAnnotatedElement implements AnnotatedElement {

    private final AnnotatedElement annotatedElement;

    protected AbstractAnnotatedElement(final AnnotatedElement annotatedElement) {
        this.annotatedElement = annotatedElement;
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
