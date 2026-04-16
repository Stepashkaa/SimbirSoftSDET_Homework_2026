package com.sdet.sdet_practice.utils;

import io.qameta.allure.Allure;
import io.qameta.allure.Attachment;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class AllureUtils {

    private AllureUtils() {
    }

    @Attachment(value = "{attachName}", type = "image/png")
    public static byte[] attachScreenshot(WebDriver driver, String attachName) {
        try {
            return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        } catch (Exception e) {
            return new byte[0];
        }
    }

    @Attachment(value = "{attachName}", type = "text/html")
    public static String attachPageSource(WebDriver driver, String attachName) {
        try {
            return driver.getPageSource();
        } catch (Exception e) {
            return "<no page source>";
        }
    }

    public static void saveScreenshotToFile(WebDriver driver, String testName, String status) {
        try {
            byte[] png = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            File dir = new File("target/screenshots");

            if (!dir.exists()) {
                dir.mkdirs();
            }

            File out = new File(dir, testName + "_" + status + "_" + timestamp + ".png");
            Files.write(out.toPath(), png);

            Allure.addAttachment("Saved file path", "text/plain", out.getAbsolutePath());
        } catch (Exception ignored) {
        }
    }
}
