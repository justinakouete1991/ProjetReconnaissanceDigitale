package org.example.ui;

import javax.swing.*;
import java.awt.*;

public class LogsArea extends JTextArea {
    public LogsArea(int rows, int columns){
        super(rows, columns);
        setEditable(false);
        setFont(new Font("Segoe UI", Font.PLAIN, 12));
        setBackground(Color.WHITE);
        //setBackground(new Color(30, 30, 30));
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        setLineWrap(true);
        setWrapStyleWord(true);
    }

    public void addLog(String message){
        append(message + "\n");
        setCaretPosition(getDocument().getLength());
    }
}
