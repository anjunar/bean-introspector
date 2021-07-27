package de.bitvale.introspector.type.resolved;

import com.google.common.reflect.TypeToken;

import java.util.List;

/**
 * @author Patrick Bittner on 04.05.2014.
 */
public interface ResolvedType<X> extends ResolvedAnnotatedElement {
    TypeToken<X> getType();

    Class<X> getRawType();

    List<ResolvedConstructor<X>> getConstructors();

    List<ResolvedField<X>> getFields();

    List<ResolvedMethod<X>> getMethods();

    ResolvedMethod<?> find(String name, Class<?>... parameters);
}
