package de.bitvale.introspector.type.resolved;

/**
 * @author Patrick Bittner on 20.05.2014.
 */
public interface ResolvedMember<X> {

    Class<? super X> getDeclaringClass();

    String getName();

    int getModifiers();

    boolean isSynthetic();


}
