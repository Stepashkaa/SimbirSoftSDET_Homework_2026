package com.sdet.sdet_practice.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.sdet.sdet_practice.utilits.MoneyUtils.parseMoney;

public class CategoryPage extends BasePage {

    private final By pageTitle = By.className("maintext");
    private final By sortSelect = By.id("sort");

    private final By gridProductCards = By.cssSelector(".thumbnails.grid .col-md-3");
    private final By gridProductNames = By.cssSelector(".thumbnails.grid .col-md-3 a.prdocutname");
    private final By gridProductPrices = By.cssSelector(".thumbnails.grid .col-md-3 .price .oneprice");

    public CategoryPage(WebDriver driver, WebDriverWait waitHelper) {
        super(driver, waitHelper);
    }

    @Step("Получить заголовок текущей категории")
    public String getPageTitle() {
        return waitHelper.visible(pageTitle).getText().trim();
    }

    @Step("Проверить наличие сортировки на странице категории")
    public boolean hasSorting() {
        return !driver.findElements(sortSelect).isEmpty();
    }

    @Step("Получить количество товаров в категории")
    public int getProductsCount() {
        return waitHelper.visibleAll(gridProductCards).size();
    }

    @Step("Выбрать сортировку")
    public CategoryPage sortByVisibleText(String visibleText) {
        WebElement selectElement = waitHelper.clickable(sortSelect);
        new Select(selectElement).selectByVisibleText(visibleText);

        waitHelper.visibleAll(gridProductCards);
        return this;
    }

    @Step("Получить список названий товаров")
    public List<String> getProductNames() {
        return waitHelper.visibleAll(gridProductNames).stream()
                .map(element -> element.getText().trim())
                .toList();
    }

    @Step("Получить список цен товаров")
    public List<BigDecimal> getProductPrices() {
        List<WebElement> elements = waitHelper.visibleAll(gridProductPrices);

        List<BigDecimal> prices = new ArrayList<>();
        for (WebElement element : elements) {
            prices.add(parseMoney(element.getText().trim()));
        }
        return prices;
    }
}
