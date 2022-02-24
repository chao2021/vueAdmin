package com.xxxx.utils;

import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BeanCopyUtil {

    public static <V> V copyProperties(Object source, Class<V> clazz) {
        V result = null;
        try {
            result = clazz.newInstance();
            BeanUtils.copyProperties(source, result);
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static <V, O> List<V> copyList(List<O> list, Class<V> clazz) {
        List<V> result = new ArrayList<>();

        try {
            result = list.stream()
                    .map(o -> copyProperties(o, clazz))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }


}
