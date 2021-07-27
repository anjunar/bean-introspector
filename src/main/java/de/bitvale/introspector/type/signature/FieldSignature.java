package de.bitvale.introspector.type.signature;

import java.lang.reflect.Type;
import java.util.Objects;

/**
 * @author Patrick Bittner on 13.04.2014.
 */
public final class FieldSignature {

    private final String name;

    private final Type type;

    public FieldSignature(final String name, final Type type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public boolean equals(final Object o) {

        if (this == o) {
            return true;
        }


        if (o instanceof FieldSignature) {

            final FieldSignature other = (FieldSignature) o;

            return Objects.equals(name, other.getName())
                    && Objects.equals(type, other.getType());

        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + type.hashCode();
        return result;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }
}
