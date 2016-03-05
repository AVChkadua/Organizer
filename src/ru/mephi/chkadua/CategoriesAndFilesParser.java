package ru.mephi.chkadua;

import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * Класс, отвечающий за парсинг файла с названиями категорий и путями к файлам
 * @author Anton_Chkadua
 */
public class CategoriesAndFilesParser {

    public void addFile(OutputStream out, String name) throws IOException {
        String filename = "categories.txt";
        JsonWriter writer = new JsonWriter(new OutputStreamWriter(out));
        writer.beginObject();
    }
}
