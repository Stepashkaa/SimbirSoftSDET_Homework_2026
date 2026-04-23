package com.sdet.sdet_practice.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.math.BigDecimal;
import java.util.List;

import static com.sdet.sdet_practice.utilits.MoneyUtils.parseMoney;

public class ProductPage extends BasePage {

    private final By productName = By.cssSelector("h1.productname .bgnone");
    private final By productPrice = By.cssSelector(".productfilneprice");

    private final By productForm = By.cssSelector("form#product");
    private final By quantityField = By.cssSelector("#product_quantity");
    private final By outOfStockLabel = By.cssSelector(".productpagecart .nostock");
    private final By addToCartButton = By.cssSelector(".productpagecart a.cart");

    private final By cartLink = By.cssSelector("a[href*='checkout/cart']");

    private final By formGroups = By.cssSelector("form#product .form-group");
    private final By requiredMark = By.cssSelector(".required");

    public ProductPage(WebDriver driver, WebDriverWait waitHelper) {
        super(driver, waitHelper);
    }

    @Step("Получить название товара")
    public String getProductName() {
        return waitHelper.visible(productName).getText().trim();
    }

    @Step("Получить цену товара")
    public BigDecimal getProductPrice() {
        return parseMoney(waitHelper.visible(productPrice).getText().trim());
    }

    @Step("Проверить, что товар отсутствует на складе")
    public boolean isOutOfStock() {
        return !driver.findElements(outOfStockLabel).isEmpty();
    }

    @Step("Проверить, что товар можно добавить в корзину")
    public boolean canBeAddedProductToCart() {
        return !isOutOfStock() && !driver.findElements(addToCartButton).isEmpty();
    }

    @Step("Выбрать обязательные параметры товара")
    public ProductPage selectRequiredOptionsIfPresent() {
        waitHelper.visible(productForm);

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

    private void selectFirstAvailableOption(WebElement selectElement) {
        Select select = new Select(selectElement);

        WebElement availableOption = select.getOptions().stream()
                .filter(option -> option.getAttribute("disabled") == null)
                .filter(option -> {
                    String value = option.getAttribute("value");
                    return value != null && !value.trim().isBlank();
                })
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Не найден option в select"));

        select.selectByValue(availableOption.getAttribute("value"));
    }

    private void selectFirstAvailableRadio(List<WebElement> radios) {
        radios.stream()
                .filter(radio -> radio.getAttribute("disabled") == null)
                .findFirst()
                .ifPresentOrElse(
                        radio -> {
                            if (!radio.isSelected()) {
                                waitHelper.clickable(radio).click();
                            }
                        },
                        () -> {
                            throw new IllegalStateException("Не найден доступный radio option среди обязательных параметров");
                        }
                );
    }

    @Step("Установить количество товара")
    public ProductPage setQuantity(int quantity) {
        WebElement quant = waitHelper.visible(quantityField);
        clearAndType(quant, String.valueOf(quantity));
        return this;
    }

    @Step("Добавить товар в корзину")
    public ProductPage addToBasket() {
        waitHelper.clickable(addToCartButton).click();

        waitHelper.until(driver -> {
            try {
                return driver.getCurrentUrl().contains("checkout/cart")
                        || !driver.findElements(cartLink).isEmpty();
            } catch (StaleElementReferenceException e) {
                return false;
            }
        });

        return this;
    }

    @Step("Переходим в корзину")
    public CartPage openCart() {
        if (driver.getCurrentUrl().contains("checkout/cart")) {
            return new CartPage(driver, waiter);
        }

        waitHelper.until(driver -> {
            try {
                return driver.getCurrentUrl().contains("checkout/cart")
                        || !driver.findElements(cartLink).isEmpty();
            } catch (StaleElementReferenceException e) {
                return false;
            }
        });

        if (!driver.getCurrentUrl().contains("checkout/cart")) {
            WebElement cartElement = waitHelper.visible(cartLink);
            waitHelper.clickable(cartElement).click();
        }

        waitHelper.until(driver -> driver.getCurrentUrl().contains("checkout/cart"));
        return new CartPage(driver, waiter);
    }
}