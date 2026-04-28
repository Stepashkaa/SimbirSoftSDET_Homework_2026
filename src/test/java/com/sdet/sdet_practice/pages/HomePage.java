package com.sdet.sdet_practice.pages;

import com.sdet.sdet_practice.helpers.ParameterProvider;
import com.sdet.sdet_practice.helpers.WaitHelper;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import java.util.List;

public class HomePage extends BasePage {

    private final By topCategoryLinks = By.cssSelector("#categorymenu .categorymenu > li > a[href*='path=']");

    private final By search = By.cssSelector("#filter_keyword");
    private final By searchButton = By.cssSelector(".button-in-search");

    private final By homePageProductCards = By.cssSelector(".thumbnails.list-inline .col-md-3");
    private final By homePageProductNames = By.cssSelector("a.prdocutname");

    public HomePage(WebDriver driver, WaitHelper waitHelper) {
        super(driver, waitHelper);
    }

    @Step("Открыть главную страницу")
    public HomePage open() {
        driver.get(ParameterProvider.get("base.url"));
        waitHelper.visible(search);
        waitHelper.until(driver -> !driver.findElements(homePageProductNames).isEmpty());

        return this;
    }

    @Step("Проверка чтобы товаров в категории было не менее 4 и была сортировка")
    public CategoryPage openFirstCategoryWithAtLeastFourProducts() {
        List<WebElement> categoryLinks = waitHelper.visibleAll(topCategoryLinks);

        for(int i = 0; i < categoryLinks.size(); i++) {
            categoryLinks = driver.findElements(topCategoryLinks);
            WebElement categoryLink = categoryLinks.get(i);

            waitHelper.clickable(categoryLink).click();

            CategoryPage categoryPage = new CategoryPage(driver, waitHelper);
            if(categoryPage.hasSorting() && categoryPage.getProductsCount() >= 4) {
                return categoryPage;
            }

            driver.navigate().back();
            waitHelper.visibleAll(topCategoryLinks);
        }
        throw new IllegalStateException("Не найдена категория с сортировкой и минимум 4 товарами");
    }

    @Step("Выполнить поиск по запросу")
    public SearchResultsPage searchFor(String query) {
        WebElement searchField = waitHelper.visible(search);
        clearAndType(searchField, query);

        waitHelper.clickable(searchButton).click();

        return new SearchResultsPage(driver, waitHelper);
    }

    @Step("Получить количество товаров на главной странице")
    public int getHomePageProductsCount() {
        return waitHelper.visibleAll(homePageProductCards).size();
    }

    @Step("Открыть товар с главной страницы по индексу")
    public ProductPage openHomePageProductByIndex(int index) {
        waitHelper.until(driver -> !driver.findElements(homePageProductNames).isEmpty());

        List<WebElement> products = driver.findElements(homePageProductNames);

        if(index < 0 || index >= products.size()) {
            throw new IllegalArgumentException("Некорректный индекс товара на главной странице: " + index);
        }

        waitHelper.clickable(products.get(index)).click();

        return new ProductPage(driver, waitHelper);
    }
}
