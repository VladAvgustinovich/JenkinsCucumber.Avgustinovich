package BaseTest;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import java.net.MalformedURLException;
import java.net.URI;
import java.time.Duration;

public class BaseTest {
    public static WebDriver driver;

    @BeforeAll
    static void setup() {
        // Определение типа драйвера
        String driverType = System.getProperty("type.driver", "remote");
        if ("remote".equalsIgnoreCase(driverType)) {
            initRemoteDriver();
        } else  initLocalDriver();
        driver.manage().window().maximize();
        driver.get("https://qualit.applineselenoid.fvds.ru/");
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(10));
    }
    private static void initLocalDriver() {
        System.setProperty("webdriver.chrome.driver", "src/main/resources/drivers/chromedriver");
        driver = new ChromeDriver();
    }

    private static void initRemoteDriver() {
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setBrowserName(System.getProperty("type.browser", "chrome"));
        capabilities.setVersion("109.0");
        capabilities.setCapability("enableVNC", true);
        capabilities.setCapability("enableVideo", false);
        try {
            driver = new RemoteWebDriver(URI.create(System.getProperty("selenoid.url", "http://jenkins.applineselenoid.fvds.ru:4444/wd/hub/")).toURL(), capabilities);
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
        }
    }
}
