package de.anjunar.introspector.bean;

import de.anjunar.introspector.type.resolved.ResolvedType;
import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * @author Patrick Bittner on 03.05.2014.
 */
public class BeanPropertyResolver<B, T> {

    private static final Map<Class<?>, Object> DEFAULT_VALUES = defaultValues();
    private final String propertyName;
    private String methodName;

    BeanPropertyResolver(final ProxyFactory factory, final ResolvedType<B> type, final Function<B, T> function) {

        @SuppressWarnings("unchecked")
        final Class<B> aClass = type.getRawType();

        if (aClass.isInterface()) {
            factory.setInterfaces(new Class[]{aClass});
        } else {
            factory.setSuperclass(aClass);
        }

        final Class<?> proxyClass = factory.createClass(method -> BeanIntrospector.isGetter(method.getName()) && !method.getReturnType().equals(void.class));

        final ProxyObject proxyInstance;

        try {

            final Optional<? extends Constructor<?>> optional = findConstructor(proxyClass);

            if (optional.isPresent()) {

                final Constructor<?> constructor = optional.get();

                final Object[] parameters = buildConstructorParameters(constructor);

                if (!constructor.isAccessible()) {
                    constructor.setAccessible(true);
                }

                proxyInstance = (ProxyObject) constructor.newInstance(parameters);

            } else {

                proxyInstance = (ProxyObject) proxyClass.newInstance();

            }

        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }

        proxyInstance.setHandler((self, thisMethod, proceed, args) -> {
            methodName = thisMethod.getName();
            return null;
        });

        //noinspection unchecked
        final B beanProxy = (B) proxyInstance;

        function.apply(beanProxy);

        if (methodName == null) {
            throw new IllegalStateException("Could not detect Property Name");
        }

        if (BeanIntrospector.isNormalGetter(methodName)) {
            propertyName = BeanIntrospector.decapitalize(methodName.substring(3));
        } else {
            propertyName = BeanIntrospector.decapitalize(methodName.substring(2));
        }


    }

    private static Optional<? extends Constructor<?>> findConstructor(final Class<?> proxyClass) {
        return Stream.of(proxyClass.getDeclaredConstructors())
                .sorted((lhs, rhs) -> Integer.compare(lhs.getParameterCount(), rhs.getParameterCount()))
                .findFirst();
    }

    private static Map<Class<?>, Object> defaultValues() {
        final Map<Class<?>, Object> result = new HashMap<>();

        result.put(short.class, Short.MIN_VALUE);
        result.put(int.class, Integer.MIN_VALUE);
        result.put(long.class, Long.MIN_VALUE);

        result.put(double.class, Double.MIN_VALUE);
        result.put(float.class, Float.MIN_VALUE);

        return result;
    }

    private static Object[] buildConstructorParameters(final Constructor<?> constructor) {
        final int parameterCount = constructor.getParameterCount();

        final Class<?>[] types = constructor.getParameterTypes();

        final Object[] parameters = new Object[parameterCount];

        for (int index = 0; index < parameters.length; index++) {

            final Class<?> type = types[index];

            final Object defaultValue = DEFAULT_VALUES.get(type);

            parameters[index] = defaultValue;

        }

        return parameters;
    }

    public String getPropertyName() {
        return propertyName;
    }

}
