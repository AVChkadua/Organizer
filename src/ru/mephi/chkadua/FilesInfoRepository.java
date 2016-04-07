package ru.mephi.chkadua;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

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
     * Добавляет информацию о файле в хранилище и JSON-файл с проверкой: если файл с указанной категорией и путём
     * уже добавлен, то добавление отменяется. Если совпало только имя, к нему добавляется суффикс
     * @param fileInfo Объект с информацией
     * @return true, если файл был добавлен, false иначе
     * @throws IOException
     */
    public boolean addFile(FileInfo fileInfo) throws IOException {
        boolean alreadyAdded = false;
        for (FileInfo file : filesArrayList) {
            if (fileInfo.getCategory().equals(file.getCategory()) && fileInfo.getPath().equals(file.getPath())) {
                alreadyAdded = true;
                break;
            }
        }
        if (!alreadyAdded) {
            String suffix = "";
            int i = 1;
            while (getFileByName(fileInfo.getCategory(),fileInfo.getName() + suffix) != null) {
                suffix = " (" + i++ + ")";
            }
            fileInfo.setName(fileInfo.getName() + suffix);
            filesArrayList.add(fileInfo);
            InfoParser.addFileInfo(fileInfo);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Изменяет имя файла в хранилище и JSON-файле
     * @param category Категория файла
     * @param oldName Старое имя файла
     * @param newName Новое имя файла
     * @throws IOException
     * @throws IllegalArgumentException
     */
    public void renameFile(String category, String oldName, String newName)
            throws IOException, IllegalArgumentException {
        if (getFileByName(category,newName) != null) {
            throw new IllegalArgumentException();
        } else {
            InfoParser.renameFile(category, oldName, newName);
            FileInfo file = getFileByName(category, oldName);
            file.setName(newName);
        }
    }

    /**
     * Удаляет информацию о файле из хранилища
     * Следует вызывать при удалении файла из JSON-файла
     * @param category Категория файла
     * @param filename Название файла
     * @throws IOException
     */
    public void deleteFile(String category, String filename) throws IOException {
        InfoParser.deleteFileInfo(category, filename);
        FileInfo file = getFileByName(category,filename);
        filesArrayList.remove(file);
    }

    /**
     * Меняет название категории в каждом объекте файлов из заданной категории
     * @param oldName Старое имя категории
     * @param newName Новое имя категории
     * @throws IOException
     * @throws IllegalArgumentException
     */
    public void renameCategory(String oldName, String newName) throws IOException, IllegalArgumentException {
        if (getCategoriesNames().contains(newName)) {
            throw new IllegalArgumentException();
        } else {
            InfoParser.renameCategory(oldName, newName);
            for (FileInfo file : filesArrayList) {
                if (file.getCategory().equals(oldName)) file.setCategory(newName);
            }
        }
    }

    /**
     * Удаляет объекты с информацией всех файлов заданной категории из репозитория
     * @param category Название категории
     * @throws IOException
     */
    public void deleteCategory(String category) throws IOException {
        ArrayList<FileInfo> filesFromCategory = getFilesByCategory(category);
        for (FileInfo file : filesFromCategory) {
            deleteFile(file.getCategory(),file.getName());
        }
        InfoParser.deleteCategory(category);
    }

    /**
     * Меняет путь к файлу
     * @param category Категория файла
     * @param name Имя файла
     * @param newPath Новый путь к файлу
     * @throws IOException
     */
    void changeFilePath(String category, String name, String newPath) throws IOException {
        InfoParser.changeFilePath(category,name,newPath);
        getFileByName(category,name).setPath(newPath);
    }
}
