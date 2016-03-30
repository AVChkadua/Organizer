package ru.mephi.chkadua.ui;

import ru.mephi.chkadua.FileInfo;
import ru.mephi.chkadua.FilesInfoRepository;
import ru.mephi.chkadua.InfoParser;
import ru.mephi.chkadua.OSOperationsManager;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

/**
 *  Класс главного окна приложения
 *  @author Anton_Chkadua
 */

public final class MainWindow implements Runnable {

    private static final int DEFAULT_WIDTH = 500;
    private static final int DEFAULT_HEIGHT = 350;
    private JList<String> categoriesList;
    private JList<String> filesList;
    private JFrame frame;
    private final FileAdderWithRefresh fileAdder = new FileAdderWithRefresh();
    private final FilesInfoRepository repo = FilesInfoRepository.getFilesInfoRepository();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new MainWindow());
    }

    public void run() {
        frame = new JFrame("Органайзер материалов");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.add(new JLabel());
        frame.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        frame.setLocationByPlatform(true);

        JPanel labelsPanel = addLabelsPanel();
        JPanel listsPanel = addListsPanel();

        createDatabaseFile();

        categoriesList.addListSelectionListener(e -> {
            String selectedCategory = categoriesList.getSelectedValue();
            refreshFilesList(selectedCategory);
        });
        listsPanel.setPreferredSize(new Dimension(480,280));
        frame.add(listsPanel, BorderLayout.CENTER);

        JPanel buttonsPanel = addButtonsPanel();

        Border border = BorderFactory.createEmptyBorder(10,10,10,10);
        labelsPanel.setBorder(border);
        listsPanel.setBorder(border);
        buttonsPanel.setBorder(border);

        refreshLists();
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Создаёт JPanel со списками
     * @return Панель со списками
     */
    private JPanel addListsPanel() {
        JPanel listsPanel = new JPanel();
        listsPanel.setLayout(new GridLayout(1, 2));
        categoriesList = addList(new String[0], listsPanel);
        filesList = addList(new String[0], listsPanel);
        return listsPanel;
    }

    /**
     * Создаёт JPanel с лейблами
     * @return Панель с лейблами
     */
    private JPanel addLabelsPanel() {
        JPanel labelsPanel = new JPanel();
        labelsPanel.setLayout(new GridLayout(1, 2));
        JLabel categories = new JLabel("Выберите категорию:");
        JLabel contents = new JLabel("Выберите документ:");
        labelsPanel.add(categories);
        labelsPanel.add(contents);
        frame.add(labelsPanel, BorderLayout.NORTH);
        return labelsPanel;
    }

    /**
     * Создаёт JPanel с кнопками и устанавливает обработчики событий
     * @return Панель с кнопками
     */
    private JPanel addButtonsPanel() {
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridLayout(3, 3));
        addButton("Переименовать файл...", e -> rename(RenamerWindow.RenamerConstants.FILES_LIST), buttonsPanel);
        addButton("Удалить файл", e -> delete(filesList), buttonsPanel);
        addButton("Добавить файл...", e -> {
            if (!fileAdder.isShowing()) {
                fileAdder.setLocationByPlatform(true);
                fileAdder.setVisible(true);
            }
        }, buttonsPanel);
        addButton("Переименовать на диске",e -> renameOnDisc(),buttonsPanel);
        addButton("Удалить с диска",e -> deleteFileFromDisc(),buttonsPanel);
        addButton("Открыть", e -> open(), buttonsPanel);
        addButton("Переименовать категорию...",
                e -> rename(RenamerWindow.RenamerConstants.CATEGORIES_LIST), buttonsPanel);
        addButton("Удалить категорию", e -> delete(categoriesList), buttonsPanel);
        addButton("О программе", e -> showAuthorInfo(), buttonsPanel);
        frame.add(buttonsPanel, BorderLayout.SOUTH);
        return buttonsPanel;
    }

    /**
     * Создаёт файл-базу (при его отсутствии)
     */
    private void createDatabaseFile() {
        try {
            InfoParser.createDatabaseFileIfNotExists();
        } catch (IOException e) {
            showErrorMessage("Ошибка при создании файла со списком файлов.", JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
        }
    }

    /**
     * Добавляет кнопку
     * @param label Надпись
     * @param listener Обработчик
     * @param panel Панель, в которую добавляется кнопка
     */
    private void addButton(String label, ActionListener listener, JPanel panel) {
        JButton button = new JButton(label);
        button.addActionListener(listener);
        panel.add(button);
    }

    /**
     * Добавляет список
     * @param data Содержимое
     * @param panel Панель, в которую добавляется список
     */
    private JList<String> addList(String[] data, JPanel panel) {
        JList<String> list = new JList<>(data);
        list.setLayoutOrientation(JList.VERTICAL);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setVisibleRowCount(-1);
        JScrollPane scrollBar = new JScrollPane(list);
        scrollBar.setViewportView(list);
        panel.add(scrollBar);
        return list;
    }

    /**
     * Обновляет список категорий в окне приложения
     */
    private void refreshCategoriesList() {
        String selected = categoriesList.getSelectedValue();
        categoriesList.setListData(new String[0]);
        try {
            repo.getFiles();
        } catch (IOException e1) {
            String[] options = {"Да","Нет"};
            int reply = JOptionPane.showOptionDialog(frame,"Ошибка при загрузке списка файлов.\nВозможно файл со " +
                    "списком повреждён.\nНажмите \"Да\", чтобы создать его заново\n(к сожалению, вся информация " +
                    "будет потеряна).","Ошибка", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
                    null, options, options[0]);
            if (reply == JOptionPane.YES_OPTION) {
                try {
                    InfoParser.clearDatabaseFile();
                } catch (IOException e) {
                    showErrorMessage("Ошибка при создании файла со списком файлов.",JOptionPane.ERROR_MESSAGE);
                }
            }
            System.exit(-1);
        }
        String[] categoriesArray = new String[repo.getCategoriesNames().size()];
        categoriesArray = repo.getCategoriesNames().toArray(categoriesArray);
        categoriesList.setListData(categoriesArray);
        categoriesList.setSelectedValue(selected, true);
    }
    /**
     * Обновляет список файлов определённой категории в окне приложения
     * @param category Название выбранной категории
     */
    private void refreshFilesList(String category) {
        filesList.setListData(new String[0]);
        ArrayList<FileInfo> files = repo.getFilesByCategory(category);
        if (files != null) {
            String[] filesNamesArray = new String[files.size()];
            int counter = 0;
            for (FileInfo file : files) {
                filesNamesArray[counter] = file.getName();
                counter++;
            }
            filesList.setListData(filesNamesArray);
        }
    }

    /**
     * Обновляет все списки в окне приложения
     */
    private void refreshLists() {
        refreshCategoriesList();
        refreshFilesList(categoriesList.getSelectedValue());
    }

    /**
     * Переименовывает файл или категорию
     * @param constant Константа, определяющая, файл или категория подлежит переименованию
     */
    private void rename(RenamerWindow.RenamerConstants constant) {
        if ((constant == RenamerWindow.RenamerConstants.FILES_LIST) &&
                (filesList.getSelectedValue() == null)) {
            showErrorMessage("Выберите файл для переименования.", JOptionPane.WARNING_MESSAGE);
        } else if ((constant == RenamerWindow.RenamerConstants.CATEGORIES_LIST) &&
                (categoriesList.getSelectedValue() == null)) {
            showErrorMessage("Выберите категорию для переименования.", JOptionPane.WARNING_MESSAGE);
        } else {
            RenamerWindowWithRefresh renamer = new RenamerWindowWithRefresh(categoriesList.getSelectedValue(),
                    constant);
            if (!renamer.isShowing()) {
                if (constant == RenamerWindow.RenamerConstants.FILES_LIST) {
                    renamer.setOldNameText(filesList.getSelectedValue());
                } else {
                    renamer.setOldNameText(categoriesList.getSelectedValue());
                }
                renamer.setLocationByPlatform(true);
                renamer.setVisible(true);
            }
        }
    }

    /**
     * Переименовывает файл на диске
     */
    private void renameOnDisc() {
        RenamerWindowWithRefresh fileRenamer = new RenamerWindowWithRefresh();
        if (filesList.getSelectedValue() == null) {
            showErrorMessage("Выберите файл для переименования.", JOptionPane.WARNING_MESSAGE);
        } else if (!fileRenamer.isShowing()) {
            FileInfo file = FilesInfoRepository.getFilesInfoRepository().getFileByName(
                    categoriesList.getSelectedValue(), filesList.getSelectedValue());
            String oldPath = file.getPath();
            fileRenamer.setSuffixLabelText(oldPath.substring(oldPath.lastIndexOf(".")));
            oldPath = oldPath.substring(oldPath.lastIndexOf("\\") + 1, oldPath.lastIndexOf("."));
            fileRenamer.setOldNameText(oldPath);
            fileRenamer.setLocationByPlatform(true);
            fileRenamer.setVisible(true);
        }
    }

    /**
     * Удаляет информацию о файле или категорию из JSON-файла и хранилища
     */
    private void delete(JList<String> list) {
        if (list == filesList) {
            deleteFile(list);
        } else {
            deleteCategory(list);
        }
        refreshLists();
    }

    /**
     * Удаляет категорию из JSON-файла
     * @param list Список с категориями
     */
    private void deleteCategory(JList<String> list) {
        try {
            if (list.getSelectedValue() == null) {
                showErrorMessage("Выберите категорию для удаления.", JOptionPane.WARNING_MESSAGE);
            } else {
                repo.deleteCategory(list.getSelectedValue());
                refreshLists();
            }
        } catch (IOException e1) {
            showErrorMessage("Ошибка при удалении категории.", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Выводит сообщение с ошибкой
     * @param message Сообщение
     * @param messageType Тип сообщения
     */
    private void showErrorMessage(String message, int messageType) {
        JOptionPane.showMessageDialog(frame, message,
                "Ошибка", messageType);
    }

    /**
     * Удаляет файл их JSON-файла
     * @param list Список с файлами
     */
    private void deleteFile(JList<String> list) {
        try {
            if (list.getSelectedValue() == null) {
                showErrorMessage("Выберите файл или категорию для удаления.", JOptionPane.WARNING_MESSAGE);
            } else {
                repo.deleteFile(categoriesList.getSelectedValue(), filesList.getSelectedValue());
                refreshLists();
            }
        } catch (IOException e1) {
            showErrorMessage("Ошибка при удалении файла.", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Удаляет файл с дивка
     */
    private void deleteFileFromDisc() {
        try {
            if (filesList.getSelectedValue() == null) {
                showErrorMessage("Выберите файл для удаления.", JOptionPane.WARNING_MESSAGE);
            } else {
                FileInfo file = repo.getFileByName(categoriesList.getSelectedValue(),filesList.getSelectedValue());
                OSOperationsManager.deleteFile(file);
                refreshLists();
            }
        } catch (IOException e) {
            showErrorMessage("Ошибка при удалении файла с диска.", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Открывает файл в стандартной программе для его просмотра
     */
    private void open() {
        if (filesList.getSelectedValue() == null) {
            showErrorMessage("Выберите файл для открытия.", JOptionPane.WARNING_MESSAGE);
        } else {
            try {
                FileInfo file = FilesInfoRepository.getFilesInfoRepository().getFileByName(
                        categoriesList.getSelectedValue(), filesList.getSelectedValue());
                OSOperationsManager.openFile(file);
            } catch (IOException e) {
                showErrorMessage("Ошибка при открытии файла.", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Показывает информацию о программе
     */
    private void showAuthorInfo() {
        JFrame infoFrame = new JFrame("О программе");
        infoFrame.setLocationByPlatform(true);
        JLabel info = new JLabel("Органайзер материалов. Автор: А. Чкадуа.\n");
        JPanel buttonPanel = new JPanel(new GridLayout(1,2));
        JButton link = new JButton("Github");
        JButton closeButton = new JButton("Закрыть");
        infoFrame.add(info,BorderLayout.CENTER);
        link.addActionListener(e -> {
            try {
                OSOperationsManager.openGithub();
            } catch (IOException e1) {
                showErrorMessage("Ошибка при открытии браузера.", JOptionPane.ERROR_MESSAGE);
            } catch (URISyntaxException e1) {
                showErrorMessage("Некорректный URI.", JOptionPane.ERROR_MESSAGE);
            }
        });
        closeButton.addActionListener(e -> infoFrame.setVisible(false));
        buttonPanel.add(link);
        buttonPanel.add(closeButton);
        infoFrame.add(buttonPanel,BorderLayout.SOUTH);
        info.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        infoFrame.pack();
        infoFrame.setVisible(true);
    }

    /**
     * Класс окна добавления файла, при закрытии которого обновляются оба списка
     */
    private class FileAdderWithRefresh extends FileAdder {
        FileAdderWithRefresh() {
            super();
            this.addWindowListener(new WindowAdapter() {
                @Override
                public void windowDeactivated(WindowEvent e) {
                    refreshLists();
                    clearFields();
                }
            });
        }
    }

    /**
     * Класс окна переименования файлов и категорий, при закрытии которого обновляются оба списка
     */
    private class RenamerWindowWithRefresh extends RenamerWindow {
        RenamerWindowWithRefresh(String category, RenamerConstants constant) {
            super(category,constant);
            this.addWindowListener(new WindowAdapter() {
                @Override
                public void windowDeactivated(WindowEvent e) {
                    refreshLists();
                }
            });
        }

        RenamerWindowWithRefresh() {
            super();
            renameButton.addActionListener(e -> renameOnDisc(categoriesList, filesList));
        }
    }
}