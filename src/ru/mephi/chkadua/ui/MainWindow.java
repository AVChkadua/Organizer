package ru.mephi.chkadua.ui;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 *  Класс главного окна приложения
 *  @author Anton_Chkadua
 */

public final class MainWindow implements Runnable {

    private static final int DEFAULT_WIDTH = 500;
    private static final int DEFAULT_HEIGHT = 350;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new MainWindow());
    }

    public void run() {

        JFrame frame = new JFrame ("Органайзер материалов");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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

        // TODO добавить подгрузку списка категорий из файла
        String[] data = {};
        addList(data, listsPanel);
        addList(data, listsPanel);
        listsPanel.setPreferredSize(new Dimension(480,280));
        frame.add(listsPanel, BorderLayout.CENTER);


        ActionListener addFile = e -> {
            FileAdder fileAdder = new FileAdder();
            fileAdder.setLocationByPlatform(true);
            fileAdder.setVisible(true);
        };

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridLayout(1, 5));
        addButton("Добавить файл...", addFile, buttonsPanel);
        addButton("Удалить", null, buttonsPanel);
        addButton("Открыть", null, buttonsPanel);
        addButton("Переименовать...", null, buttonsPanel);
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

    /**
     * Метод, добавляющий список
     * @param data Содержимое
     * @param panel Панель, в которую добавляется список
     */

    private void addList(String[] data, JPanel panel) {
        JList<String> list = new JList<>(data);
        list.setLayoutOrientation(JList.VERTICAL);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setVisibleRowCount(-1);
        JScrollPane scrollBar = new JScrollPane(list);
        scrollBar.setViewportView(list);
        panel.add(scrollBar);
    }
}