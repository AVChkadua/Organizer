package ru.mephi.chkadua;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;

/**
 * Класс, отвечающий за открытие файлов в стандартной программе для просмотра файла указанного формата.
 */
public class OSOperationsManager {

    private static final FilesInfoRepository repo = FilesInfoRepository.getFilesInfoRepository();

    /**
     * Открывает файл в стандартной программе просмотра, ассоциированной с данным форматом
     * @param fileInfo Объект с информацией о файле
     * @throws IOException
     */
    public static void openFile(FileInfo fileInfo) throws IOException {
        File file = new File(fileInfo.getPath());
        if (file.exists() && file.isFile())
            Desktop.getDesktop().open(file);
        else
            throw new FileNotFoundException();
    }

    /**
     * Переименовывает файл на диске и обновляет информацию в JSON-файле и репозитории
     * @param fileInfo Объект с информацией о файле
     * @param newName Новое название файла (не включает в себя путь к файлу)
     * @throws IOException
     */
    public static void renameFile(FileInfo fileInfo, String newName)
            throws IOException {
        File file = new File(fileInfo.getPath());
        if (file.exists() && file.isFile()) {
            int indexOfLastSeparator = fileInfo.getPath().lastIndexOf("\\");
            String newPath = fileInfo.getPath().substring(0, indexOfLastSeparator + 1) + newName;
            Files.move(new File(fileInfo.getPath()).toPath(), new File(newPath).toPath());
            repo.changeFilePath(fileInfo.getCategory(), fileInfo.getName(), newPath);
        } else {
            throw new FileNotFoundException();
        }
    }

    /**
     * Удаляет файл с диска
     * @param fileInfo Объект с информацией о файле
     * @throws IOException
     */
    public static void deleteFile(FileInfo fileInfo) throws IOException {
        File file = new File(fileInfo.getPath());
        if (file.exists() && file.isFile()) {
            Files.delete(new File(fileInfo.getPath()).toPath());
            repo.deleteFile(fileInfo.getCategory(), fileInfo.getName());
        } else {
            throw new FileNotFoundException();
        }
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
