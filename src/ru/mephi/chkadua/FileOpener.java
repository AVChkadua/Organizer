package ru.mephi.chkadua;

import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * Класс, отвечающий за открытие файлов в стандартной программе для просмотра файла указанного формата.
 */
public class FileOpener {

    /**
     * Открывает файл в стандартной программе просмотра, ассоциированной с данным форматом
     * @param fileInfo Объект с информацией о файле
     * @throws IOException
     */
    public static void openFile(FileInfo fileInfo) throws IOException {
        Desktop.getDesktop().open(new File(fileInfo.getPath()));
    }
}
