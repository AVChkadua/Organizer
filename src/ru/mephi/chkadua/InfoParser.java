package ru.mephi.chkadua;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;

/**
 * Класс, отвечающий за работу с JSON-файлом с информацией о файлах
 * @author Anton_Chkadua
 */
public class InfoParser {

    /**
     * Получает информацию о файлах (название, категорию и путь к файлу) из определённой категории
     * @return info Информация о файлах
     * @throws IOException
     */
    public static ArrayList<FileInfoContainer> getFilesFromCategory(String categoryName) throws IOException {
        ArrayList<FileInfoContainer> filesList = new ArrayList<>();
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
                FileInfoContainer file = new FileInfoContainer();
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
    public static void addFile(FileInfoContainer info) throws IOException {
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
     * Удаляет информацию о файле из JSON-файла
     * @param category Категория файла
     * @param filename Название файла
     * @throws IOException
     */
    public static void deleteFile(String category, String filename) throws IOException {
        SessionContainer container = SessionContainer.getSessionContainer();
        FileInfoContainer file = container.getFileByName(category, filename);
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
     * Получает список всех категорий
     * @return Список категорий
     * @throws IOException
     */
    public static ArrayList<String> getCategoriesList() throws IOException {
        ArrayList<String> categoriesList = new ArrayList<>();
        JsonReader reader = new JsonReader(new FileReader("categories.txt"));
        reader.beginArray();
        while (reader.hasNext()) {
            reader.beginObject();
            reader.nextName();
            reader.nextString();
            reader.nextName();
            String category = reader.nextString();
            reader.nextName();
            reader.nextString();
            reader.endObject();
            if (!categoriesList.contains(category)) {
                categoriesList.add(category);
            }
        }
        reader.endArray();
        return categoriesList;
    }
}
