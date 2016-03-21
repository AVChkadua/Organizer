package ru.mephi.chkadua.ui;

import ru.mephi.chkadua.FileInfo;
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
    private final RenamerWindowWithRefresh fileRenamer = new RenamerWindowWithRefresh();
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

        JPanel labelsPanel = new JPanel();
        labelsPanel.setLayout(new GridLayout(1, 2));
        JLabel categories = new JLabel("Выберите категорию:");
        JLabel contents = new JLabel("Выберите документ:");
        labelsPanel.add(categories);
        labelsPanel.add(contents);
        frame.add(labelsPanel, BorderLayout.NORTH);

        JPanel listsPanel = new JPanel();
        listsPanel.setLayout(new GridLayout(1, 2));
        categoriesList = addList(new String[0], listsPanel);
        filesList = addList(new String[0], listsPanel);

        try {
            InfoParser.createFileIfNotExists();
            repo.getFiles();
            refreshCategoriesList();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame,"Ошибка при создании файла со списком категорий.", "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
        }

        categoriesList.addListSelectionListener(e -> {
            String selectedCategory = categoriesList.getSelectedValue();
            refreshFilesList(selectedCategory);
        });
        listsPanel.setPreferredSize(new Dimension(480,280));
        frame.add(listsPanel, BorderLayout.CENTER);

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridLayout(1, 5));
        addButton("Добавить файл...", e -> {
            if (!fileAdder.isShowing()) {
                fileAdder.setLocationByPlatform(true);
                fileAdder.setVisible(true);
            }
        }, buttonsPanel);
        addButton("Удалить", e -> deleteFileInfo(), buttonsPanel);
        addButton("Открыть", null, buttonsPanel);
        addButton("Переименовать...", e -> renameFile(), buttonsPanel);
        addButton("О программе", null, buttonsPanel);
        frame.add(buttonsPanel, BorderLayout.SOUTH);

        Border border = BorderFactory.createEmptyBorder(10,10,10,10);
        labelsPanel.setBorder(border);
        listsPanel.setBorder(border);
        buttonsPanel.setBorder(border);

        frame.pack();
        frame.setVisible(true);
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

    //TODO добавить переименование категорий
    public void renameFile() {
        if (filesList.getSelectedValue() == null) {
            JOptionPane.showMessageDialog(frame, "Выберите файл или категорию для переименования",
                    "Ошибка", JOptionPane.WARNING_MESSAGE);
        } else {
            if (!fileRenamer.isShowing()) {
                fileRenamer.setOldNameText(filesList.getSelectedValue());
                fileRenamer.setLocationByPlatform(true);
                fileRenamer.setVisible(true);
            }
        }
    }

    /**
     * Удаляет информацию о файле из JSON-файла и хранилища
     */
    private void deleteFileInfo() {
        try {
            if (filesList.getSelectedValue() == null) {
                JOptionPane.showMessageDialog(frame, "Выберите файл или категорию для удаления",
                        "Ошибка", JOptionPane.WARNING_MESSAGE);
            } else {
                repo.removeFile(categoriesList.getSelectedValue(), filesList.getSelectedValue());
                InfoParser.deleteFileInfo(categoriesList.getSelectedValue(), filesList.getSelectedValue());
            }
        } catch (IOException e1) {
            JOptionPane.showMessageDialog(frame, "Ошибка при удалении файла.", "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
        }
        refreshLists();
    }

    /**
     * Класс окна добавления файла, при закрытии которого обновляются оба списка
     */
    private class FileAdderWithRefresh extends FileAdder {
        public FileAdderWithRefresh() {
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

    private class RenamerWindowWithRefresh extends RenamerWindow {
        public RenamerWindowWithRefresh() {
            super();
            renameButton.addActionListener(e -> {
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
            });
            this.addWindowListener(new WindowAdapter() {
                @Override
                public void windowDeactivated(WindowEvent e) {
                    refreshLists();
                    clearFields();
                }
            });
        }

        public void setOldNameText(String oldName) {
            this.oldName.setText(oldName);
        }
    }
}