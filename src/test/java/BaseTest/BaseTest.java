package BaseTest;

import org.apache.commons.exec.OS;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.time.Duration;


public class BaseTest {
    public static WebDriver driver;
    private static Process process;

    @BeforeAll
    static void setup() {
        // Запуск стенда
        ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", "qualit-sandbox.jar");
        processBuilder.directory(new File("C:\\Working Project"));

        try {
            process = processBuilder.start();
            Thread.sleep(10000); // Ожидание запуска стенда
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Ошибка запуска стенда", e);
        }

        // Определение типа драйвера
        String driverType = System.getProperty("type.driver", "local");
        if ("remote".equalsIgnoreCase(driverType)) {
            initRemoteDriver();
        } else {
            initLocalDriver();
        }

        driver.manage().window().maximize();
        driver.get("http://localhost:8080");
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(10));
    }

    private static void initLocalDriver() {
        String browser = System.getProperty("type.browser", "chrome");
        if (OS.isFamilyWindows()) {
            setupDriverForOs(browser, "src/test/resources/geckodriver.exe", "src/test/resources/chromedriver.exe");
        } else if (OS.isFamilyMac()) {
            setupDriverForOs(browser, "src/test/resources/geckodriver_mac", "src/test/resources/chromedriver_mac");
        } else if (OS.isFamilyUnix()) {
            setupDriverForOs(browser, "src/test/resources/geckodriver_unix", "src/test/resources/chromedriver_unix");
        } else {
            throw new RuntimeException("Неизвестная ОС, драйвера не найдены");
        }
    }

    private static void setupDriverForOs(String browser, String geckoPath, String chromePath) {
        switch (browser.toLowerCase()) {
            case "firefox":
                System.setProperty("webdriver.gecko.driver", geckoPath);
                driver = new FirefoxDriver();
                break;
            case "chrome":
            default:
                System.setProperty("webdriver.chrome.driver", chromePath);
                driver = new ChromeDriver();
                break;
        }
    }

    private static void initRemoteDriver() {
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setBrowserName(System.getProperty("type.browser", "chrome"));
        capabilities.setVersion(System.getProperty("browser.version", "latest"));
        capabilities.setCapability("enableVNC", true);
        capabilities.setCapability("enableVideo", false);
        try {
            driver = new RemoteWebDriver(URI.create(System.getProperty("selenoid.url")).toURL(), capabilities);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Ошибка подключения к Selenoid", e);
        }
    }

    @AfterAll
    static void tearDown() {
        try {
            if (driver != null) {
                driver.quit();
            }
        } catch (Exception e) {
            System.err.println("Ошибка во время завершения работы драйвера: " + e.getMessage());
        } finally {
            if (process != null) {
                process.destroyForcibly();
            }
        }
    }
}


