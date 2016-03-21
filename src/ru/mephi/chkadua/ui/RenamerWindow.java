package ru.mephi.chkadua.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Класс окна для переименования файла.
 */
public class RenamerWindow extends JFrame{
    protected JTextField oldName = new JTextField();
    protected JTextField newName = new JTextField();
    protected JButton renameButton = new JButton();

    public RenamerWindow() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle("Переименование файла");
        JPanel renamerPanel = new JPanel(new GridLayout(2, 2));
        renamerPanel.add(new JLabel("Старое имя:"));
        oldName.setEditable(false);
        renamerPanel.add(oldName);
        renamerPanel.add(new JLabel("Новое имя:"));
        renamerPanel.add(newName);
        add(renamerPanel, BorderLayout.NORTH);

        JPanel renameButtonPanel = new JPanel();
        renameButton.setText("Переименовать");
        renameButtonPanel.add(renameButton);
        add(renameButtonPanel, BorderLayout.SOUTH);
        renamerPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        renameButtonPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        pack();
    }

    public void clearFields() {
        oldName.setText("");
        newName.setText("");
    }
}
