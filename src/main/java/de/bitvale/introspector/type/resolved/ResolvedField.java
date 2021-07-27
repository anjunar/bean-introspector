package de.bitvale.introspector.type.resolved;

import com.google.common.reflect.TypeToken;

/**
 * @author Patrick Bittner on 04.05.2014.
 */
public interface ResolvedField<X> extends ResolvedAnnotatedElement, ResolvedMember<X> {

    Object invoke(X instance);

    TypeToken<?> getType();

    ResolvedType<X> getEnclosingType();

}
