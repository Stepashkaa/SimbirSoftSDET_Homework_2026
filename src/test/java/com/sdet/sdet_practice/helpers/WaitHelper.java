package com.sdet.sdet_practice.helpers;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.function.Function;

public class WaitHelper {

    private final WebDriver driver;
    private final WebDriverWait waiter;

    public WaitHelper(WebDriver driver, WebDriverWait waiter) {
        this.driver = driver;
        this.waiter = waiter;
    }

    public WebElement visible(By locator){
        return waiter.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public List<WebElement> visibleAll(By locator){
        return waiter.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
    }

    public WebElement clickable(By locator){
        return waiter.until(ExpectedConditions.elementToBeClickable(locator));
    }

    public WebElement clickable(WebElement element) {
        return waiter.until(ExpectedConditions.elementToBeClickable(element));
    }

    public boolean stale(WebElement element) {
        return waiter.until(ExpectedConditions.stalenessOf(element));
    }

    public <T> T until(Function<WebDriver, T> condition) {
        return waiter.until(condition);
    }
}
