package ru.mephi.chkadua.ui;

import ru.mephi.chkadua.FileInfo;
import ru.mephi.chkadua.FilesInfoRepository;
import ru.mephi.chkadua.InfoParser;
import ru.mephi.chkadua.OSOperationsManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.nio.file.InvalidPathException;

/**
 * Класс окна для переименования файла.
 */
class RenamerWindow extends JFrame{
    private JTextField oldName = new JTextField();
    private JTextField newName = new JTextField();
    private JLabel suffixLabel = new JLabel();
    JButton renameButton = new JButton();
    private static final int DEFAULT_WIDTH = 300;
    private static final int DEFAULT_HEIGHT = 100;
    enum RenamerConstants {FILES_LIST, CATEGORIES_LIST}

    RenamerWindow(String category, RenamerConstants constant) {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle("Переименование файла");

        JPanel renameButtonPanel = new JPanel();
        renameButton.setText("Переименовать");
        JPanel labelsPanel;
        if (category != null) {
            labelsPanel = addSimpleRenamingPanel();
            renameButton.addActionListener(e -> rename(category, constant));
        } else {
            labelsPanel = addRenamingOnDiscPanel();
        }
        renameButtonPanel.add(renameButton);
        add(renameButtonPanel, BorderLayout.SOUTH);
        labelsPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        renameButtonPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        setSize(new Dimension(DEFAULT_WIDTH,DEFAULT_HEIGHT));
        pack();
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                clearFields();
            }
        });
    }

    RenamerWindow() {
        this(null,null);
    }

    /**
     * Добавляет панель с полями для переименования файла в JSON-файле
     * @return JPanel с полями
     */
    private JPanel addSimpleRenamingPanel() {
        JPanel renamerPanel = new JPanel(new GridLayout(2, 2));
        renamerPanel.add(new JLabel("Старое имя:"));
        oldName.setEditable(false);
        renamerPanel.add(oldName);
        renamerPanel.add(new JLabel("Новое имя:"));
        renamerPanel.add(newName);
        add(renamerPanel, BorderLayout.NORTH);
        return renamerPanel;
    }

    /**
     * Добавляет панель с полями для переименования файла на диске
     * @return JPanel с полями
     */
    private JPanel addRenamingOnDiscPanel() {
        JPanel renamerPanel = new JPanel(new GridLayout(1, 3));
        renamerPanel.add(new JLabel("Имя файла на диске: "));
        renamerPanel.add(oldName);
        renamerPanel.add(suffixLabel);
        add(renamerPanel, BorderLayout.NORTH);
        return renamerPanel;
    }

    /**
     * Переименовывает файл или категорию в JSON-файле
     * @param category Категория, которая (или файл из которой) подлежит переименованию
     */
    private void rename(String category, RenamerConstants constant) {
        if (category == null) {
            showErrorMessage("Выберите файл или категорию для переименования.", JOptionPane.WARNING_MESSAGE);
        }
        if (constant == RenamerConstants.FILES_LIST) {
            renameFile(category);
        } else if (constant == RenamerConstants.CATEGORIES_LIST){
            renameCategory(category);
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Переименовывает категорию
     * @param category Список категорий
     */
    private void renameCategory(String category) {
        try {
            if (!newName.getText().trim().isEmpty()) {
                InfoParser.renameCategory(category, newName.getText());
                FilesInfoRepository.getFilesInfoRepository().
                        renameCategory(category, newName.getText());
                setVisible(false);
            }
        } catch (IOException ex) {
            showErrorMessage("Ошибка при переименовании категории.", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Переименовывает файл
     * @param category Список категорий
     */
    private void renameFile(String category) {
        try {
            if (!newName.getText().trim().isEmpty()) {
                FilesInfoRepository.getFilesInfoRepository().
                        renameFile(category, oldName.getText(), newName.getText());
                setVisible(false);
            }
        } catch (IOException ex) {
            showErrorMessage("Ошибка при переименовании файла.", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Переименовывает файл на диске
     * @param categoriesList Список категорий
     * @param filesList Список файлов
     */
    void renameOnDisc(JList<String> categoriesList, JList<String> filesList) {
        try {
            if (!oldName.getText().trim().isEmpty()) {
                String newName = oldName.getText().trim() + suffixLabel.getText();
                FilesInfoRepository repo = FilesInfoRepository.getFilesInfoRepository();
                FileInfo file = repo.getFileByName(categoriesList.getSelectedValue(),filesList.getSelectedValue());
                OSOperationsManager.renameFile(file,newName);
                this.setVisible(false);
            }
        } catch (IOException e) {
            showErrorMessage("Ошибка при переименовании файла.", JOptionPane.ERROR_MESSAGE);
        } catch (InvalidPathException e) {
            showErrorMessage("Новое имя файла содержит недопустимые символы.", JOptionPane.WARNING_MESSAGE);

        }
    }

    /**
     * Выводит сообщение об ошибке
     * @param message Выводимое сообщение
     * @param warningMessage Константа JOptionPane
     */
    private void showErrorMessage(String message, int warningMessage) {
        JOptionPane.showMessageDialog(this, message, "Ошибка",
                warningMessage);
    }

    /**
     * Сбрасывает значения текстовых полей
     */
    private void clearFields() {
        oldName.setText("");
        newName.setText("");
    }

    /**
     * Устанавливает текст в поле со старым названием
     * @param text Текстовая строка
     */
    void setOldNameText(String text) {
        this.oldName.setText(text);
    }

    /**
     * Устанавливает суффикс расширения файла
     * @param suffix Расширение файла
     */
    void setSuffixLabelText(String suffix) {
        suffixLabel.setText(suffix);
    }
}
