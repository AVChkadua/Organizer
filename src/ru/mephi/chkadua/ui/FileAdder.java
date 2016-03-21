package ru.mephi.chkadua.ui;

import ru.mephi.chkadua.FileInfo;
import ru.mephi.chkadua.FilesInfoRepository;
import ru.mephi.chkadua.InfoParser;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

/**
 * Класс окна с добавлением материала.
 * @author Anton_Chkadua
 */
public class FileAdder extends JFrame {

    private JTextField name = new JTextField();
    private JTextField category = new JTextField();
    private JTextField filepath = new JTextField();
    private JFileChooser fc = new JFileChooser();

    public FileAdder() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setTitle("Добавление нового материала");
        JPanel fileAdderPanel = new JPanel(new GridLayout(3, 2));
        filepath.setText("Выберите файл");

        filepath.setEditable(false);
        JLabel inputName = new JLabel("Введите название материала");
        JLabel inputCategory = new JLabel("Введите категорию");
        fileAdderPanel.add(filepath);
        addButton("Выбрать...", e -> openFileChooser(), fileAdderPanel);
        fileAdderPanel.add(inputName);
        fileAdderPanel.add(name);
        fileAdderPanel.add(inputCategory);
        fileAdderPanel.add(category);

        JPanel addButtonPanel = new JPanel();
        addButton("Добавить", e -> addFile(), addButtonPanel);

        add(fileAdderPanel);
        add(addButtonPanel,BorderLayout.SOUTH);
        fileAdderPanel.setBorder(new EmptyBorder(10,10,10,10));
        addButtonPanel.setBorder(new EmptyBorder(10,10,10,10));
        pack();
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
     * Сбрасывает значения всех полей в окне добавления файлов
     */
    protected void clearFields() {
        filepath.setText("Выберите файл");
        name.setText("");
        category.setText("");
    }

    /**
     * Добавляет информацию о файле в JSON-файл
     */
    private void addFile() {
        if (!filepath.getText().equals("Выберите файл")) {
            if (name.getText().trim().isEmpty() || category.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this,"Введите корректные названия материала и категории",
                        "Ошибка",JOptionPane.WARNING_MESSAGE);
            } else {
                FileInfo info = new FileInfo();
                info.setName(name.getText());
                info.setCategory(category.getText());
                info.setPath(filepath.getText());
                try {
                    FilesInfoRepository.getFilesInfoRepository().addFile(info);
                    InfoParser.addFileInfo(info);
                } catch (IOException e1) {
                    JOptionPane.showMessageDialog(this, "Произошла ошибка.\nПопробуйте снова.",
                            "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
                FileAdder.this.setVisible(false);
            }
        } else {
            JOptionPane.showMessageDialog(this,"Файл не выбран","Ошибка",JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Открывает окно выбора файла
     */
    private void openFileChooser() {
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
}
