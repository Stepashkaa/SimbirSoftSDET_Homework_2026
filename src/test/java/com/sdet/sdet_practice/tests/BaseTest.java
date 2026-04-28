package com.sdet.sdet_practice.tests;

import com.sdet.sdet_practice.helpers.ParameterProvider;
import com.sdet.sdet_practice.helpers.WaitHelper;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

public abstract class BaseTest {

    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();
    private static final ThreadLocal<WaitHelper> WAIT_HELPER = new ThreadLocal<>();

    @BeforeMethod
    public void up() {
        WebDriver driver = new ChromeDriver();
        driver.manage().window().maximize();

        WebDriverWait waiter = new WebDriverWait(
                driver,
                Duration.ofSeconds(Long.parseLong(ParameterProvider.get("explicit.wait.time")))
        );

        WaitHelper waitHelper = new WaitHelper(driver, waiter);

        DRIVER.set(driver);
        WAIT_HELPER.set(waitHelper);

        driver.get(ParameterProvider.get("base.url"));
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        WebDriver driver = DRIVER.get();

        if (driver != null) {
            driver.quit();
            DRIVER.remove();
        }

        WAIT_HELPER.remove();
    }

    protected WebDriver getDriver() {
        return DRIVER.get();
    }

    protected WaitHelper getWaitHelper() {
        return WAIT_HELPER.get();
    }

    protected int getRandomQuantity() {
        return ThreadLocalRandom.current().nextInt(1, 4);
    }
}
