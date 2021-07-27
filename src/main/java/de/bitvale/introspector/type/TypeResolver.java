package de.bitvale.introspector.type;

import com.google.common.cache.Cache;
import com.google.common.reflect.TypeToken;
import de.bitvale.introspector.TCCLCache;
import de.bitvale.introspector.type.internal.Reflections;
import de.bitvale.introspector.type.resolved.ResolvedConstructor;
import de.bitvale.introspector.type.resolved.ResolvedField;
import de.bitvale.introspector.type.resolved.ResolvedMethod;
import de.bitvale.introspector.type.resolved.ResolvedType;
import de.bitvale.introspector.type.resolved.generic.GenericResolvedType;
import de.bitvale.introspector.type.resolved.raw.RawResolvedConstructor;
import de.bitvale.introspector.type.resolved.raw.RawResolvedField;
import de.bitvale.introspector.type.resolved.raw.RawResolvedMethod;
import de.bitvale.introspector.type.resolved.raw.RawResolvedType;
import javassist.ClassPool;
import javassist.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * @author Patrick Bittner on 13.04.2014.
 *
 * This Typeresolver resovles all methods, fields and constructors of the given Class. With help of Javaassist the
 * right order of methods and fields are given. The hierarchy of the methods and fields are kept in that resolver.
 */
public final class TypeResolver {

    private static final Logger log = LoggerFactory.getLogger(TypeResolver.class);

    private static final Cache<Class<?>, ResolvedType<?>> cache = new TCCLCache<>();

    /**
     * Inspects the given Class to a Resolved Type. The given class is mostly a real Class.
     * @param classType A Class
     * @param <X> The Type of the given Class
     * @return The Resolved Type
     */
    public static <X> ResolvedType<X> resolve(final Class<X> classType) {
        try {
            @SuppressWarnings("unchecked")
            final ResolvedType<X> resolvedType = (ResolvedType<X>) cache.get(classType, () -> resolveInternal(classType));
            return resolvedType;
        } catch (final ExecutionException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * This method is used for Generics with a {@link TypeToken}
     * @param typeToken The type Token
     * @param <X> The Type of the Type Token
     * @return The Resolved Type
     */
    public static <X> ResolvedType<X> resolve(final TypeToken<X> typeToken) {
        @SuppressWarnings("unchecked")
        final Class<X> rawType = (Class<X>) typeToken.getRawType();

        final ResolvedType<X> resolvedType = resolve(rawType);

        return new GenericResolvedType<>(typeToken, resolvedType);
    }

    /**
     * Internal usage to generate the {@link ResolvedType}
     * @param classType The Class file to resolve
     * @param <X> The Type of the Class File
     * @return A resolved Type
     */
    private static <X> ResolvedType<X> resolveInternal(final Class<X> classType) {

        final List<ResolvedConstructor<X>> resolvedConstructors = new ArrayList<>();

        final List<ResolvedField<X>> resolvedFields = new ArrayList<>();

        final List<ResolvedMethod<X>> resolvedMethods = new ArrayList<>();

        final List<Class<?>> hierarchy = Reflections.hierarchy(classType);

        final ResolvedType<X> resolvedType = RawResolvedType.create(classType, hierarchy, resolvedConstructors, resolvedFields, resolvedMethods);

        resolvedConstructors.addAll(
                Reflections.constructors(classType)
                        .stream()
                        .map(typeHierarchy -> RawResolvedConstructor.create(resolvedType, typeHierarchy))
                        .collect(Collectors.toList())
        );

        resolvedFields.addAll(
                Reflections.fields(hierarchy)
                        .entrySet()
                        .stream()
                        .map(entry -> RawResolvedField.create(entry.getKey().getName(), resolvedType, entry.getValue()))
                        .collect(Collectors.toList())
        );

        resolvedMethods.addAll(
                Reflections.methods(hierarchy)
                        .entrySet()
                        .stream()
                        .map(entry -> RawResolvedMethod.create(entry.getKey().getName(), resolvedType, entry.getValue()))
                        .collect(Collectors.toList())
        );

        return resolvedType;
    }

}
