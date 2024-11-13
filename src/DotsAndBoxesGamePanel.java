import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DotsAndBoxesGamePanel extends JPanel {
    private static final int GRID_SIZE = 2;
    private static final int DOT_SIZE = 20;
    private final boolean[][] horizontal;
    private final boolean[][] vertical;
    private final char[][] squares;
    private boolean player1Turn = true;
    private boolean gameFinished = false;
    private Point firstPoint = null;

    public DotsAndBoxesGamePanel(GameInterface server) {
        horizontal = new boolean[GRID_SIZE + 1][GRID_SIZE];
        vertical = new boolean[GRID_SIZE][GRID_SIZE + 1];
        squares = new char[GRID_SIZE][GRID_SIZE];

        setBackground(Color.WHITE);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (!gameFinished) {
                    handleMouseClick(e.getX(), e.getY());
                }
            }
        });
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
                makeMove(firstPoint, selectedPoint);
                repaint();
            }
            firstPoint = null; // сброс первой точки после каждой линии
        }

        if (isGameFinished()) {
            gameFinished = true;
            repaint();
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

    private void makeMove(Point p1, Point p2) {
        int row1 = p1.x, col1 = p1.y;
        int row2 = p2.x, col2 = p2.y;

        boolean squareMade = false;
        if (row1 == row2) {
            horizontal[row1][Math.min(col1, col2)] = true;
            squareMade = checkAndUpdateSquares(row1, Math.min(col1, col2), true);
        } else if (col1 == col2) {
            vertical[Math.min(row1, row2)][col1] = true;
            squareMade = checkAndUpdateSquares(Math.min(row1, row2), col1, false);
        }

        if (!squareMade) {
            togglePlayer();
        }

        firstPoint = null;
    }

    private boolean checkAndUpdateSquares(int row, int col, boolean isHorizontal) {
        boolean squareCompleted = false;

        if (isHorizontal) {
            if (row > 0 && horizontal[row][col] && vertical[row - 1][col] && vertical[row - 1][col + 1] && horizontal[row - 1][col]) {
                squares[row - 1][col] = player1Turn ? 'X' : 'O';
                squareCompleted = true;
            }
            if (row < GRID_SIZE && horizontal[row][col] && vertical[row][col] && vertical[row][col + 1] && horizontal[row + 1][col]) {
                squares[row][col] = player1Turn ? 'X' : 'O';
                squareCompleted = true;
            }
        } else {
            if (col > 0 && vertical[row][col] && horizontal[row][col - 1] && horizontal[row + 1][col - 1] && vertical[row][col - 1]) {
                squares[row][col - 1] = player1Turn ? 'X' : 'O';
                squareCompleted = true;
            }
            if (col < GRID_SIZE && vertical[row][col] && horizontal[row][col] && horizontal[row + 1][col] && vertical[row][col + 1]) {
                squares[row][col] = player1Turn ? 'X' : 'O';
                squareCompleted = true;
            }
        }

        return squareCompleted;
    }

    private void togglePlayer() {
        player1Turn = !player1Turn;
    }

    private boolean isGameFinished() {
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                if (squares[row][col] == '\0') {
                    return false;
                }
            }
        }
        return true;
    }

    private String getWinner() {
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