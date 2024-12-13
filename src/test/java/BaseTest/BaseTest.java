package BaseTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

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

        // Определение типа браузера и настройка WebDriver
        String browser = System.getProperty("browser", "chrome"); // По умолчанию chrome
        String selenoidUrl = System.getProperty("http://jenkins.applineselenoid.fvds.ru:4444/wd/hub/"); // Если Selenoid не используется, будет null

        if (selenoidUrl != null) {
            // Настройка удаленного WebDriver для Selenoid
            DesiredCapabilities capabilities = new DesiredCapabilities();
            Map<String, Object> selenoidOptions = new HashMap<>();
            selenoidOptions.put("browserName", browser);
            selenoidOptions.put("browserVersion", "109.0");
            selenoidOptions.put("enableVNC", true);
            selenoidOptions.put("enableVideo", false);
            capabilities.setCapability("selenoid:options", selenoidOptions);

            try {
                driver = new RemoteWebDriver(
                        URI.create(selenoidUrl).toURL(),
                        capabilities
                );
            } catch (Exception e) {
                throw new RuntimeException("Ошибка подключения к Selenoid", e);
            }
        } else {
            // Локальный запуск браузера
            switch (browser) {
                case "firefox":
                    System.setProperty("webdriver.gecko.driver", "src/test/resources/geckodriver.exe");
                    driver = new FirefoxDriver();
                    break;
                case "chrome":
                default:
                    System.setProperty("webdriver.chrome.driver", "src/test/resources/chromedriver.exe");
                    driver = new ChromeDriver();
                    break;
            }
        }

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