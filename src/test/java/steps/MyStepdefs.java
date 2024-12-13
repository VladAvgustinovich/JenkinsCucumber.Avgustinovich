package steps;

import io.cucumber.java.ru.И;
import org.junit.jupiter.api.Assertions;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import pages.*;
import DataBaseConfig.*;
import static steps.Hooks.driver;


public class MyStepdefs {
    DataBaseConfig dataBaseConfig = new DataBaseConfig();


    @И("Нажать на кнопку \"Добавить\", в поле \"Наименование\" ввести {string}, тип - {string}, чекбокс экзотический - {string}")
    public void addFruitExotic(String name, String type, String isExotic) {
        boolean isExoticBoolean = Boolean.parseBoolean(isExotic);
        // Добавляем новый товар "Манго" через интерфейс
        HomePage homePage = new HomePage(driver);
        AddItemPage page = homePage.selectDropdownOption("Товары");
        page.addItem(name, type, isExoticBoolean);
        Assertions.assertTrue(page.isItemDisplayed(name), "Товар '" + name + "' не отображается на странице");
    }

    @И("Проверить, что товар {string} проявился в БД")
    public void checkDataBase(String itemName) throws SQLException {
        // Проверяем, что товар появился в базе данных
        boolean itemAddInDb = dataBaseConfig.isItemInDatabase(itemName);
        Assertions.assertTrue(itemAddInDb, "Товар" + itemName + "не был добавлен в базу данных");
    }

    @И("Удалить товар {string} из БД")
    // Удаляем товар из БД
    public void deleteFromDataBase(String itemName) throws SQLException {
        dataBaseConfig.deleteItemFromDatabase(itemName);
    }

    @И("Проверяем, что товар {string} удален из БД")
    // Проверяем, что товар удален из базы данных
    public void checkDelFromDataBase(String itemName) throws SQLException {
        boolean itemDelFromDb = dataBaseConfig.isItemInDatabase(itemName);
        Assertions.assertFalse(itemDelFromDb, "Товар" + itemName + "не был удален из базы данных");
    }

    @И("Добавить товар {string}, тип - {string}, экзотическй - {string} в таблицу через БД")
    public void addVegetableExotic(String itemName, String itemType, String isExotic) throws SQLException {
        // Добавляем товар "Бамия" в базу данных напрямую
        boolean isExoticBoolean = Boolean.parseBoolean(isExotic);
        dataBaseConfig.addItemToDatabase(itemName, itemType, isExoticBoolean);
    }

    @И("Проверяем уникальность товара {string} в БД")
    public void checkDataBaseUnique(String itemName) throws SQLException {
        // Проверяем, что товар "Бамия" создан в одном уникальном экземпляре
        int itemCount = dataBaseConfig.countItemsInDatabase(itemName);
        Assertions.assertEquals(1, itemCount, "Товар " + itemName + " не должен повторяться в базе данных");
    }
}
