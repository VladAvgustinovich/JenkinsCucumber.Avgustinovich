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
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.Duration;

public class Hooks extends BaseTest {

    public static WebDriver driver;
    private static Process process;
    private static Connection connection;
    private final String url = "jdbc:h2:tcp://qualit.applineselenoid.fvds.ru:9092/mem:testdb";
    private final String user = "user";
    private final String password = "pass";


    @BeforeAll(order = 2)
    public void dataBaseConnection() {
        // Устанавливаем соединение с БД
        try {
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Подключение к базе данных успешно установлено.");
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка подключения к базе данных: " + e.getMessage());
        }

    }

    @BeforeAll(order = 1)
    public void setUp() {
        // Если драйвер не был инициализирован в BaseTest, можно выполнить настройку драйвера здесь
        if (BaseTest.driver == null) {
            System.setProperty("webdriver.chrome.driver", "src/test/resources/chromedriver.exe");
            BaseTest.driver = new ChromeDriver();
            BaseTest.driver.manage().window().maximize();
            BaseTest.driver.get("https://qualit.applineselenoid.fvds.ru/");
            BaseTest.driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
            BaseTest.driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(10));
        }
    }




    @AfterAll
    public static void tearDown() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        // Ожидаем выпадающий список
        try {
            WebElement navbarDropdown = wait.until(ExpectedConditions.elementToBeClickable(By.id("navbarDropdown")));
            navbarDropdown.click();
            System.out.println("Выпадающий список был кликнут.");
        } catch (Exception e) {
            System.out.println("Ошибка при клике на выпадающий список: " + e.getMessage());
        }

        // Ожидаем кнопку сброса
        try {
            WebElement btnReset = wait.until(ExpectedConditions.elementToBeClickable(By.id("reset")));
            btnReset.click();
            System.out.println("Кнопка сброса была нажата.");
        } catch (Exception e) {
            System.out.println("Ошибка при клике на кнопку сброса: " + e.getMessage());
        }
        driver.quit();
        process.destroyForcibly();
    }
}
