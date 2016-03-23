package ru.mephi.chkadua.ui;

import ru.mephi.chkadua.FileInfo;
import ru.mephi.chkadua.FileOpener;
import ru.mephi.chkadua.FilesInfoRepository;
import ru.mephi.chkadua.InfoParser;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
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
     * Создаёт JPanel с кнопками
     * @return Панель с кнопками
     */
    private JPanel addButtonsPanel() {
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridLayout(2, 3));
        addButton("Добавить файл...", e -> {
            if (!fileAdder.isShowing()) {
                fileAdder.setLocationByPlatform(true);
                fileAdder.setVisible(true);
            }
        }, buttonsPanel);
        addButton("Переименовать файл...", e -> rename(filesList), buttonsPanel);
        addButton("Переименовать категорию...", e -> rename(categoriesList), buttonsPanel);
        addButton("Открыть", e -> open(), buttonsPanel);
        addButton("Удалить файл", e -> delete(filesList), buttonsPanel);
        addButton("Удалить категорию", e -> delete(categoriesList), buttonsPanel);
        // addButton("О программе", null, buttonsPanel);
        frame.add(buttonsPanel, BorderLayout.SOUTH);
        return buttonsPanel;
    }

    /**
     * Создаёт файл-базу (при его отсутствии)
     */
    private void createDatabaseFile() {
        try {
            InfoParser.createFileIfNotExists();
            repo.getFiles();
            refreshCategoriesList();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame,"Ошибка при создании файла со списком категорий.", "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
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
        try {
            String selected = categoriesList.getSelectedValue();
            categoriesList.setListData(new String[0]);
            repo.getFiles();
            String[] categoriesArray = new String[repo.getCategoriesNames().size()];
            categoriesArray = repo.getCategoriesNames().toArray(categoriesArray);
            categoriesList.setListData(categoriesArray);
            categoriesList.setSelectedValue(selected, true);
        } catch (IOException e1) {
            JOptionPane.showConfirmDialog(frame,"Ошибка при загрузке списка файлов","Ошибка",
                    JOptionPane.YES_NO_OPTION);
            System.exit(-1);
        }
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
     * Переименовывает файл или категорию (в зависимости от того, какой список передаётся на вход)
     * @param list Список, элемент которого нужно переименовать
     */
    private void rename(JList<String> list) {
        RenamerWindowWithRefresh fileRenamer = new RenamerWindowWithRefresh(list);
        if (!fileRenamer.isShowing()) {
            fileRenamer.setOldNameText(list.getSelectedValue());
            fileRenamer.setLocationByPlatform(true);
            fileRenamer.setVisible(true);
        }
    }

    /**
     * Удаляет информацию о файле из JSON-файла и хранилища
     */
    private void delete(JList<String> list) {
        if (list == filesList) {
            try {
                if (list.getSelectedValue() == null) {
                    JOptionPane.showMessageDialog(frame, "Выберите файл или категорию для удаления",
                            "Ошибка", JOptionPane.WARNING_MESSAGE);
                } else {
                    repo.deleteFile(categoriesList.getSelectedValue(), filesList.getSelectedValue());
                    InfoParser.deleteFileInfo(categoriesList.getSelectedValue(), filesList.getSelectedValue());
                }
            } catch (IOException e1) {
                JOptionPane.showMessageDialog(frame, "Ошибка при удалении файла.", "Ошибка",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else {
            try {
                if (list.getSelectedValue() == null) {
                    JOptionPane.showMessageDialog(frame, "Выберите категорию для удаления",
                            "Ошибка", JOptionPane.WARNING_MESSAGE);
                } else {
                    repo.deleteCategory(list.getSelectedValue());
                    InfoParser.deleteCategory(list.getSelectedValue());
                }
            } catch (IOException e1) {
                JOptionPane.showMessageDialog(frame, "Ошибка при удалении .", "Ошибка",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
        refreshLists();
    }

    /**
     * Открывает файл в стандартной программе для его просмотра
     */
    private void open() {
        try {
            FileInfo file = FilesInfoRepository.getFilesInfoRepository().getFileByName(
                categoriesList.getSelectedValue(), filesList.getSelectedValue());
            FileOpener.openFile(file);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame,"Ошибка при открытии файла.","Ошибка",
                    JOptionPane.ERROR_MESSAGE);
        }
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
        RenamerWindowWithRefresh(JList list) {
            super();
            renameButton.addActionListener(e -> rename(list));
            this.addWindowListener(new WindowAdapter() {
                @Override
                public void windowDeactivated(WindowEvent e) {
                    refreshLists();
                    clearFields();
                }
            });
        }

        /**
         * Переименовывает файл или категорию
         * @param list Список, элемент (категорию или файл) которого нужно переименовать
         */
        void rename(JList list) {
            if (list.getSelectedValue() == null) {
                JOptionPane.showMessageDialog(frame,"Выберите файл или категорию для переименования",
                        "Ошибка",JOptionPane.WARNING_MESSAGE);
            }
            if (list == filesList) {
                try {
                    if (!newName.getText().trim().isEmpty()) {
                        InfoParser.renameFile(categoriesList.getSelectedValue(), oldName.getText(), newName.getText());
                        repo.renameFile(categoriesList.getSelectedValue(), oldName.getText(), newName.getText());
                        setVisible(false);
                    }
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(frame,"Ошибка при переименовании файла","Ошибка",
                            JOptionPane.ERROR_MESSAGE);
                }
            } else {
                try {
                    if (!newName.getText().trim().isEmpty()) {
                        InfoParser.renameCategory(categoriesList.getSelectedValue(), newName.getText());
                        repo.renameCategory(categoriesList.getSelectedValue(), newName.getText());
                        setVisible(false);
                    }
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(frame,"Ошибка при переименовании категории","Ошибка",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        /**
         * Устанавливает текст в поле со старым названием
         * @param text Текстовая строка
         */
        void setOldNameText(String text) {
            this.oldName.setText(text);
        }
    }
}