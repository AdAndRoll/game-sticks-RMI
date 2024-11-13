import javax.swing.*;
import java.rmi.Naming;

public class GameClient extends JFrame {

    public GameClient() {
        try {
            // Подключаемся к серверу RMI
            GameInterface server = (GameInterface) Naming.lookup("rmi://localhost/GameServer");

            // Создаем панель с сервером
            DotsAndBoxesGamePanel gamePanel = new DotsAndBoxesGamePanel(server);

            // Настройки окна
            setTitle("Dots and Boxes");
            setSize(400, 400);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocationRelativeTo(null);
            setResizable(false);
            add(gamePanel);  // Добавляем панель с игрой в окно
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            GameClient client = new GameClient();
            client.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
