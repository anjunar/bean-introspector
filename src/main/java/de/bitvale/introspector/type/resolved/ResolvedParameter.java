package de.bitvale.introspector.type.resolved;

import com.google.common.reflect.TypeToken;

/**
 * @author Patrick Bittner on 04.05.2014.
 */
public interface ResolvedParameter<X> extends ResolvedAnnotatedElement {

    int getPosition();

    ResolvedExecutable<X> getEnclosingExecutable();

    TypeToken<?> getType();

}
