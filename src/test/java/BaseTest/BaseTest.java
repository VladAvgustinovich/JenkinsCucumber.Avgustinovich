package BaseTest;

import io.qameta.allure.Step;
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
import java.util.HashMap;
import java.util.Map;

public class BaseTest {
    public static WebDriver driver;

    @BeforeAll
    @Step("Инициализация драйвера")
    static void setup() {
        String driverType = System.getProperty("type.driver", "local");
        if ("remote".equalsIgnoreCase(driverType)) {
            initRemoteDriver();
        } else {
            initLocalDriver();
        }
        driver.manage().window().maximize();
        driver.get(System.getProperty("app.url", "https://qualit.applineselenoid.fvds.ru/"));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(10));
    }

    private static void initLocalDriver() {
        String browser = System.getProperty("type.browser", "chrome");
        if ("firefox".equalsIgnoreCase(browser)) {
            System.setProperty("webdriver.gecko.driver", "src/main/resources/drivers/geckodriver");
            driver = new FirefoxDriver();
        } else {
            System.setProperty("webdriver.chrome.driver", "src/main/resources/drivers/chromedriver");
            driver = new ChromeDriver();
        }
    }

    private static void initRemoteDriver() {
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setBrowserName(System.getProperty("type.browser", "chrome"));
        capabilities.setVersion(System.getProperty("browser.version", "109.0"));

        Map<String, Object> selenoidOptions = new HashMap<>();
        selenoidOptions.put("enableVNC", true);
        selenoidOptions.put("enableVideo", false);
        capabilities.setCapability("selenoid:options", selenoidOptions);

        try {
            driver = new RemoteWebDriver(
                    URI.create(System.getProperty("selenoid.url", "http://jenkins.applineselenoid.fvds.ru:4444/wd/hub")).toURL(),
                    capabilities
            );
        } catch (MalformedURLException e) {
            throw new RuntimeException("Ошибка подключения к Selenoid", e);
        }
    }

    @AfterAll
    @Step("Завершение работы драйвера")
    static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}

