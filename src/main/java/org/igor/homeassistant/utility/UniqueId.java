package org.igor.homeassistant.utility;

import org.igor.homeassistant.exception.DataAccessException;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

public class UniqueId {

    public static <U> U createWithUID(Class<U> clazz) throws DataAccessException {
        U uniqueObject = null;
        try {
            uniqueObject = clazz.getDeclaredConstructor(String.class)
                                .newInstance(createUID());
        } catch(InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new DataAccessException("Can not create a new object with a unique id for class " + clazz.getName(),
                                          e);
        }
        return uniqueObject;
    }

    private static String createUID() {
        return UUID.randomUUID()
                   .toString();
    }
}
