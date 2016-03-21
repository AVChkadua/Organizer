package ru.mephi.chkadua;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
// TODO сделать переименование категорий
/**
 *  Хранилище информации о файлах
 */
public class FilesInfoRepository {
    private static FilesInfoRepository instance;
    private ArrayList<FileInfo> filesArrayList;

    private FilesInfoRepository() {
        this.filesArrayList = new ArrayList<>();
    }

    /**
     * Возвращает объект контейнера или создаёт его, если он ещё не создан
     * @return Объект контейнера
     */
    public static FilesInfoRepository getFilesInfoRepository() {
        if (instance == null) {
            instance = new FilesInfoRepository();
        }
        return instance;
    }

    /**
     * Получает множество названий всех категорий
     * @return Названия категорий
     */
    public Set<String> getCategoriesNames() {
        Set<String> names = new HashSet<>();
        for (FileInfo file : filesArrayList) {
            names.add(file.getCategory());
        }
        return names;
    }

    /**
     * Загружает в контейнер объекты всех файлов, находящихся в JSON-файле
     * Необходимо вызывать при запуске приложения
     * @throws IOException
     */
    public void getFiles() throws IOException {
        this.filesArrayList.clear();
        this.filesArrayList.addAll(InfoParser.getAllFilesInfo());
    }

    /**
     * Получает список файлов определённой категории из контейнера
     * @param category Название категории
     * @return Список файлов или null, если такая категория не найдена
     */
    public ArrayList<FileInfo> getFilesByCategory(String category) {
        ArrayList<FileInfo> files = new ArrayList<>();
        for (FileInfo file : filesArrayList) {
            if (file.getCategory().equals(category)) {
                files.add(file);
            }
        }
        if (files.isEmpty()) return null;
        return files;
    }

    /**
     * Возвращает объект с информацией о файле с указанным именем из указанной категории
     * @param category Название категории
     * @param filename Название файла
     * @return Информация о файле или null, если такой файл не найден
     */
    public FileInfo getFileByName(String category, String filename) {
        for (FileInfo file : filesArrayList) {
            if ((file.getName().equals(filename)) && (file.getCategory().equals(category))) {
                return file;
            }
        }
        return null;
    }

    /**
     * Добавляет информацию о файле в хранилище
     * Следует вызывать при добавлении файла в JSON-файл
     * @param fileInfo Объект с информацией
     */
    public void addFile(FileInfo fileInfo) {
        filesArrayList.add(fileInfo);
    }

    /**
     * Изменяет имя файла в хранилиище
     * @param category Категория файла
     * @param oldName Старое имя файла
     * @param newName Новое имя файла
     */
    public void renameFile(String category, String oldName, String newName) {
        FileInfo file = getFileByName(category,oldName);
        filesArrayList.remove(file);
        file.setName(newName);
        filesArrayList.add(file);
    }

    /**
     * Удаляет информацию о файле из хранилища
     * Следует вызывать при удалении файла из JSON-файла
     * @param category Категория файла
     * @param filename Название файла
     */
    public void removeFile(String category, String filename) {
        FileInfo file = getFileByName(category,filename);
        filesArrayList.remove(file);
    }

    public void printRepo() {
        for (FileInfo file : filesArrayList) {
            System.out.println(file.getCategory() + ":" + file.getName() + ":" + file.getPath());
        }
    }
}
