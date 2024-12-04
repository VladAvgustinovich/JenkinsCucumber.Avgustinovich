package steps;

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

public class Hooks {

    public static WebDriver driver;
    private static Process process;
    private final String url = "jdbc:h2:tcp://localhost:9092/mem:testdb";
    private final String user = "user";
    private final String password = "pass";


    @BeforeAll(order = 1)
    @И("Установлен локальный проект, установлено подключение к БД")
    public static void setUp() {
        if (driver == null) {
            // Запуск локального проекта
            ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", "C:\\Working Project\\qualit-sandbox.jar");
            processBuilder.directory(new File("C:\\Working Project"));

            try {
                process = processBuilder.start();
                Thread.sleep(5000); // Ожидание запуска стенда
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException("Ошибка запуска стенда", e);
            }

            // Настройка WebDriver
            System.setProperty("webdriver.chromedriver/driver", "\\src\\test\\resources\\chromedriver.exe");
            driver = new ChromeDriver();
            driver.manage().window().maximize();
            driver.get("http://localhost:8080");
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
            driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(10));
        }
    }

    @BeforeAll(order = 2)
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
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
