import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class GameServerApp {
    public static void main(String[] args) {
        try {
            LocateRegistry.createRegistry(1099);
            GameServer server = new GameServer();
            Naming.rebind("GameServer", server);
            System.out.println("Сервер запущен");
        } catch (Exception e) {
            System.out.println("Ошибка запуска сервера: " + e.getMessage());
        }
    }
}
