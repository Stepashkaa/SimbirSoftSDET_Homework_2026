package com.sdet.sdet_practice.pages;

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.math.BigDecimal;

public abstract class BasePage {

    protected final WebDriver driver;
    protected final WebDriverWait waiter;

    public BasePage(WebDriver driver, WebDriverWait waiter) {
        this.driver = driver;
        this.waiter = waiter;
    }

    protected BigDecimal parseMoney(String raw){
        String normalized = raw.replace("$", "").replace(",", "").trim();
        return new BigDecimal(normalized);
    }

    protected void clearAndType(WebElement element, String value){
        element.click();
        element.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        element.sendKeys(Keys.DELETE);
        element.sendKeys(value);
    }
}
