package com.sdet.sdet_practice.pages;

import com.sdet.sdet_practice.helpers.WaitHelper;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

public class SearchResultsPage extends BasePage {

    private final By pageTitle = By.cssSelector("span.maintext");
    private final By sortSelect = By.id("sort");

    private final By gridProductCards = By.cssSelector(".thumbnails.grid .col-md-3");
    private final By gridProductNames = By.cssSelector(".thumbnails.grid .col-md-3 a.prdocutname");

    public SearchResultsPage(WebDriver driver, WaitHelper waitHelper) {
        super(driver, waitHelper);
    }

    @Step("Проверить наличие сортировки на странице поисковой выдачи")
    public boolean hasSorting() {
        return !driver.findElements(sortSelect).isEmpty();
    }

    @Step("Получить количество товаров в поисковой выдаче")
    public int getResultsCount() {
        return waitHelper.visibleAll(gridProductCards).size();
    }

    @Step("Выбрать сортировку")
    public void sortByVisibleText(String visibleText) {
        WebElement selectElement = waitHelper.clickable(sortSelect);
        new Select(selectElement).selectByVisibleText(visibleText);

        waitHelper.visibleAll(gridProductCards);
    }

    @Step("Открыть первый доступный для добавления товар, начиная с индекса")
    public ProductPage openFirstAvailableProduct(int startIndex) {
        List<WebElement> products = waitHelper.visibleAll(gridProductNames);

        if(startIndex < 0 || startIndex >= products.size()) {
            throw new IllegalArgumentException("Некорректный стартовый индекс товара: " + startIndex);
        }

        for(int i = startIndex; i < products.size(); i++) {
            products = driver.findElements(gridProductNames);
            waitHelper.clickable(products.get(i)).click();

            ProductPage productPage = new ProductPage(driver, waitHelper);
            if(productPage.canBeAddedProductToCart()) {
                return productPage;
            }

            driver.navigate().back();
            waitHelper.visibleAll(gridProductNames);
        }

        throw new IllegalStateException("Не найден доступный для добавления товар, начиная с индекса: " + startIndex);
    }
}
