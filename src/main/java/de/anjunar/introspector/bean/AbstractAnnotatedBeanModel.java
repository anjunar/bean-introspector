package de.anjunar.introspector.bean;

import de.anjunar.introspector.meta.AbstractMetaModel;
import de.anjunar.introspector.type.resolved.ResolvedType;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

/**
 * @author Patrick Bittner on 03.05.2014.
 */
public abstract class AbstractAnnotatedBeanModel<B> extends AbstractMetaModel<B, String> implements AnnotatedElement {

    private final ResolvedType<B> type;

    protected AbstractAnnotatedBeanModel(final ResolvedType<B> type) {
        this.type = type;
    }

    protected ResolvedType<B> getType() {
        return type;
    }

    @Override
    public boolean isAnnotationPresent(final Class<? extends Annotation> annotationClass) {
        return type.isAnnotationPresent(annotationClass);
    }

    @Override
    public <A extends Annotation> A getAnnotation(final Class<A> annotationClass) {
        return type.getAnnotation(annotationClass);
    }

    @Override
    public Annotation[] getAnnotations() {
        return type.getAnnotations();
    }

    @Override
    public <A extends Annotation> A[] getAnnotationsByType(final Class<A> annotationClass) {
        return type.getAnnotationsByType(annotationClass);
    }

    @Override
    public <A extends Annotation> A getDeclaredAnnotation(final Class<A> annotationClass) {
        return type.getDeclaredAnnotation(annotationClass);
    }

    @Override
    public <A extends Annotation> A[] getDeclaredAnnotationsByType(final Class<A> annotationClass) {
        return type.getDeclaredAnnotationsByType(annotationClass);
    }

    @Override
    public Annotation[] getDeclaredAnnotations() {
        return type.getDeclaredAnnotations();
    }

}
