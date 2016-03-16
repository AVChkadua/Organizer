package ru.mephi.chkadua;

import java.util.ArrayList;

/**
 * Класс, хранящий категорию и информацию о файлах, которые в неё входят
 */
public class Category {
    private String name;
    private ArrayList<String> filesNames = new ArrayList<>();
    private ArrayList<String> paths = new ArrayList<>();

    public Category(String name, ArrayList<FileInfoContainer> info) {
        this.name = name;
        for (FileInfoContainer file : info) {
            this.filesNames.add(file.getName());
            this.paths.add(file.getPath());
        }
    }

    /**
     * Получает массив имён файлов
     * @return массив имён
     */
    public String[] getNames() {
        String[] namesArray = new String[filesNames.size()];
        namesArray = filesNames.toArray(namesArray);
        return namesArray;
    }

    /**
     * Получает массив путей к файлам
     * @return массив путей
     */

    public String[] getPaths() {
        String[] pathsArray = new String[paths.size()];
        pathsArray = paths.toArray(pathsArray);
        return pathsArray;
    }

    /**
     * Получает имя категории
     * @return имя категории
     */
    public String getName() {
        return name;
    }
}
