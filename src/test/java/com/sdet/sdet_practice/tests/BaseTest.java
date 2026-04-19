package com.sdet.sdet_practice.tests;

import com.sdet.sdet_practice.helpers.ParameterProvider;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public abstract class BaseTest {

    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();
    private static final ThreadLocal<WebDriverWait> WAITER = new ThreadLocal<>();

    protected WebDriver driver;
    protected WebDriverWait waiter;

    @BeforeMethod
    public void up(){
        WebDriver localDriver = new ChromeDriver();
        localDriver.manage().window().maximize();

        WebDriverWait localWaiter = new WebDriverWait(
                localDriver,
                Duration.ofSeconds(Long.parseLong(ParameterProvider.get("explicit.wait.time")))
        );

        DRIVER.set(localDriver);
        WAITER.set(localWaiter);

        driver = getDriver();
        waiter = getWaiter();

        driver.get(ParameterProvider.get("base.url"));
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(){
        WebDriver localDriver = DRIVER.get();
        if (localDriver != null) {
            localDriver.quit();
            DRIVER.remove();
        }
        WAITER.remove();
    }

    protected WebDriver getDriver() {
        return DRIVER.get();
    }

    protected WebDriverWait getWaiter() {
        return WAITER.get();
    }

    protected int getRandomQuantity(){
        return ThreadLocalRandom.current().nextInt(1, 4);
    }
}
