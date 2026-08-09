package com.sdet.sdet_practice.tests;

import com.sdet.sdet_practice.helpers.ParameterProvider;
import com.sdet.sdet_practice.helpers.WaitHelper;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

public abstract class BaseTest {

    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();
    private static final ThreadLocal<WaitHelper> WAIT_HELPER = new ThreadLocal<>();

    @BeforeMethod
    public void up() {
        WebDriver driver = createDriver();
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

    private WebDriver createDriver() {
        boolean remote = Boolean.parseBoolean(
                System.getProperty("remote", "false")
        );

        if (!remote) {
            return new ChromeDriver();
        }

        ChromeOptions options = new ChromeOptions();
        options.setBrowserVersion(
                System.getProperty("browser.version", "118.0")
        );

        String remoteUrl = System.getProperty(
                "remote.url",
                "http://selenoid:4444/wd/hub"
        );

        try {
            return new RemoteWebDriver(
                    new URL(remoteUrl),
                    options
            );
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(
                    "Некорректный URL Selenoid: " + remoteUrl,
                    e
            );
        }
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
