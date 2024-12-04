import java.rmi.Remote;
import java.rmi.RemoteException;

public interface GameInterface extends Remote {
    // Метод для начала игры
    void startGame() throws RemoteException;

    // Метод для выполнения хода
    String makeMove(int x1, int y1, int x2, int y2) throws RemoteException;

    // Метод для получения текущего игрока
    String getCurrentPlayer() throws RemoteException;

    // Метод для получения состояния игры (горизонтальные линии)
    boolean[][] getHorizontalLines() throws RemoteException;

    // Метод для получения состояния игры (вертикальные линии)
    boolean[][] getVerticalLines() throws RemoteException;

    // Метод для получения состояния квадратов (X или O в квадратах)
    char[][] getSquares() throws RemoteException;

    // Метод для получения победителя
    String getWinner() throws RemoteException;
    void updateStateFromServer() throws RemoteException;
    void notifyMoveMade() throws RemoteException; // Новый метод для уведомления клиентов

    void addClient(GameInterface client) throws RemoteException;
}
