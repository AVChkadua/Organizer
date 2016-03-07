package ru.mephi.chkadua.ui;

import ru.mephi.chkadua.FileInfoContainer;
import ru.mephi.chkadua.InfoParser;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

/**
 * Класс окна с добавлением материала.
 * @author Anton_Chkadua
 */
public class FileAdder extends JFrame {
    /**
     * Конструктор окна добавления файла
    */

    public FileAdder() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        JFileChooser fc = new JFileChooser();

        setTitle("Добавление нового материала");
        JPanel fileAdderPanel = new JPanel(new GridLayout(3, 2));
        JTextField filepath = new JTextField("Выберите файл");
        ActionListener openFileChooser = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int returnVal = fc.showOpenDialog(FileAdder.this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    String pathname = fc.getSelectedFile().getAbsolutePath();
                    File f = new File(pathname);
                    if (!f.exists()) {
                        JOptionPane.showMessageDialog(FileAdder.this, "Файл не найден", "Ошибка", JOptionPane.ERROR_MESSAGE);
                    } else {
                        filepath.setText(pathname);
                    }
                }
            }
        };

        filepath.setEditable(false);
        JLabel inputName = new JLabel("Введите название материала");
        JLabel inputCategory = new JLabel("Введите категорию");
        JTextField name = new JTextField();
        JTextField category = new JTextField();
        fileAdderPanel.add(filepath);
        addButton("Выбрать...", openFileChooser, fileAdderPanel);
        fileAdderPanel.add(inputName);
        fileAdderPanel.add(name);
        fileAdderPanel.add(inputCategory);
        fileAdderPanel.add(category);

        JPanel addButtonPanel = new JPanel();
        addButton("Добавить",
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (!filepath.getText().equals("Выберите файл")) {
                            FileInfoContainer info = new FileInfoContainer();
                            info.setName(name.getText());
                            info.setCategory(category.getText());
                            InfoParser parser = new InfoParser();
                            info.setPath(filepath.getText());
                            try {
                                parser.addFile(info);
                            } catch (IOException e1) {
                                e1.printStackTrace();
                                // TODO Дописать нормальную обработку исключения
                            }
                            FileAdder.this.setVisible(false);
                        }
                    }
                }, addButtonPanel);

        add(fileAdderPanel);
        add(addButtonPanel,BorderLayout.SOUTH);
        fileAdderPanel.setBorder(new EmptyBorder(10,10,10,10));
        addButtonPanel.setBorder(new EmptyBorder(10,10,10,10));
        pack();
    }

    /**
     * Метод, добавляющий кнопку
     * @param label Надпись
     * @param listener Обработчик
     * @param panel Панель, в которую добавляется кнопка
     */

    private void addButton(String label, ActionListener listener, JPanel panel) {
        JButton button = new JButton(label);
        button.addActionListener(listener);
        panel.add(button);
    }
}
