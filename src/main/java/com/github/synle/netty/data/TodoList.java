package com.github.synle.netty.data;

import java.util.ArrayList;
import java.util.List;

public class TodoList {
    private static List<ToDo> todos = new ArrayList<>();

    public static ToDo addTodo(String message, String status) {
        ToDo newTodo = new ToDo(message, status);
        todos.add(newTodo);
        return newTodo;
    }

    public static void setTodo(int todoIndex, ToDo targetToDo) {
        todos.set(todoIndex, targetToDo);
    }

    public static ToDo getTodo(int index) {
        return todos.get(index);
    }

    public static List<ToDo> getAll() {
        return todos;
    }

    public static int size() {
        return todos.size();
    }

}
