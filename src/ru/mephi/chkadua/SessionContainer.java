package ru.mephi.chkadua;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Контейнер объектов категорий текущей сессии
 */
public class SessionContainer {
    private static SessionContainer instance;
    private ArrayList<Category> categoriesArrayList;

    private SessionContainer() {
        this.categoriesArrayList = new ArrayList<>();
    }

    /**
     * Возвращает объект контейнера или создаёт его, если он ещё не создан
     * @return Объект контейнера
     */
    public static SessionContainer getSessionContainer() {
        if (instance == null) {
            instance = new SessionContainer();
        }
        return instance;
    }

    /**
     * Получает список названий всех категорий
     * @return Названия категорий
     */
    public ArrayList<String> getCategoriesNames() {
        ArrayList<String> names = new ArrayList<>();
        for (Category category : categoriesArrayList) {
            names.add(category.getName());
        }
        return names;
    }

    /**
     * Загружает в контейнер объекты всех категорий. Необходимо вызывать при каждом изменении JSON-файла.
     * @throws IOException
     */
    public void getFilesAndCategories() throws IOException {
        this.categoriesArrayList.clear();
        ArrayList<String> categoriesList = InfoParser.getCategoriesList();
        for (String categoryName : categoriesList) {
            ArrayList<FileInfoContainer> filesInfo = InfoParser.getFilesFromCategory(categoryName);
            Category category = new Category(categoryName, filesInfo);
            this.categoriesArrayList.add(category);
        }
    }

    /**
     * Получает список файлов определённой категории из контейнера
     * @param categoryName Название категории
     * @return Список файлов или null, если такая категория не найдена
     */
    public ArrayList<FileInfoContainer> getFilesByCategoryName(String categoryName) {
        for (Category category : categoriesArrayList) {
            if (category.getName().equals(categoryName)) {
                ArrayList<FileInfoContainer> files = new ArrayList<>();
                for (FileInfoContainer file : category.getFiles()) {
                    files.add(file);
                }
                return files;
            }
        }
        return null;
    }

    /**
     * Возвращает объект категории с указанным названием
     * @param categoryName Название категории
     * @return Объект категории или null, если такая категория не найдена
     */
    public Category getCategoryByName(String categoryName) {
        for (Category category : categoriesArrayList) {
            if (category.getName().equals(categoryName)) {
                return category;
            }
        }
        return null;
    }

    /**
     * Возвращает объект с информацией о файле с указанным именем из указанной категории
     * @param categoryName Название категории
     * @param filename Название файла
     * @return Информация о файле или null, если такой файл не найден
     */
    public FileInfoContainer getFileByName(String categoryName, String filename) {
        Category category = getCategoryByName(categoryName);
        for (FileInfoContainer file : category.getFiles()) {
            if (file.getName().equals(filename)) {
                return file;
            }
        }
        return null;
    }
}
