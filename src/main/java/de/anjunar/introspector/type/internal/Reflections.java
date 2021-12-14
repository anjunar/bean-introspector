package de.anjunar.introspector.type.internal;

import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import de.anjunar.introspector.type.signature.FieldSignature;
import de.anjunar.introspector.type.signature.MethodSignature;
import javassist.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Modifier;
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Patrick Bittner on 13.04.2014.
 */
public final class Reflections {

    private static final Logger log = LoggerFactory.getLogger(Reflections.class);

    public static List<TypeHierarchy<Class<?>, Constructor<?>>> constructors(final Class<?> declaringClass) {
        final List<TypeHierarchy<Class<?>, Constructor<?>>> result = new ArrayList<>();
        for (final Constructor<?> constructor : declaringClass.getDeclaredConstructors()) {
            if (declaringClass.isEnum()) {
                if (Arrays.equals(constructor.getParameterTypes(), new Class<?>[]{String.class, int.class})) {
                    // Filter out Default Enum Constructor
                    continue;
                }
            }
            final TypeHierarchy<Class<?>, Constructor<?>> hierarchy = new TypeHierarchy<>();
            hierarchy.add(declaringClass, constructor);
            result.add(hierarchy);
        }
        return result;
    }

    public static Map<MethodSignature, TypeHierarchy<Class<?>, Method>> methods(final List<Class<?>> hierarchy) {
        ClassPool pool = ClassPool.getDefault();
        final Map<MethodSignature, TypeHierarchy<Class<?>, Method>> result = new LinkedHashMap<>();
        try {
            for (final Class<?> declaringClass : Lists.reverse(hierarchy)) {
                CtClass ctClass = pool.get(declaringClass.getName());
                CtMethod[] ctClassDeclaredMethods = ctClass.getDeclaredMethods();
                Method[] declaredMethods = declaringClass.getDeclaredMethods();
                for (final CtMethod ctClassMethod : ctClassDeclaredMethods) {
                    Method classMethod = convertCtMethodToMethod(declaredMethods, ctClassMethod);
                    if (classMethod != null && !classMethod.isBridge() && ! classMethod.getName().startsWith("lambda")) {
                        final MethodSignature methodSignature = buildMethodSignature(hierarchy, classMethod);
                        TypeHierarchy<Class<?>, Method> methodMap = result.get(methodSignature);
                        if (methodMap == null) {
                            methodMap = new TypeHierarchy<>();
                            result.put(methodSignature, methodMap);
                        }
                        methodMap.add(declaringClass, classMethod);
                    }
                }
                for (final Class<?> declaringInterface : declaringClass.getInterfaces()) {
                    CtClass ctInterface = pool.get(declaringInterface.getName());
                    CtMethod[] ctInterfaceDeclaredMethods = ctInterface.getDeclaredMethods();
                    Method[] interfaceMethods = declaringInterface.getDeclaredMethods();
                    for (final CtMethod ctInterfaceMethod : ctInterfaceDeclaredMethods) {
                        Method interfaceMethod = convertCtMethodToMethod(interfaceMethods, ctInterfaceMethod);
                        if (interfaceMethod != null && !interfaceMethod.isBridge() && ! interfaceMethod.getName().startsWith("lambda")) {
                            final MethodSignature methodSignature = buildMethodSignature(hierarchy, interfaceMethod);
                            TypeHierarchy<Class<?>, Method> methodMap = result.get(methodSignature);
                            if (methodMap == null) {
                                methodMap = new TypeHierarchy<>();
                                result.put(methodSignature, methodMap);
                            }
                            methodMap.add(declaringInterface, interfaceMethod);
                        }
                    }
                }
            }
        } catch (NotFoundException e) {
            log.error(e.getLocalizedMessage());
        }
        return result;
    }

    private static MethodSignature buildMethodSignature(List<Class<?>> hierarchy, Method classMethod) {
        List<Type> parameters = new ArrayList<>();
        for (Type genericParameterType : classMethod.getGenericParameterTypes()) {
            if (genericParameterType instanceof Class) {
                parameters.add(genericParameterType);
            } else {
                parameters.add(TypeToken.of(hierarchy.get(0)).resolveType(genericParameterType).getType());
            }
        }
        return new MethodSignature(classMethod.getName(), parameters.toArray(new Type[parameters.size()]));
    }

    private static Method convertCtMethodToMethod(Method[] declaredMethods, CtMethod ctClassMethod) {
        return Arrays.stream(declaredMethods)
                .filter((method) -> {
                    try {
                        List<String> ctParamTypes = Arrays
                                .stream(ctClassMethod.getParameterTypes())
                                .map(CtClass::getName)
                                .collect(Collectors.toList());
                        List<String> paramTypes = Arrays
                                .stream(method.getParameterTypes())
                                .map(param -> {
                                    if (param.isArray()) {
                                        String stringType = arrayToStringType(param.getComponentType());
                                        return stringType;
                                    }
                                    return param.getName();
                                })
                                .collect(Collectors.toList());
                        return method.getName().equals(ctClassMethod.getName()) && ctParamTypes.equals(paramTypes) && ! method.isBridge();
                    } catch (NotFoundException e) {
                        log.error(e.getLocalizedMessage());
                        return false;
                    }
                })
                .findFirst()
                .orElse(null);
    }

    public static String arrayToStringType(Class<?> type) {
        if (type.isArray()) {
            return arrayToStringType(type.getComponentType()) + "[]";
        } else {
            return type.getName() + "[]";
        }
    }

    public static Map<FieldSignature, TypeHierarchy<Class<?>, Field>> fields(final List<Class<?>> hierarchy) {
        ClassPool pool = ClassPool.getDefault();
        final Map<FieldSignature, TypeHierarchy<Class<?>, Field>> result = new LinkedHashMap<>();
        try {
            for (final Class<?> declaringClass : Lists.reverse(hierarchy)) {
                CtClass ctClass = pool.get(declaringClass.getName());
                CtField[] ctClassDeclaredFields = ctClass.getDeclaredFields();
                for (final CtField ctDeclaredField : ctClassDeclaredFields) {
                    Field declaredField = declaringClass.getDeclaredField(ctDeclaredField.getName());
                    Type field;
                    if (declaredField.getGenericType() instanceof Class) {
                        field = declaredField.getGenericType();
                    } else {
                        field = TypeToken.of(hierarchy.get(0)).resolveType(declaredField.getGenericType()).getRawType();
                    }
                    final FieldSignature fieldSignature = new FieldSignature(declaredField.getName(), field);
                    final TypeHierarchy<Class<?>, Field> typeHierarchy = result.get(fieldSignature);
                    if (typeHierarchy == null) {
                        final TypeHierarchy<Class<?>, Field> newTypeHierarchy = new TypeHierarchy<>();
                        result.put(fieldSignature, newTypeHierarchy);
                        newTypeHierarchy.add(declaringClass, declaredField);
                    } else {
                        if (Modifier.isPrivate(declaredField.getModifiers())) {
                            log.warn(String.format("Hidden Field %s at %s is private and will be dropped", declaredField.getName(), declaringClass.getName()));
                        } else {
                            typeHierarchy.add(declaringClass, declaredField);
                        }
                    }
                }
            }
        } catch (NotFoundException | NoSuchFieldException e) {
            log.error(e.getLocalizedMessage());
        }

        return result;
    }

    public static List<Class<?>> hierarchy(final Class<?> classType) {
        if (classType.isInterface()) {
            return Collections.singletonList(classType);
        }
        final List<Class<?>> result = new ArrayList<>();
        Class<?> cursor = classType;
        while (cursor.getSuperclass() != null) {
            result.add(cursor);
            cursor = cursor.getSuperclass();
        }
        return result;
    }


}
