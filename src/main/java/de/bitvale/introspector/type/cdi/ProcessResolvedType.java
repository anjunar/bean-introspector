package de.bitvale.introspector.type.cdi;

import de.bitvale.introspector.type.resolved.ResolvedType;

/**
 * @author Patrick Bittner on 18.05.2014.
 */
public interface ProcessResolvedType {

    ResolvedType<?> getResolvedType();

    void veto();


}
