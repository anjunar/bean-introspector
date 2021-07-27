package de.bitvale.introspector.type.signature;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Objects;

/**
 * @author Patrick Bittner on 13.04.2014.
 */
public final class MethodSignature {

    public final String name;

    private final Type[] parameters;

    public MethodSignature(final String name, final Type[] parameters) {
        this.name = name;
        this.parameters = parameters;
    }

    @Override
    public boolean equals(final Object o) {

        if (this == o) {
            return true;
        }

        if (o instanceof MethodSignature) {
            final MethodSignature other = (MethodSignature) o;

            return Objects.equals(name, other.getName())
                    && Arrays.equals(parameters, other.getParameters());
        }

        return false;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + Arrays.hashCode(parameters);
        return result;
    }

    public String getName() {
        return name;
    }

    public Type[] getParameters() {
        return parameters;
    }
}
