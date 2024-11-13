import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.rmi.Naming;
import java.rmi.RemoteException;

public class GameClient extends JFrame {

    private GameInterface server;
    private String currentPlayer;
    private boolean[][] horizontalLines;
    private boolean[][] verticalLines;
    private Point startPoint = null;

    public GameClient() {
        try {
            server = (GameInterface) Naming.lookup("rmi://localhost/GameServer");
            currentPlayer = server.getCurrentPlayer();
            horizontalLines = server.getHorizontalLines();
            verticalLines = server.getVerticalLines();
        } catch (Exception e) {
            e.printStackTrace();
        }

        setTitle("Dots and Boxes");
        setSize(300, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                int x = e.getX() / 100;
                int y = e.getY() / 100;

                if (startPoint == null) {
                    startPoint = new Point(x, y);
                } else {
                    try {
                        String result = server.makeMove(startPoint.x, startPoint.y, x, y);
                        JOptionPane.showMessageDialog(GameClient.this, result);

                        if (result.equals("Линия нарисована!")) {
                            horizontalLines = server.getHorizontalLines();
                            verticalLines = server.getVerticalLines();
                            currentPlayer = server.getCurrentPlayer();
                            repaint();
                        }
                    } catch (RemoteException ex) {
                        ex.printStackTrace();
                    }
                    startPoint = null;
                }
            }
        });
    }

    public void paint(Graphics g) {
        super.paint(g);

        // Рисуем точки
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                g.fillOval(i * 100 + 45, j * 100 + 45, 10, 10);  // Центр точки
            }
        }

        // Рисуем горизонтальные линии
        g.setColor(Color.BLACK);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 2; j++) {
                if (horizontalLines[i][j]) {
                    // Рисуем горизонтальную линию от центра первой точки до центра второй
                    int x1 = j * 100 + 50; // Центр первой точки по X
                    int y1 = i * 100 + 50; // Центр точки по Y
                    int x2 = (j + 1) * 100 + 50; // Центр второй точки по X
                    int y2 = i * 100 + 50; // Центр второй точки по Y
                    g.drawLine(x1, y1, x2, y2);  // Рисуем линию
                }
            }
        }

        // Рисуем вертикальные линии
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 3; j++) {
                if (verticalLines[i][j]) {
                    // Рисуем вертикальную линию от центра первой точки до центра второй
                    int x1 = j * 100 + 50; // Центр первой точки по X
                    int y1 = i * 100 + 50; // Центр точки по Y
                    int x2 = j * 100 + 50; // Центр второй точки по X
                    int y2 = (i + 1) * 100 + 50; // Центр второй точки по Y
                    g.drawLine(x1, y1, x2, y2);  // Рисуем линию
                }
            }
        }

        // Текущий игрок
        g.setColor(Color.BLACK);
        g.drawString("Текущий игрок: " + currentPlayer, 10, 290);
    }



    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new GameClient().setVisible(true);
            }
        });
    }
}
