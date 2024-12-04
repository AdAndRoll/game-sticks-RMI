import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.rmi.RemoteException;

public class DotsAndBoxesGamePanel extends JPanel {

    private static final int GRID_SIZE = 2;
    private static final int DOT_SIZE = 20;
    private final GameInterface server;
    private boolean[][] horizontal;
    private boolean[][] vertical;
    private char[][] squares;
    private boolean player1Turn = true;
    private boolean gameFinished = false;
    private Point firstPoint = null;

    public DotsAndBoxesGamePanel(GameInterface server) {
        this.server = server;

        try {
            this.horizontal = server.getHorizontalLines();
            this.vertical = server.getVerticalLines();
            this.squares = server.getSquares();
            // Обновляем состояние
            updateStateFromServer();
            repaint();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        setBackground(Color.WHITE);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (!gameFinished) {
                    handleMouseClick(e.getX(), e.getY());
                }
            }
        });

        // Используем таймер для обновления состояния раз в 100 миллисекунд
        Timer timer = new Timer(100, e -> {
            if (!gameFinished) {
                updateStateFromServer();
                if (isGameFinished()) {
                    gameFinished = true;
                }
                repaint();
            }
            ;
        });
        timer.start();
    }

    private void handleMouseClick(int x, int y) {
        int panelSize = Math.min(getWidth(), getHeight());
        int cellSize = panelSize / (GRID_SIZE + 2);
        int offset = (panelSize - (GRID_SIZE * cellSize)) / 2;

        int col = (x - offset + cellSize / 2) / cellSize;
        int row = (y - offset + cellSize / 2) / cellSize;

        if (col < 0 || col > GRID_SIZE || row < 0 || row > GRID_SIZE) return;

        Point selectedPoint = new Point(row, col);

        if (firstPoint == null) {
            firstPoint = selectedPoint;
        } else {
            if (isValidMove(firstPoint, selectedPoint)) {
                try {
                    // Передаем ход серверу
                    String response = server.makeMove(firstPoint.x, firstPoint.y, selectedPoint.x, selectedPoint.y);
                    System.out.println(response);

                    // Обновляем состояние
                    updateStateFromServer();

                    repaint();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            firstPoint = null;
        }

        if (isGameFinished()) {
            gameFinished = true;
            repaint();
        }
    }

    public void updateStateFromServer() {
        try {
            this.horizontal = server.getHorizontalLines();
            this.vertical = server.getVerticalLines();
            this.squares = server.getSquares();
            this.player1Turn = server.getCurrentPlayer().equals("Player 1 (X)");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private boolean isValidMove(Point p1, Point p2) {
        int row1 = p1.x, col1 = p1.y;
        int row2 = p2.x, col2 = p2.y;

        if (row1 == row2 && Math.abs(col1 - col2) == 1) {
            return !horizontal[row1][Math.min(col1, col2)];
        } else if (col1 == col2 && Math.abs(row1 - row2) == 1) {
            return !vertical[Math.min(row1, row2)][col1];
        }
        return false;
    }

    private void togglePlayer() {
        player1Turn = !player1Turn;
    }

    private boolean isGameFinished() {
        try {
            // Проверяем на наличие победителя
            String winner = server.getWinner();
            if (winner == null) {
                return true;
            }

            // Проверяем, есть ли ещё доступные ходы
            // Если нет доступных ходов, то игра также считается завершённой
            for (int row = 0; row < GRID_SIZE; row++) {
                for (int col = 0; col < GRID_SIZE; col++) {
                    if (squares[row][col] == '\0') {
                        return false;  // Есть хотя бы один незаполненный квадрат
                    }
                }
            }
            return true; // Нет незаполненных квадратов
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getWinner() {
        int player1Score = 0;
        int player2Score = 0;

        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                if (squares[row][col] == 'X') {
                    player1Score++;
                } else if (squares[row][col] == 'O') {
                    player2Score++;
                }
            }
        }

        if (player1Score > player2Score) {
            return "Player 1 (X) Wins!";
        } else if (player2Score > player1Score) {
            return "Player 2 (O) Wins!";
        } else {
            return "It's a Draw!";
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int panelSize = Math.min(getWidth(), getHeight());
        int cellSize = panelSize / (GRID_SIZE + 2);
        int offset = (panelSize - (GRID_SIZE * cellSize)) / 2;

        g.setColor(Color.BLACK);
        if (gameFinished) {
            g.drawString(getWinner(), 20, 20);  // выводим победителя или ничью
            return;
        }

        // Вывод текущего хода
        String currentPlayer = player1Turn ? "Player 1 (X)" : "Player 2 (O)";
        g.drawString("Current turn: " + currentPlayer, 20, 20);

        // Рисуем сетку точек
        for (int row = 0; row <= GRID_SIZE; row++) {
            for (int col = 0; col <= GRID_SIZE; col++) {
                g.fillOval(offset + col * cellSize - DOT_SIZE / 2, offset + row * cellSize - DOT_SIZE / 2, DOT_SIZE, DOT_SIZE);
            }
        }

        // Рисуем линии
        for (int row = 0; row <= GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                if (horizontal[row][col]) {
                    g.drawLine(offset + col * cellSize, offset + row * cellSize, offset + (col + 1) * cellSize, offset + row * cellSize);
                }
            }
        }
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col <= GRID_SIZE; col++) {
                if (vertical[row][col]) {
                    g.drawLine(offset + col * cellSize, offset + row * cellSize, offset + col * cellSize, offset + (row + 1) * cellSize);
                }
            }
        }

        // Рисуем символы (крестики и нолики)
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                if (squares[row][col] != '\0') {
                    g.drawString(String.valueOf(squares[row][col]), offset + col * cellSize + cellSize / 3, offset + row * cellSize + 2 * cellSize / 3);
                }
            }
        }
    }
}
