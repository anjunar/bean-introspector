package de.anjunar.introspector.type.resolved;

import com.google.common.reflect.TypeToken;

import java.lang.reflect.Method;

/**
 * @author Patrick Bittner on 04.05.2014.
 */
public interface ResolvedMethod<X> extends ResolvedExecutable<X> {

    Object invoke(X instance, Object... args);

    TypeToken<?> getReturnType();

    boolean equalSignature(Method method);

}
