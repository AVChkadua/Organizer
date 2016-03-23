package ru.mephi.chkadua;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.util.ArrayList;

/**
 * Класс, отвечающий за работу с JSON-файлом с информацией о файлах
 * @author Anton_Chkadua
 */
public class InfoParser {

    /**
     * Получает информацию о файле по его имени и категории
     * @param categoryName Название категории
     * @param filename Название файла
     * @return Объект с информацией о файле или null, если такой файл не найден
     * @throws IOException
     */
    private static FileInfo getFileInfo(String categoryName, String filename) throws IOException {
        JsonReader reader = new JsonReader(new FileReader("categories.txt"));
        FileInfo file = null;
        reader.beginArray();
        while (reader.hasNext()) {
            reader.beginObject();
            reader.nextName();
            String name = reader.nextString();
            reader.nextName();
            String category = reader.nextString();
            reader.nextName();
            String path = reader.nextString();
            reader.endObject();
            if (category.equals(categoryName) && name.equals(filename)) {
                file = new FileInfo();
                file.setName(name);
                file.setCategory(category);
                file.setPath(path);
            }
        }
        reader.endArray();
        return file;
    }

    /**
     * Получает информацию обо всех файлах из JSON-файла
     * @return Массив объектов с информацией о файле (может быть пустым, если JSON-файл пуст)
     * @throws IOException
     */
    static ArrayList<FileInfo> getAllFilesInfo() throws IOException {
        JsonReader reader = new JsonReader(new FileReader("categories.txt"));
        ArrayList<FileInfo> filesList = new ArrayList<>();
        reader.beginArray();
        while (reader.hasNext()) {
            reader.beginObject();
            reader.nextName();
            String name = reader.nextString();
            reader.nextName();
            String category = reader.nextString();
            reader.nextName();
            String path = reader.nextString();
            reader.endObject();
            FileInfo file = new FileInfo();
            file.setName(name);
            file.setCategory(category);
            file.setPath(path);
            filesList.add(file);
        }
        reader.endArray();
        return filesList;
    }

    /**
     * Получает информацию о файлах (название, категорию и путь к файлу) из определённой категории
     * @param categoryName Название категории
     * @return Массив объектов с информацией о файлах из данной категории (может быть пустым, если файлы указанной
     * категории не найдены)
     * @throws IOException
     */
    private static ArrayList<FileInfo> getFilesByCategory(String categoryName) throws IOException {
        ArrayList<FileInfo> filesList = new ArrayList<>();
        JsonReader reader = new JsonReader(new FileReader("categories.txt"));
        reader.beginArray();
        while (reader.hasNext()) {
            reader.beginObject();
            reader.nextName();
            String name = reader.nextString();
            reader.nextName();
            String category = reader.nextString();
            reader.nextName();
            String path = reader.nextString();
            reader.endObject();
            if (category.equals(categoryName)) {
                FileInfo file = new FileInfo();
                file.setName(name);
                file.setCategory(category);
                file.setPath(path);
                filesList.add(file);
            }
        }
        reader.endArray();
        return filesList;
    }

    /**
     * Добавляет информацию о файле в JSON-файл
     * @param info Объект с информацией о файле
     * @throws IOException
     */
    public static void addFileInfo(FileInfo info) throws IOException {
        JsonParser parser = new JsonParser();
        JsonElement jsonElement = parser.parse(new FileReader("categories.txt"));
        JsonArray array = jsonElement.getAsJsonArray();
        JsonObject object = new JsonObject();
        object.addProperty("name", info.getName());
        object.addProperty("category", info.getCategory());
        object.addProperty("path", info.getPath());
        array.add(object);
        try (Writer writer = new FileWriter("categories.txt")) {
            Gson gson = new GsonBuilder().create();
            gson.toJson(array, writer);
        }
    }

    /**
     * Перезаписывает информацию о файле в JSON-файл с новым именем
     * @param category Категория файла
     * @param oldName Старое имя файла
     * @param newName Новое имя файла
     * @throws IOException
     */
    public static void renameFile(String category, String oldName, String newName) throws IOException {
        FileInfo file = getFileInfo(category,oldName);
        deleteFileInfo(category,oldName);
        FileInfo newInfo = new FileInfo(newName,file.getCategory(),file.getPath());
        addFileInfo(newInfo);
    }

    /**
     * Удаляет информацию о файле из JSON-файла
     * @param category Категория файла
     * @param filename Название файла
     * @throws IOException
     */
    public static void deleteFileInfo(String category, String filename) throws IOException {
        FileInfo file = getFileInfo(category, filename);
        JsonObject object = new JsonObject();
        object.addProperty("name", filename);
        object.addProperty("category", category);
        object.addProperty("path", file.getPath());
        JsonParser parser = new JsonParser();
        JsonElement jsonElement = parser.parse(new FileReader("categories.txt"));
        JsonArray array = jsonElement.getAsJsonArray();
        array.remove(object);
        try (Writer writer = new FileWriter("categories.txt")) {
            Gson gson = new GsonBuilder().create();
            gson.toJson(array, writer);
        }
    }

    /**
     * Переименовывает категорию во всех файлах из указанной
     * @param oldName Старое имя категории
     * @param newName Новое имя категории
     * @throws IOException
     */
    public static void renameCategory(String oldName, String newName) throws IOException {
        ArrayList<FileInfo> filesFromCategory = getFilesByCategory(oldName);
        for (FileInfo file : filesFromCategory) {
            FileInfo newFile = new FileInfo(file.getName(),newName,file.getPath());
            deleteFileInfo(file.getCategory(),file.getName());
            addFileInfo(newFile);
        }
    }

    /**
     * Удаляет все файлы из указанной категории
     * @param category Название категории
     * @throws IOException
     */
    public static void deleteCategory(String category) throws IOException {
        ArrayList<FileInfo> filesFromCategory = getFilesByCategory(category);
        for (FileInfo file : filesFromCategory) {
            deleteFileInfo(category, file.getName());
        }
    }

    /**
     * Создаёт файл-базу, если он не создан
     * @throws IOException
     */
    public static void createFileIfNotExists() throws IOException {
        File file = new File("categories.txt");
        if (file.createNewFile()) {
            try (OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(file))){
                out.write("[]");
                out.close();
            }
        }
    }
}
