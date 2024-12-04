package BaseTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.File;
import java.io.IOException;
import java.time.Duration;

public  class BaseTest {
    public static WebDriver driver;
    private static Process process;

    @BeforeAll
    static void setup() {

        // Запуск стенда
        ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", "C:\\Working Project\\qualit-sandbox.jar");
        processBuilder.directory(new File("C:\\Working Project"));

        try {
            process = processBuilder.start();
            Thread.sleep(10000); // Ожидание запуска стенда
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

    @AfterAll
    static void tearDown() {
        WebElement navbarDropdown = driver.findElement(By.id("navbarDropdown"));
        navbarDropdown.click();
        WebElement btnReset = driver.findElement(By.id("reset"));
        btnReset.click();
        driver.quit();
        process.destroyForcibly();
        }
}

