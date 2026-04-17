package com.sdet.sdet_practice.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

public class HomePage extends BasePage{

    private final By topCategoryLinks = By.cssSelector("#categorymenu .categorymenu > li > a[href*='path=']");

    public HomePage(WebDriver driver, WebDriverWait waiter) {
        super(driver, waiter);
    }

    public HomePage open(){
        driver.get("https://automationteststore.com/");
        return this;
    }

    public CategoryPage openFirstCategoryWithAtLeastFourProducts() {
        List<WebElement> categoryLinks = waiter.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(topCategoryLinks));

        for(int i = 0; i < categoryLinks.size(); i++){
            categoryLinks = driver.findElements(topCategoryLinks);
            WebElement categoryLink = categoryLinks.get(i);

            String categoryName = categoryLink.getText().trim();
            categoryLink.click();

            CategoryPage categoryPage = new CategoryPage(driver, waiter);
            if(categoryPage.hasSorting() && categoryPage.getProductsCount() >= 4){
                return categoryPage;
            }

            driver.navigate().back();
            waiter.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(topCategoryLinks));
        }
        throw new IllegalStateException("Не найдена категория с сортировкой и минимум 4 товарами");
    }
}
