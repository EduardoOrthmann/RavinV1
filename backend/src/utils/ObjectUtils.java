package utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class ObjectUtils {
    public static <T> void mapToEntity(Object dto, Class<T> entityClass) {
        try {
            T entity = entityClass.getDeclaredConstructor().newInstance();
            Field[] dtoFields = dto.getClass().getDeclaredFields();
            Field[] entityFields = entityClass.getDeclaredFields();

            for (Field dtoField : dtoFields) {
                dtoField.setAccessible(true);
                Object dtoValue = dtoField.get(dto);

                for (Field entityField : entityFields) {
                    if (entityField.getName().equals(dtoField.getName())) {
                        entityField.setAccessible(true);
                        entityField.set(entity, dtoValue);
                        break;
                    }
                }
            }
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException | SecurityException e) {
            e.printStackTrace();
        }
    }
}
