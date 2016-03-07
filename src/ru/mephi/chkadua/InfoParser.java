package ru.mephi.chkadua;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.util.ArrayList;

/**
 * Класс, отвечающий за парсинг файла с названиями категорий и путями к файлам
 * @author Anton_Chkadua
 */
public class InfoParser {

    /**
     * Метод, извлекающий информацию о материалах (название, категорию и путь к файлу)
     * @return info Динамический массив с информацией
     * @throws IOException
     */
    public ArrayList<FileInfoContainer> getFilesInfo() throws IOException {
        ArrayList<FileInfoContainer> info = new ArrayList<>();
        JsonReader reader = new JsonReader(new FileReader("categories.txt"));
        reader.beginArray();
        while (reader.hasNext()) {
            reader.beginObject();
            FileInfoContainer file = new FileInfoContainer();
            file.setName(reader.nextString());
            file.setCategory(reader.nextString());
            file.setPath(reader.nextString());
            reader.endObject();
            info.add(file);
        }
        reader.endArray();
        return info;
    }

    /**
     * Метод, добавляющий информацию о файле в JSON-файл
     * @param info Объект с информацией о материале
     * @throws IOException
     */
    public void addFile(FileInfoContainer info) throws IOException {
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
}
