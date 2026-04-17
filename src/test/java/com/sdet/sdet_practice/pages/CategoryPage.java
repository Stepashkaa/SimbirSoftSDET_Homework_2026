package com.sdet.sdet_practice.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CategoryPage extends BasePage{

    private final By pageTitle = By.className("maintext");
    private final By sortSelect = By.id("sort");

    private final By gridProductCards = By.cssSelector(".thumbnails.grid .col-md-3");
    private final By gridProductNames = By.cssSelector(".thumbnails.grid .col-md-3 a.prdocutname");
    private final By gridProductPrices = By.cssSelector(".thumbnails.grid .col-md-3 .price .oneprice");

    public CategoryPage(WebDriver driver, WebDriverWait waiter) {
        super(driver, waiter);
    }

    public String getPageTitle(){
        return waiter.until(ExpectedConditions.visibilityOfElementLocated(pageTitle)).getText().trim();
    }

    public boolean hasSorting() {
        return !driver.findElements(sortSelect).isEmpty();
    }

    public int getProductsCount(){
        waiter.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(gridProductCards));
        return driver.findElements(gridProductCards).size();
    }

    public CategoryPage sortByVisibleText(String visibleText){
        WebElement selectElement = waiter.until(ExpectedConditions.elementToBeClickable(sortSelect));
        new Select(selectElement).selectByVisibleText(visibleText);

        waiter.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(gridProductCards));
        return this;
    }

    public List<String> getProductNames(){
        waiter.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(gridProductNames));
        List<WebElement> elements = driver.findElements(gridProductNames);

        List<String> names = new ArrayList<>();
        for(WebElement element : elements){
            names.add(element.getText().trim());
        }
        return names;
    }

    public List<BigDecimal> getProductPrices(){
        waiter.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(gridProductPrices));
        List<WebElement> elements = driver.findElements(gridProductPrices);

        List<BigDecimal> prices = new ArrayList<>();
        for(WebElement element : elements){
            String rawText = element.getText().trim();
            String normalized = rawText.replace("$", "").replace(",","").trim();
            prices.add(new BigDecimal(normalized));
        }
        return prices;
    }
}
