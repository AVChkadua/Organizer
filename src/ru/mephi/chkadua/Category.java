package ru.mephi.chkadua;

import java.util.ArrayList;

/**
 * Класс, хранящий категорию и информацию о файлах, которые в неё входят
 */
public class Category {

    private String name;
    private ArrayList<FileInfoContainer> files = new ArrayList<>();

    public Category(String name, ArrayList<FileInfoContainer> info) {
        this.name = name;
        for (FileInfoContainer file : info) {
            this.files.add(file);
        }
    }

    /**
     * Геттер списка файлов категории
     * @return Список файлов
     */
    public ArrayList<FileInfoContainer> getFiles() {
        return files;
    }

    /**
     * Получает массив имён файлов
     * @return Массив имён
     */
    public String[] getNames() {
        String[] namesArray = new String[files.size()];
        ArrayList<String> filesNames = new ArrayList<>();
        for (FileInfoContainer file : files) {
            filesNames.add(file.getName());
        }
        namesArray = filesNames.toArray(namesArray);
        return namesArray;
    }

    /**
     * Получает массив путей к файлам
     * @return Массив путей
     */

    public String[] getPaths() {
        String[] pathsArray = new String[files.size()];
        ArrayList<String> filesPaths = new ArrayList<>();
        for (FileInfoContainer file : files) {
            filesPaths.add(file.getPath());
        }
        pathsArray = filesPaths.toArray(pathsArray);
        return pathsArray;
    }

    /**
     * Получает имя категории
     * @return Имя категории
     */
    public String getName() {
        return name;
    }
}
