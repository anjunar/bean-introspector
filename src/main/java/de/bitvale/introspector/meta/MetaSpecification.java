package de.bitvale.introspector.meta;

import de.bitvale.introspector.type.resolved.ResolvedExecutable;
import de.bitvale.introspector.type.resolved.ResolvedField;
import de.bitvale.introspector.type.resolved.ResolvedParameter;
import de.bitvale.introspector.type.resolved.ResolvedType;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * @author Patrick Bittner on 15.05.2014.
 */
public abstract class MetaSpecification {

    @SafeVarargs
    public static <X> boolean isSatisfiedWith(final ResolvedType<X> type,
                                              final Class<? extends Annotation>... requiredAnnotations) {

        if (type.getRawType().isInterface()) {
            return false;
        }

        for (final Class<? extends Annotation> requiredAnnotation : requiredAnnotations) {

            if (type.isAnnotationPresent(requiredAnnotation)) {
                return true;
            }

            if (isExecutableSatisfiedWith(type.getMethods(), requiredAnnotation)) {
                return true;
            }

            if (isExecutableSatisfiedWith(type.getConstructors(), requiredAnnotation)) {
                return true;
            }

            for (final ResolvedField<X> resolvedField : type.getFields()) {
                if (resolvedField.isAnnotationPresent(requiredAnnotation)) {
                    return true;
                }
            }

        }
        return false;
    }

    private static <X> boolean isExecutableSatisfiedWith(final List<? extends ResolvedExecutable<X>> executables,
                                                         final Class<? extends Annotation> requiredAnnotation) {
        for (final ResolvedExecutable<X> executable : executables) {
            if (executable.isAnnotationPresent(requiredAnnotation)) {
                return true;
            }

            if (isParameterSatisfiedWith(executable.getParameters(), requiredAnnotation)) {
                return true;
            }

        }
        return false;
    }

    private static <X> boolean isParameterSatisfiedWith(final List<ResolvedParameter<X>> parameters,
                                                        final Class<? extends Annotation> requiredAnnotation) {
        for (final ResolvedParameter<X> resolvedParameter : parameters) {
            if (resolvedParameter.isAnnotationPresent(requiredAnnotation)) {
                return true;
            }
        }
        return false;
    }

    public abstract <X> boolean isSatisfiedBy(final ResolvedType<X> type);

    public abstract String name();


}
