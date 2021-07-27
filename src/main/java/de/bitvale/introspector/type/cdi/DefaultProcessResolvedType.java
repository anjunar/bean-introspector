package de.bitvale.introspector.type.cdi;

import de.bitvale.introspector.type.resolved.ResolvedType;

import javax.enterprise.inject.spi.ProcessAnnotatedType;

/**
 * @author Patrick Bittner on 26.05.2014.
 */
public class DefaultProcessResolvedType implements ProcessResolvedType {

    private final ProcessAnnotatedType<?> processAnnotatedType;

    private final ResolvedType<?> resolvedType;

    public DefaultProcessResolvedType(final ProcessAnnotatedType<?> processAnnotatedType,
                                      final ResolvedType<?> resolvedType) {
        this.processAnnotatedType = processAnnotatedType;
        this.resolvedType = resolvedType;
    }

    @Override
    public ResolvedType<?> getResolvedType() {
        return resolvedType;
    }

    @Override
    public void veto() {
        processAnnotatedType.veto();
    }
}
