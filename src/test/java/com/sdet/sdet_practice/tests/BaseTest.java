package com.sdet.sdet_practice.tests;

import com.sdet.sdet_practice.helpers.ParameterProvider;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Random;

public abstract class BaseTest {

    protected WebDriver driver;
    protected WebDriverWait waiter;

    @BeforeMethod
    public void up(){
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        waiter = new WebDriverWait(
                driver,
                Duration.ofSeconds(Long.parseLong(ParameterProvider.get("explicit.wait.time"))));
        driver.get(ParameterProvider.get("base.url"));
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(){
        if (driver != null) {
            driver.quit();
        }
    }

    protected int getRandomQuantity(){
        return new Random().nextInt(3) + 1;
    }
}
