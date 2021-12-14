package de.anjunar.introspector.type.resolved;

import java.util.List;

/**
 * @author Patrick Bittner on 04.05.2014.
 */
public interface ResolvedExecutable<X> extends ResolvedAnnotatedElement, ResolvedMember<X> {

    ResolvedType<X> getEnclosingType();

    List<ResolvedParameter<X>> getParameters();


}
