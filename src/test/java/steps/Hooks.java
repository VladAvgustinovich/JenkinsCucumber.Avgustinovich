package steps;

import BaseTest.BaseTest;
import DataBaseConfig.DataBaseConfig;
import io.cucumber.java.AfterAll;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.ru.И;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.AddItemPage;
import pages.HomePage;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.Duration;

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


    @AfterAll
    public static void tearDown() {
        HomePage homePage = new HomePage(driver);
        homePage.selectDropdownOption("Сброс данных");
        driver.quit();
        process.destroyForcibly();
    }
}
