package com.sdet.sdet_practice.pages;

import com.sdet.sdet_practice.helpers.WaitHelper;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public abstract class BasePage {

    protected final WebDriver driver;
    protected final WaitHelper waitHelper;

    public BasePage(WebDriver driver, WaitHelper waitHelper) {
        this.driver = driver;
        this.waitHelper = waitHelper;
    }

    protected void clearAndType(WebElement element, String value) {
        element.click();
        element.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        element.sendKeys(Keys.DELETE);
        element.sendKeys(value);
    }
}
