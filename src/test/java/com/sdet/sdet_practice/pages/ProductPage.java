package com.sdet.sdet_practice.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.math.BigDecimal;

public class ProductPage extends BasePage{

    private final By productName = By.cssSelector("h1.productname .bgnone");
    private final By productPrice = By.cssSelector(".productfilneprice");

    private final By quantityField = By.cssSelector("#product_quantity");
    private final By totalPrice = By.cssSelector(".total-price");
    private final By outOfStockLabel = By.cssSelector(".productpagecart .nostock");
    private final By addToCartButton = By.cssSelector(".productpagecart a.cart");

    private final By cartLink = By.cssSelector("a[href*='checkout/cart']");

    public ProductPage(WebDriver driver, WebDriverWait waiter) {
        super(driver, waiter);
    }

    @Step("Получить название товара")
    public String getProductName(){
        return waiter.until(ExpectedConditions.visibilityOfElementLocated(productName)).getText().trim();
    }

    @Step("Получить цену товара")
    public BigDecimal getProductPrice(){
        waiter.until(ExpectedConditions.visibilityOfElementLocated(productPrice));
        WebElement element = driver.findElement(productPrice);

        String rawText = element.getText().trim();
        String normalized = rawText.replace("$", "").replace(",", "").trim();
        return new BigDecimal(normalized);
    }

    @Step("Проверить, что товар отсутствует на складе")
    public boolean isOutOfStock(){
        return !driver.findElements(outOfStockLabel).isEmpty();
    }

    @Step("Проверить, что товар можно добавить в корзину")
    public boolean canBeAddedProductToCart(){
        return !isOutOfStock() && !driver.findElements(addToCartButton).isEmpty();
    }

    @Step("Установить количество товара")
    public ProductPage setQuantity(int quantity){
        WebElement quant = waiter.until(ExpectedConditions.visibilityOfElementLocated(quantityField));
        quant.clear();
        quant.sendKeys(String.valueOf(quantity));
        return this;
    }

    @Step("Добавить товар в корзину")
    public ProductPage addToBasket(){
        waiter.until(ExpectedConditions.elementToBeClickable(addToCartButton)).click();
        return this;
    }

    @Step("Переходим в корзину")
    public CartPage openCart(){
        waiter.until(ExpectedConditions.elementToBeClickable(cartLink)).click();
        return new CartPage(driver, waiter);
    }
}
