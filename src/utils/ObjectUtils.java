package utils;

import java.lang.reflect.Field;

public class ObjectUtils {
    public static void copyNonNullFields(Object source, Object destination) {
        Class<?> sourceClass = source.getClass();
        Class<?> destinationClass = destination.getClass();

        for (Field sourceField : sourceClass.getDeclaredFields()) {
            sourceField.setAccessible(true);

            try {
                Object sourceValue = sourceField.get(source);

                if (sourceValue != null) {
                    try {
                        Field destinationField = destinationClass.getDeclaredField(sourceField.getName());
                        destinationField.setAccessible(true);

                        destinationField.set(destination, sourceValue);
                    } catch (NoSuchFieldException e) {
                        System.out.println("Campo não encontrado!\n");
                        e.printStackTrace();
                    }
                }
            } catch (IllegalAccessException e) {
                System.out.println("Acesso não permitido ao campo\n");
                e.printStackTrace();
            }
        }
    }
}
