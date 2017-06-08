package net.yuanmomo;

import net.yuanmomo.springboot.bean.Demo;

import java.lang.reflect.Field;

/**
 * Created by Hongbin.Yuan on 2017-04-04 23:59.
 */

public class Temp {
    public static void main(String[] args) {
        Field[] fields = Demo.class.getDeclaredFields();
        for (Field field : fields) {
            System.out.println(field.getName());
        }
    }
}
