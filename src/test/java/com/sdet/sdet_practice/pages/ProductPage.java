package com.sdet.sdet_practice.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.math.BigDecimal;
import java.util.List;

public class ProductPage extends BasePage{

    private final By productName = By.cssSelector("h1.productname .bgnone");
    private final By productPrice = By.cssSelector(".productfilneprice");

    private final By productForm = By.cssSelector("form#product");
    private final By quantityField = By.cssSelector("#product_quantity");
    private final By outOfStockLabel = By.cssSelector(".productpagecart .nostock");
    private final By addToCartButton = By.cssSelector(".productpagecart a.cart");

    private final By cartLink = By.cssSelector("a[href*='checkout/cart']");

    private final By formGroups = By.cssSelector("form#product .form-group");
    private final By requiredMark = By.cssSelector(".required");

    public ProductPage(WebDriver driver, WebDriverWait waiter) {
        super(driver, waiter);
    }

    @Step("Получить название товара")
    public String getProductName(){
        return waiter.until(ExpectedConditions.visibilityOfElementLocated(productName)).getText().trim();
    }

    @Step("Получить цену товара")
    public BigDecimal getProductPrice(){
        WebElement element = waiter.until(ExpectedConditions.visibilityOfElementLocated(productPrice));
        return parseMoney(element.getText().trim());
    }

    @Step("Проверить, что товар отсутствует на складе")
    public boolean isOutOfStock(){
        return !driver.findElements(outOfStockLabel).isEmpty();
    }

    @Step("Проверить, что товар можно добавить в корзину")
    public boolean canBeAddedProductToCart(){
        return !isOutOfStock() && !driver.findElements(addToCartButton).isEmpty();
    }

    @Step("Выбрать обязательные параметры товара")
    public ProductPage selectRequiredOptionsIfPresent(){
        waiter.until(ExpectedConditions.visibilityOfElementLocated(productForm));

        List<WebElement> groups = driver.findElements(formGroups);

        for (WebElement group : groups) {
            boolean isRequired = !group.findElements(requiredMark).isEmpty();
            if (!isRequired) {
                continue;
            }

            List<WebElement> selects = group.findElements(By.tagName("select"));
            if (!selects.isEmpty()) {
                selectFirstAvailableOption(selects.get(0));
                continue;
            }

            List<WebElement> radios = group.findElements(By.cssSelector("input[type='radio']"));
            if (!radios.isEmpty()) {
                selectFirstAvailableRadio(radios);
            }
        }

        return this;
    }

    private void selectFirstAvailableOption(WebElement selectElement){
        Select select = new Select(selectElement);
        List<WebElement> options = select.getOptions();

        for (WebElement option : options) {
            String value = option.getAttribute("value");
            boolean disabled = option.getAttribute("disabled") != null;

            if (!disabled && value != null && !value.trim().isBlank()) {
                select.selectByValue(value);
                return;
            }
        }

        throw new IllegalStateException("Не найден option в select");
    }

    private void selectFirstAvailableRadio(List<WebElement> radios){
        for (WebElement radio : radios) {
            boolean disabled = radio.getAttribute("disabled") != null;

            if (!disabled) {
                if (!radio.isSelected()) {
                    radio.click();
                }
                return;
            }
        }

        throw new IllegalStateException("Не найден доступный radio option среди обязательных параметров");
    }

    @Step("Установить количество товара")
    public ProductPage setQuantity(int quantity){
        WebElement quant = waiter.until(ExpectedConditions.visibilityOfElementLocated(quantityField));
        clearAndType(quant, String.valueOf(quantity));
        return this;
    }

    @Step("Добавить товар в корзину")
    public ProductPage addToBasket(){
        waiter.until(ExpectedConditions.elementToBeClickable(addToCartButton)).click();

        waiter.until(driver -> {
            try {
                return driver.getCurrentUrl().contains("checkout/cart")
                        || !driver.findElements(cartLink).isEmpty();
            } catch (Exception e) {
                return false;
            }
        });

        return this;
    }

    @Step("Переходим в корзину")
    public CartPage openCart(){
        if (driver.getCurrentUrl().contains("checkout/cart")) {
            return new CartPage(driver, waiter);
        }

        waiter.until(driver -> {
            try {
                return driver.getCurrentUrl().contains("checkout/cart")
                        || !driver.findElements(cartLink).isEmpty();
            } catch (Exception e) {
                return false;
            }
        });

        if (!driver.getCurrentUrl().contains("checkout/cart")) {
            WebElement cartElement = waiter.until(ExpectedConditions.presenceOfElementLocated(cartLink));
            cartElement.click();
        }

        waiter.until(ExpectedConditions.urlContains("checkout/cart"));
        return new CartPage(driver, waiter);
    }
}