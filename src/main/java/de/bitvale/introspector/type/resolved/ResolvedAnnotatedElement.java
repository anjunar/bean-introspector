package de.bitvale.introspector.type.resolved;

import com.google.common.collect.ImmutableList;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.reflect.AnnotatedElement;

/**
 * @author Patrick Bittner on 04.05.2014.
 */
public interface ResolvedAnnotatedElement extends AnnotatedElement {

    ElementType getElementType();

    ImmutableList<ResolvedAnnotatedElement> find(Class<? extends Annotation> annotationClass);

}
