package ru.avalon.javapp.devj120.puzzledemo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalTime;
import java.util.Random;

public class Puzzle extends JFrame {

    private JPanel panelKnuckle = new JPanel(new GridLayout(4, 4));

    private JButton button;
    private JButton reset;
    private JLabel lblTime;
    private LocalTime time;

    private Timer timer;
    private static Random generator = new Random();

    private int[][] knuckle = new int[4][4];
    private int[] invariants = new int[16];

    public Puzzle() throws HeadlessException {
        setTitle("Пятнашки");
        setSize (400, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);
        addWindowListener(new DemoWindowAdapter());
        add(panelKnuckle, BorderLayout.CENTER);

        // Локализация кнопок
        UIManager.put("OptionPane.yesButtonText"   , "Да"    );
        UIManager.put("OptionPane.noButtonText"    , "Нет"   );
        UIManager.put("OptionPane.cancelButtonText", "Отмена");
        UIManager.put("OptionPane.okButtonText", "Я готов!");

        reset = new JButton("Сбросить костяшки заново");
        addDialogPanes();
        add(reset, BorderLayout.SOUTH);
        time = LocalTime.of(00, 00, 00);
        String timeStr = String.format("%02d:%02d:%02d", time.getHour(), time.getMinute(), time.getSecond());
        lblTime = new JLabel(timeStr, SwingConstants.CENTER);
        add(lblTime, BorderLayout.NORTH);

        timer = new Timer(1000, (e) -> {
            time = time.plusSeconds(1);
            lblTime.setText(time.toString());
        });
        timer.start();
        
        generate();
    }

    public static void main(String[] args) {
        Puzzle puzzle = new Puzzle();
    }


    public void generate() {
        timer.stop();

        do {
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    knuckle[i][j] = 0;
                    invariants[i * 4 + j] = 0;
                }
            }

            for (int i = 1; i < 16; i++) {
                int k, l;
                do {
                    k = generator.nextInt(4);
                    l = generator.nextInt(4);
                }
                while (knuckle[k][l] != 0);
                knuckle[k][l] = i;
                invariants[k * 4 + l] = i;
            }
        }
        while (!canBeSolved(invariants));
        repaintField();
        time = LocalTime.of(00, 00, 00);
        timer.restart();
    }

    private boolean canBeSolved(int[] invariants) {
        int sum = 0;
        for (int i = 0; i < 16; i++) {
            if (invariants[i] == 0) {
                sum += i / 4;
                continue;
            }

            for (int j = i + 1; j < 16; j++) {
                if (invariants[j] < invariants[i])
                    sum ++;
            }
        }
        return sum % 2 == 0;
    }

    public void repaintField() {  //метод расстановки кнопок со значениями на сетке
        panelKnuckle.removeAll();

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                button = new JButton(Integer.toString(knuckle[i][j]));
                button.setFocusable(false);
                panelKnuckle.add(button);
                if (knuckle[i][j] == 0) {
                    button.setVisible(false); // сокрытие нулевого элемента массива
                } else
                    button.addActionListener(new ClickListener());
            }
        }

        panelKnuckle.validate();
        panelKnuckle.repaint();
    }

    public boolean checkWin() { //метод проверки победы
        boolean status = true;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (i == 3 && j > 2) //проверка на то, что последняя ячейка в сетке пустая
                    break;
                if (knuckle[i][j] != i * 4 + j + 1) { //проверка на соотвествие элементам массива координатам в сетке
                    status = false;
                }
            }
        }
        return status;
    }

    private class ClickListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JButton button = (JButton) e.getSource();
            button.setVisible(false);
            String name = button.getText();
            change(Integer.parseInt(name));
        }
    }

    private class DemoWindowAdapter extends WindowAdapter {

        @Override
        public void windowIconified(WindowEvent e) {
            System.out.println("Window Iconified");
            timer.stop();
        }

        @Override
        public void windowDeiconified(WindowEvent e) {
            System.out.println("Window Deiconified");
            timer.start();
        }
    }



    private void addDialogPanes() {
        reset.addActionListener((e) -> {
            timer.stop();
            int result = JOptionPane.showConfirmDialog(
                    null,
                    "Это все, на что ты способен?",
                    "Новый сброс костяшек",
                    JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                result = JOptionPane.showConfirmDialog(null,
                        "Ты точно уверен?", "Не тот велик, кто никогда не падал...",
                        JOptionPane.YES_NO_OPTION);
                if(result == JOptionPane.YES_OPTION) {
                    generate();
                }
                else if (result == JOptionPane.NO_OPTION) {
                    JOptionPane.showMessageDialog(null,
                            "Красавчик! Не сдавайся!",
                            "Just do it!", JOptionPane.INFORMATION_MESSAGE);
                    timer.start();
                }
            }
            else if (result == JOptionPane.NO_OPTION) {
                JOptionPane.showMessageDialog(null,
                        "Красавчик! Не сдавайся!",
                        "Just do it!", JOptionPane.INFORMATION_MESSAGE);
                timer.start();
            }
        });
    }


    public void change(int num) { // передаем в качестве входящих параметров метода change переменную num типа int
        int i = 0, j = 0;
        for (int k = 0; k < 4; k++) {
            for (int l = 0; l < 4; l++) {
                if (knuckle[k][l] == num) { // если массив numbers[k][l] равен переменной num то,
                    i = k; // переменную i приравниваем переменной k
                    j = l; // переменную j приравниваем переменной l
                }
            }
        }

        /*реализация логики сдвигов кнопок на сетке 4 Х 4*/
        //сдвиг вверх по строкам
        if (i > 0) { // условие отвечающее за то можно ли сдвинуть кнопку по строке
            if (knuckle[i - 1][j] == 0) { //сравниваем значение координат элемента массива с кнопкой которая в текущем массиве равна нулю
                knuckle[i - 1][j] = num; //присваиваем переменной num значение координат элемента массива
                knuckle[i][j] = 0; //присваиваем нулевой элемент массива в ячейку которая перед этим смещалась в ноль
            }
        }
        //сдвиг вниз по строкам
        if (i < 3) {
            if (knuckle[i + 1][j] == 0) {
                knuckle[i + 1][j] = num;
                knuckle[i][j] = 0;
            }
        }
        //сдвиг влево по столбцам
        if (j > 0) {
            if (knuckle[i][j - 1] == 0) {
                knuckle[i][j - 1] = num;
                knuckle[i][j] = 0;
            }
        }
        //сдвиг вправо по столбцам
        if (j < 3) {
            if (knuckle[i][j + 1] == 0) {
                knuckle[i][j + 1] = num;
                knuckle[i][j] = 0;
            }
        }
        repaintField();
        if (checkWin()) {
            timer.stop();
            JOptionPane.showMessageDialog(null, "ВЫ ВЫИГРАЛИ!", "Поздравляем", 1);
            generate();
            setVisible(false);
            setVisible(true);
        }
    }
}
