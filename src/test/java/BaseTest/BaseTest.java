package BaseTest;

import io.cucumber.java.AfterAll;
import io.cucumber.java.BeforeAll;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.net.MalformedURLException;
import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class BaseTest {
    public static WebDriver driver;
    private static Connection connection;
    private static final String url = "jdbc:h2:tcp://qualit.applineselenoid.fvds.ru:9092/mem:testdb";
    private static final String user = "user";
    private static final String password = "pass";


    @BeforeAll
    @Step("Инициализация драйвера")
    public static void setup() {
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
        try {
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Подключение к базе данных успешно установлено.");
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка подключения к базе данных: " + e.getMessage());
        }
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
        capabilities.setVersion("109.0");
        Map<String, Object> selenoidOptions = new HashMap<>();
        selenoidOptions.put("enableVNC", true);
        selenoidOptions.put("enableVideo", false);
        capabilities.setCapability("selenoid:options", selenoidOptions);
        try {
            driver = new RemoteWebDriver(
                    URI.create(System.getProperty("selenoid.url", "http://jenkins.applineselenoid.fvds.ru:4444/wd/hub/")).toURL(),
                    capabilities
            );
        } catch (MalformedURLException e) {
            throw new RuntimeException("Ошибка подключения к Selenoid", e);
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

        try {
            if (driver != null) {
                driver.quit();
            }
        } catch (Exception e) {
            System.err.println("Ошибка во время завершения работы драйвера: " + e.getMessage());
        }
    }
}

