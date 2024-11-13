import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;

public class GameServer extends UnicastRemoteObject implements GameInterface {
    private static final int GRID_SIZE = 3;
    private final boolean[][] horizontal;
    private final boolean[][] vertical;
    private final char[][] squares;
    private boolean player1Turn = true;

    public GameServer() throws RemoteException {
        horizontal = new boolean[GRID_SIZE + 1][GRID_SIZE];
        vertical = new boolean[GRID_SIZE][GRID_SIZE + 1];
        squares = new char[GRID_SIZE][GRID_SIZE];
    }

    @Override
    public void startGame() throws RemoteException {
        // Инициализация игры
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                squares[row][col] = '\0';  // Пустые квадраты
            }
        }
    }

    @Override
    public String makeMove(int x1, int y1, int x2, int y2) throws RemoteException {
        if (isValidMove(x1, y1, x2, y2)) {
            // Делаем ход
            boolean squareMade = false;
            if (x1 == x2) {
                horizontal[x1][Math.min(y1, y2)] = true;
                squareMade = checkAndUpdateSquares(x1, Math.min(y1, y2), true);
            } else if (y1 == y2) {
                vertical[Math.min(x1, x2)][y1] = true;
                squareMade = checkAndUpdateSquares(Math.min(x1, x2), y1, false);
            }

            // Переход к следующему игроку
            if (!squareMade) {
                togglePlayer();
            }

            return "Линия нарисована!";
        }
        return "Некорректный ход!";
    }

    private boolean isValidMove(int x1, int y1, int x2, int y2) {
        return (Math.abs(x1 - x2) == 1 && y1 == y2) || (Math.abs(y1 - y2) == 1 && x1 == x2);
    }

    private boolean checkAndUpdateSquares(int row, int col, boolean isHorizontal) {
        boolean squareCompleted = false;

        if (isHorizontal) {
            // Проверяем два квадрата, которые могут быть завершены этим ходом
            if (row > 0 && horizontal[row][col] && vertical[row - 1][col] && vertical[row - 1][col + 1] && horizontal[row - 1][col]) {
                squares[row - 1][col] = player1Turn ? 'X' : 'O';
                squareCompleted = true;
            }
            if (row < GRID_SIZE && horizontal[row][col] && vertical[row][col] && vertical[row][col + 1] && horizontal[row + 1][col]) {
                squares[row][col] = player1Turn ? 'X' : 'O';
                squareCompleted = true;
            }
        } else {
            // Проверяем два квадрата, которые могут быть завершены этим ходом
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

    @Override
    public String getCurrentPlayer() throws RemoteException {
        return player1Turn ? "Player 1 (X)" : "Player 2 (O)";
    }

    @Override
    public boolean[][] getHorizontalLines() throws RemoteException {
        return horizontal;
    }

    @Override
    public boolean[][] getVerticalLines() throws RemoteException {
        return vertical;
    }

    @Override
    public char[][] getSquares() throws RemoteException {
        return squares;
    }

    @Override
    public String getWinner() throws RemoteException {
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
}
