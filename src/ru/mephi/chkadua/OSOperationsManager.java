package ru.mephi.chkadua;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;

/**
 * Класс, отвечающий за открытие файлов в стандартной программе для просмотра файла указанного формата.
 */
public class OSOperationsManager {

    /**
     * Открывает файл в стандартной программе просмотра, ассоциированной с данным форматом
     * @param fileInfo Объект с информацией о файле
     * @throws IOException
     */
    public static void openFile(FileInfo fileInfo) throws IOException {
        Desktop.getDesktop().open(new File(fileInfo.getPath()));
    }

    /**
     * Переименовывает файл на диске и обновляет информацию в JSON-файле и репозитории
     * @param file Объект с информацией о файле
     * @param newName Новое название файла (не включает в себя путь к файлу)
     * @throws IOException
     */
    public static void renameFile(FileInfo file, String newName) throws IOException {
        int indexOfLastSeparator = file.getPath().lastIndexOf("\\");
        String newPath = file.getPath().substring(0,indexOfLastSeparator + 1) + newName;
        Files.move(new File(file.getPath()).toPath(), new File(newPath).toPath());
        FilesInfoRepository.getFilesInfoRepository().changeFilePath(file.getCategory(),file.getName(),newPath);
    }

    /**
     * Удаляет файл с диска
     * @param file Объект с информацией о файле
     * @throws IOException
     */
    public static void deleteFile(FileInfo file) throws IOException {
        Files.delete(new File(file.getPath()).toPath());
        FilesInfoRepository.getFilesInfoRepository().deleteFile(file.getCategory(),file.getName());
    }

    /**
     * Открывает Github с проектом
     * @throws URISyntaxException
     * @throws IOException
     */
    public static void openGithub() throws URISyntaxException, IOException {
        Desktop.getDesktop().browse(new URI("https://github.com/AVChkadua/Organizer"));
    }
}
