import java.rmi.Remote;
import java.rmi.RemoteException;

public interface GameInterface extends Remote {

    // Метод для начала игры
    void startGame() throws RemoteException;

    // Метод для выполнения хода
    String makeMove(int x1, int y1, int x2, int y2) throws RemoteException;

    // Метод для получения текущего игрока
    String getCurrentPlayer() throws RemoteException;

    // Метод для проверки окончания игры
    boolean isGameOver() throws RemoteException;

    // Метод для получения победителя игры
    String getWinner() throws RemoteException;

    // Метод для получения всех горизонтальных линий
    boolean[][] getHorizontalLines() throws RemoteException;

    // Метод для получения всех вертикальных линий
    boolean[][] getVerticalLines() throws RemoteException;
}
