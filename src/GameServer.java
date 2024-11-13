import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class GameServer extends UnicastRemoteObject implements GameInterface {

    private String currentPlayer;
    private boolean[][] horizontalLines;
    private boolean[][] verticalLines;
    private static final int SIZE = 3;  // Размер поля

    public GameServer() throws RemoteException {
        super();
        this.currentPlayer = "X"; // Начинает игрок X
        this.horizontalLines = new boolean[SIZE][SIZE - 1];
        this.verticalLines = new boolean[SIZE - 1][SIZE];
    }

    @Override
    public void startGame() throws RemoteException {
        // Логика начала игры
        currentPlayer = "X"; // Начинает игрок X
    }

    @Override
    public String makeMove(int x1, int y1, int x2, int y2) throws RemoteException {
        if (x1 == x2 && Math.abs(y1 - y2) == 1) { // Вертикальная линия
            int minY = Math.min(y1, y2);
            if (!verticalLines[minY][x1]) {
                verticalLines[minY][x1] = true;
                currentPlayer = currentPlayer.equals("X") ? "O" : "X";
                return "Линия нарисована!";
            }
        } else if (y1 == y2 && Math.abs(x1 - x2) == 1) { // Горизонтальная линия
            int minX = Math.min(x1, x2);
            if (!horizontalLines[y1][minX]) {
                horizontalLines[y1][minX] = true;
                currentPlayer = currentPlayer.equals("X") ? "O" : "X";
                return "Линия нарисована!";
            }
        }
        return "Невозможный ход!";
    }

    @Override
    public String getCurrentPlayer() throws RemoteException {
        return currentPlayer;
    }

    @Override
    public boolean isGameOver() throws RemoteException {
        // Логика завершения игры
        return false;
    }

    @Override
    public String getWinner() throws RemoteException {
        // Возвращает победителя игры
        return "X"; // Для упрощения, например, всегда X
    }

    @Override
    public boolean[][] getHorizontalLines() throws RemoteException {
        return horizontalLines;
    }

    @Override
    public boolean[][] getVerticalLines() throws RemoteException {
        return verticalLines;
    }
}
