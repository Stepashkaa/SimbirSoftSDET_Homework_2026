package com.sdet.sdet_practice.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class HomePage extends BasePage{

    private final By apparelAndAccessoriesCategory = By.cssSelector("a[href*='path=68']");
    private final By shoesCategory = By.cssSelector("a[href*='path=68_69']");

    public HomePage(WebDriver driver, WebDriverWait waiter) {
        super(driver, waiter);
    }

    public HomePage open(){
        driver.get("https://automationteststore.com/");
        return this;
    }

    public CategoryPage openShoesCategory() {
        WebElement parent = waiter.until(ExpectedConditions.visibilityOfElementLocated(apparelAndAccessoriesCategory));
        new Actions(driver).moveToElement(parent).perform();

        waiter.until(ExpectedConditions.elementToBeClickable(shoesCategory)).click();
        return new CategoryPage(driver, waiter);
    }
}
