package com.covercorp.holosports.shared.util;

import java.lang.reflect.Method;

public class EnumUtil {
    public static <T extends Enum<?>> T[] getValues(Class<T> clazz) {
        try {
            Method method = clazz.getMethod("values");
            return (T[]) method.invoke(null);
        } catch (ReflectiveOperationException ex) {
            throw new RuntimeException(ex);
        }
    }
}
