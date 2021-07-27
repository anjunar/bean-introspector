package de.bitvale.introspector.bean;

import com.google.common.cache.Cache;
import com.google.common.reflect.TypeToken;
import de.bitvale.introspector.TCCLCache;
import de.bitvale.introspector.type.TypeResolver;
import de.bitvale.introspector.type.resolved.ResolvedField;
import de.bitvale.introspector.type.resolved.ResolvedMethod;
import de.bitvale.introspector.type.resolved.ResolvedType;

import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

/**
 * @author Patrick Bittner on 12.04.2014.
 */
public final class BeanIntrospector {

    public static final String SETTER_PREFIX = "set";
    public static final String GETTER_PREFIX = "get";
    public static final String BOOLEAN_GETTER_PREFIX = "is";
    private static final Cache<Class<?>, BeanModel<?>> classCache = new TCCLCache<>();
    private static final Cache<TypeToken<?>, BeanModel<?>> typeCache = new TCCLCache<>();

    /**
     * A Class to create a BeanModel with its properties
     * @param beanClass A Class
     * @param <X> The type of that Class
     * @return The Bean Model
     */
    public static <X> BeanModel<X> create(final Class<X> beanClass) {
        try {
            @SuppressWarnings("unchecked")
            final BeanModel<X> beanModel = (BeanModel<X>) classCache.get(beanClass, () -> create(TypeResolver.resolve(beanClass)));

            return beanModel;

        } catch (final ExecutionException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Create a Bean Model with a {@link TypeToken} it is used for Generics
     * @param beanClass The Type Token
     * @param <X> The Type of that Token
     * @return A Bean Model
     */
    public static <X> BeanModel<X> create(final TypeToken<X> beanClass) {
        try {
            @SuppressWarnings("unchecked")
            final BeanModel<X> beanModel = (BeanModel<X>) typeCache.get(beanClass, () -> create(TypeResolver.resolve(beanClass)));

            return beanModel;

        } catch (final ExecutionException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Create a Bean Model with a Resovled Type from {@link TypeResolver}
     * @param type The Resolved Type
     * @param <X> The Type of that Resolved Type
     * @return A Bean Model
     */
    public static <X> BeanModel<X> create(final ResolvedType<X> type) {

        final LinkedHashMap<String, BeanProperty<X, ?>> properties = new LinkedHashMap<>();
        final BeanModel<X> beanModel = new BeanModel<>(type, properties);
        properties.putAll(buildProperties(type, beanModel));

        return beanModel;
    }


    private static <X> LinkedHashMap<String, BeanProperty<X, ?>> buildProperties(final ResolvedType<X> type,
                                                                                 final BeanModel<X> beanModel) {

        final Map<String, List<ResolvedMethod<X>>> propertiesMap = type.getMethods()
                .stream()
                .filter(BeanIntrospector::isBeanProperty)
                .collect(Collectors.groupingBy(BeanIntrospector::propertyName, LinkedHashMap::new, Collectors.toList()));


        return propertiesMap.entrySet()
                .stream()
                .map(entry -> create(beanModel, type, entry))
                .collect(Collectors.toMap(BeanProperty::getKey, v -> v, throwingMerger(), LinkedHashMap::new));
    }

    private static <X> BinaryOperator<X> throwingMerger() {
        return (u, v) -> {
            throw new IllegalStateException(String.format("Duplicate key %s", u));
        };
    }


    private static <X> BeanProperty<X, ?> create(final BeanModel<X> model, final ResolvedType<X> type, final Map.Entry<String, List<ResolvedMethod<X>>> entry) {

        final String name = entry.getKey();

        final Optional<ResolvedMethod<X>> optionalGetter = entry.getValue()
                .stream()
                .filter(method -> isGetter(method.getName()))
                .findFirst();

        final Optional<ResolvedMethod<X>> optionalSetter = entry.getValue()
                .stream()
                .filter(method -> isSetter(method.getName()))
                .findFirst();

        final Optional<ResolvedField<X>> optionalField = type.getFields()
                .stream()
                .filter(field -> field.getName().equals(name) && field.getType().equals(optionalGetter.get().getReturnType()))
                .findFirst();

        return BeanProperty.create(name, model, optionalField, optionalGetter, optionalSetter);

    }

    private static <X> String propertyName(final ResolvedMethod<X> method) {
        final String methodName = method.getName();

        if (isNormalGetter(methodName)) {
            return decapitalize(methodName.substring(3));
        }

        if (isBooleanGetter(methodName)) {
            return decapitalize(methodName.substring(2));
        }

        if (isSetter(methodName)) {
            return decapitalize(methodName.substring(3));
        }

        throw new IllegalStateException("Bad method name " + methodName);
    }

    private static <X> boolean isBeanProperty(final ResolvedMethod<X> element) {

        if (Modifier.isStatic(element.getModifiers())) {
            return false;
        }

        if (element.getEnclosingType().getRawType().equals(Object.class)) {
            return false;
        }

        if (Modifier.isPublic(element.getModifiers())) {

            final String name = element.getName();
            final Class<?> returnType = element.getReturnType().getRawType();
            final int parameterSize = element.getParameters().size();

            if (isNormalGetter(name)) {
                return !void.class.equals(returnType) && parameterSize == 0;
            }

            if (isBooleanGetter(name)) {
                return boolean.class.equals(returnType) && returnType.isPrimitive() && parameterSize == 0;
            }

            if (isSetter(name)) {
                return void.class.equals(returnType) && parameterSize == 1;
            }

        }

        return false;
    }


    public static boolean isSetter(final String methodName) {
        return methodName.startsWith(SETTER_PREFIX);
    }

    public static boolean isBooleanGetter(final String methodName) {
        return methodName.startsWith(BOOLEAN_GETTER_PREFIX);
    }

    public static boolean isNormalGetter(final String methodName) {
        return methodName.startsWith(GETTER_PREFIX);
    }

    public static boolean isGetter(final String methodName) {
        return isNormalGetter(methodName) || isBooleanGetter(methodName);
    }

    public static String decapitalize(final String name) {
        if (name == null || name.isEmpty()) {
            return name;
        }
        if (name.length() > 1 && Character.isUpperCase(name.charAt(1)) &&
                Character.isUpperCase(name.charAt(0))) {
            return name;
        }
        final char[] chars = name.toCharArray();
        chars[0] = Character.toLowerCase(chars[0]);
        return new String(chars);
    }


}
