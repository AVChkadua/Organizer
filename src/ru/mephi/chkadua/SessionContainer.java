package ru.mephi.chkadua;

import java.util.ArrayList;

/**
 * Класс, хранящий все объекты категорий сессии
 */
public class SessionContainer {
    private static SessionContainer instance;
    private ArrayList<Category> categoriesArrayList;

    private SessionContainer() {
        this.categoriesArrayList = new ArrayList<>();
    }

    /**
     * Возвращает объект контейнера или создаёт его, если он ещё не создан
     * @return объект контейнера
     */
    public static SessionContainer getSessionContainer() {
        if (instance == null) {
            instance = new SessionContainer();
        }
        return instance;
    }

    /**
     * Добавляет список категорий в контейнер
     * @param categories список категорий
     */
    public void setCategories(ArrayList<Category> categories) {
        this.categoriesArrayList = categories;
    }
}
