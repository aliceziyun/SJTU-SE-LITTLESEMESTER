package com.example.myapplication.options;

public interface Data<T> {
    /**
     * 添加元素
     *
     * @param values
     * @return
     */
    T data(Object... values);
}
