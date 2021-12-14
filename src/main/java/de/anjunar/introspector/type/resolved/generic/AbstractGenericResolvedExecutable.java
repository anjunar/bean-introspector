package de.anjunar.introspector.type.resolved.generic;

import de.anjunar.introspector.type.resolved.ResolvedExecutable;
import de.anjunar.introspector.type.resolved.ResolvedParameter;
import de.anjunar.introspector.type.resolved.ResolvedType;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Patrick Bittner on 04.05.2014.
 */
public abstract class AbstractGenericResolvedExecutable<X> extends AbstractGenericResolvedAnnotatedElement
        implements ResolvedExecutable<X> {

    private final ResolvedType<X> enclosingType;

    private final ResolvedExecutable<X> resolvedExecutable;

    private List<ResolvedParameter<X>> parameters;

    protected AbstractGenericResolvedExecutable(final ResolvedType<X> enclosingType,
                                                final ResolvedExecutable<X> resolvedExecutable) {
        super(resolvedExecutable);
        this.enclosingType = enclosingType;
        this.resolvedExecutable = resolvedExecutable;
    }

    @Override
    public Class<? super X> getDeclaringClass() {
        return resolvedExecutable.getDeclaringClass();
    }

    @Override
    public String getName() {
        return resolvedExecutable.getName();
    }

    @Override
    public int getModifiers() {
        return resolvedExecutable.getModifiers();
    }

    @Override
    public boolean isSynthetic() {
        return resolvedExecutable.isSynthetic();
    }

    @Override
    public ResolvedType<X> getEnclosingType() {
        return enclosingType;
    }

    @Override
    public List<ResolvedParameter<X>> getParameters() {
        if (parameters == null) {
            parameters = resolvedExecutable.getParameters().stream()
                    .map(parameter -> new GenericResolvedParameter<>(this, parameter)).collect(Collectors.toList());
        }
        return Collections.unmodifiableList(parameters);
    }

}
