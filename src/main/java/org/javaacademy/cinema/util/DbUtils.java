package org.javaacademy.cinema.util;

import lombok.experimental.UtilityClass;

import java.util.Optional;
import java.util.function.Function;

@UtilityClass
public class DbUtils {

    public <T> T getEntityById(String idString, Function<Integer, Optional<T>> finder) {
        if (idString != null) {
            Integer id = Integer.valueOf(idString);
            return finder.apply(id).orElse(null);
        }
        return null;
    }
}
