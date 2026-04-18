package com.sdet.sdet_practice.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.List;

public class SearchResultsPage extends BasePage{

    private final By pageTitle = By.cssSelector("span.maintext");
    private final By sortSelect = By.id("sort");

    private final By gridProductCards = By.cssSelector(".thumbnails.grid .col-md-3");
    private final By gridProductNames = By.cssSelector(".thumbnails.grid .col-md-3 a.prdocutname");

    public SearchResultsPage(WebDriver driver, WebDriverWait waiter) {
        super(driver, waiter);
    }

    @Step("Получить заголовок страницы поисковой выдачи")
    public String getPageTitle(){
        return waiter.until(ExpectedConditions.visibilityOfElementLocated(pageTitle)).getText().trim();
    }

    @Step("Проверить наличие сортировки на странице поисковой выдачи")
    public boolean hasSorting() {
        return !driver.findElements(sortSelect).isEmpty();
    }

    @Step("Получить количество товаров в поисковой выдаче")
    public int getResultsCount(){
        waiter.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(gridProductCards));
        return driver.findElements(gridProductCards).size();
    }

    @Step("Выбрать сортировку")
    public SearchResultsPage sortByVisibleText(String visibleText){
        WebElement selectElement = waiter.until(ExpectedConditions.elementToBeClickable(sortSelect));
        new Select(selectElement).selectByVisibleText(visibleText);

        waiter.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(gridProductCards));
        return this;
    }

    @Step("Получить список названий товаров")
    public List<String> getResultNames(){
        waiter.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(gridProductNames));
        List<WebElement> elements = driver.findElements(gridProductNames);

        List<String> names = new ArrayList<>();
        for(WebElement element : elements){
            names.add(element.getText().trim());
        }
        return names;
    }

    @Step("Открыть первый доступный для добавления товар, начиная с индекса")
    public ProductPage openFirstAvailableProduct(int startIndex){
        waiter.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(gridProductNames));
        List<WebElement> products = driver.findElements(gridProductNames);

        if(startIndex < 0 || startIndex >= products.size()){
            throw new IllegalArgumentException("Некорректный стартовый индекс товара: " + startIndex);
        }

        for(int i = startIndex; i < products.size(); i++){
            products = driver.findElements(gridProductNames);
            products.get(i).click();

            ProductPage productPage = new ProductPage(driver, waiter);
            if(productPage.canBeAddedProductToCart()){
                return productPage;
            }

            driver.navigate().back();
            waiter.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(gridProductNames));
        }

        throw new IllegalStateException("Не найден доступный для добавления товар, начиная с индекса: " + startIndex);
    }
}
