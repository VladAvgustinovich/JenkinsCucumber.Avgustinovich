package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class AddItemPage {
    private WebDriver driver;

    public AddItemPage(WebDriver driver) {
        this.driver = driver;
    }

    public void addItem(String name, String type, boolean isExotic) {
        driver.findElement(By.xpath("//button[contains(text(), 'Добавить')]")).click();
        driver.findElement(By.id("name")).sendKeys(name);

        if (type != null) {
            driver.findElement(By.id("type")).click();
            driver.findElement(By.xpath("//select[@id='type']/option[@value='" + type + "']")).click();
        }

        if (isExotic) {
            WebElement checkboxExotic = driver.findElement(By.id("exotic"));
            checkboxExotic.click();
        }

        driver.findElement(By.id("save")).click();
    }

    public boolean isItemDisplayed(String name) {
        try {
            return driver.findElement(By.xpath("//td[text()='" + name + "']")).isDisplayed();
        } catch (Exception e) {
            return false;
        }

    }
}
