package de.anjunar.introspector.type.cdi;

import de.anjunar.introspector.type.resolved.ResolvedType;

/**
 * @author Patrick Bittner on 18.05.2014.
 */
public interface ProcessResolvedType {

    ResolvedType<?> getResolvedType();

    void veto();


}
