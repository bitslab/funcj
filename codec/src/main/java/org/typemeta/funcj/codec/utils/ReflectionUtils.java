package org.typemeta.funcj.codec.utils;

import org.typemeta.funcj.util.Exceptions;

import java.lang.reflect.*;
import java.util.*;

import static java.util.stream.Collectors.toList;

/**
 * Utility methods relating to Reflection.
 */
@SuppressWarnings("unchecked")
public abstract class ReflectionUtils {
    private static final int ENUM      = 0x00004000;

    /**
     * Determine whether a class is an enum type.
     * @param clazz     the class to check
     * @return          true if the class is an enum type.
     */
    public static boolean isEnumSubType(Class<?> clazz) {
        return (clazz.getModifiers() & ENUM) != 0 &&
                (clazz.getSuperclass() != null &&
                        clazz.getSuperclass().getSuperclass() == java.lang.Enum.class
                );
    }

    /**
     * Return the {@link Class} for the given name.
     * Alternative to {@link Class#forName(String)}, that throws a {@link RuntimeException}.
     * @param className the class name
     * @return          the class
     */
    public static Class<?> forName(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Data structure representing the type arguments of a generic type.
     */
    public static class TypeArgs {
        public final List<Class<?>> typeArgs;

        public TypeArgs(List<Class<?>> typeArgs) {
            this.typeArgs = typeArgs;
        }

        public TypeArgs() {
            this.typeArgs = Collections.emptyList();
        }

        public int size() {
            return typeArgs.size();
        }

        public Class<?> get(int index) {
            if (index < typeArgs.size()) {
                return typeArgs.get(index);
            } else {
                return Object.class;
            }
        }
    }

    /**
     * Inspect the given field. If it implements the supplied generic interface
     * then extract and return the type arguments.
     * @param field     the field to inspect
     * @param iface     the generic interface
     * @return          the type arguments
     */
    public static TypeArgs getTypeArgs(Field field, Class<?> iface) {
        final Type type = field.getGenericType();
        if (type instanceof ParameterizedType) {
            final ParameterizedType pt = (ParameterizedType) type;
            if (pt.getRawType() instanceof Class &&
                    iface.isAssignableFrom((Class)pt.getRawType())) {
                final Type[] typeArgs = pt.getActualTypeArguments();
                final List<Class<?>> results = new ArrayList<>(typeArgs.length);
                for (Type typeArg : pt.getActualTypeArguments()) {
                    if (typeArg instanceof Class) {
                        results.add((Class<?>) typeArg);
                    } else if (typeArg instanceof TypeVariable) {
                        final TypeVariable<?> tv = (TypeVariable<?>) typeArg;
                        final Type[] bounds = tv.getBounds();
                        if (bounds.length == 1 && bounds[0] instanceof Class) {
                            results.add((Class<?>) bounds[0]);
                        } else {
                            results.add(Object.class);
                        }
                    } else {
                        results.add(Object.class);
                    }
                }

                return new TypeArgs(results);
            }
        }

        return new TypeArgs();
    }

    /**
     * Inspect the supplied type. If it implements the supplied generic interface
     * then extract and return the type arguments.
     * @param implClass the type to inspect
     * @param iface     the generic interface
     * @return          the type arguments
     */
    public static TypeArgs getTypeArgs(Class<?> implClass, Class<?> iface) {
        final List<ParameterizedType> genIfaces =
                Arrays.stream(implClass.getGenericInterfaces())
                        .filter(t -> t instanceof ParameterizedType)
                        .map(t -> (ParameterizedType) t)
                        .collect(toList());
        return genIfaces.stream()
                .filter(pt -> pt.getRawType() instanceof Class)
                .filter(pt -> iface.isAssignableFrom((Class)pt.getRawType()))
                .findFirst()
                .map(ReflectionUtils::getGenTypeArgs)
                .orElseGet(TypeArgs::new);
    }

    private static TypeArgs getGenTypeArgs(ParameterizedType type) {
        final List<Class<?>> typeArgs =
                Arrays.stream(type.getActualTypeArguments())
                        .filter(t -> t instanceof Class)
                        .map(t -> (Class<?>)t)
                        .collect(toList());
        return new TypeArgs(typeArgs);
    }
}
