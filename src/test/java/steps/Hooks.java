package steps;

import BaseTest.BaseTest;
import io.cucumber.java.BeforeAll;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class Hooks extends BaseTest {

    private static Process process;
    private static Connection connection;
    private static final String url = "jdbc:h2:tcp://qualit.applineselenoid.fvds.ru:9092/mem:testdb";
    private static final String user = "user";
    private static final String password = "pass";


    @BeforeAll(order = 1)
    public static void dataBaseConnection() {
        // Устанавливаем соединение с БД
        try {
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Подключение к базе данных успешно установлено.");
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка подключения к базе данных: " + e.getMessage());
        }

    }

}
