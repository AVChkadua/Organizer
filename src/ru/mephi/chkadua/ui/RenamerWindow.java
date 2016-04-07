package ru.mephi.chkadua.ui;

import ru.mephi.chkadua.FileInfo;
import ru.mephi.chkadua.FilesInfoRepository;
import ru.mephi.chkadua.OSOperationsManager;

import javax.swing.*;
import java.awt.*;
import java.io.FileNotFoundException;
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
    private final FilesInfoRepository repo = FilesInfoRepository.getInstance();

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
            showMessage("Выберите файл или категорию для переименования.", JOptionPane.WARNING_MESSAGE);
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
            if (newName.getText().trim().equals(oldName.getText())) {
                showMessage("Новое имя совпадает со старым", JOptionPane.WARNING_MESSAGE);
            } else if (!newName.getText().trim().isEmpty()) {
                try {
                    repo.renameCategory(category, newName.getText());
                    dispose();
                } catch (IllegalArgumentException e) {
                    showMessage("Категория с таким именем уже существует", JOptionPane.WARNING_MESSAGE);
                }
            } else {
                showMessage("Введите корректное новое имя", JOptionPane.WARNING_MESSAGE);
            }
        } catch (IOException ex) {
            showMessage("Ошибка при переименовании категории.", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Переименовывает файл
     * @param category Список категорий
     */
    private void renameFile(String category) {
        try {
            if (newName.getText().trim().equals(oldName.getText())) {
                showMessage("Новое имя совпадает со старым.", JOptionPane.WARNING_MESSAGE);
            } else if (!newName.getText().trim().isEmpty()) {
                try {
                    repo.renameFile(category, oldName.getText(), newName.getText());
                    dispose();
                } catch (IllegalArgumentException e) {
                    showMessage("Файл с таким именем уже существует.", JOptionPane.WARNING_MESSAGE);
                }
            } else {
                showMessage("Введите корректное новое имя.",JOptionPane.WARNING_MESSAGE);
            }
        } catch (IOException ex) {
            showMessage("Ошибка при переименовании файла.", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Переименовывает файл на диске
     * @param categoriesList Список категорий
     * @param filesList Список файлов
     * @throws FileNotFoundException
     */
    void renameOnDisc(JList<String> categoriesList, JList<String> filesList) throws FileNotFoundException {
        try {
            if (!oldName.getText().trim().isEmpty()) {
                String newName = oldName.getText().trim() + suffixLabel.getText();
                FileInfo file = repo.getFileByName(categoriesList.getSelectedValue(), filesList.getSelectedValue());
                OSOperationsManager.renameFile(file, newName);
                this.setVisible(false);
            }
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException();
        } catch (IOException e) {
            showMessage("Ошибка при переименовании файла.", JOptionPane.ERROR_MESSAGE);
        } catch (InvalidPathException e) {
            showMessage("Новое имя файла содержит недопустимые символы.", JOptionPane.WARNING_MESSAGE);

        }
    }

    /**
     * Выводит сообщение об ошибке
     * @param message Выводимое сообщение
     * @param warningMessage Константа JOptionPane
     */
    private void showMessage(String message, int warningMessage) {
        JOptionPane.showMessageDialog(this, message, "Ошибка",
                warningMessage);
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
