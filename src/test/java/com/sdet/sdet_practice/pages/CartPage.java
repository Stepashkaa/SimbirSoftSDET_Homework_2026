package com.sdet.sdet_practice.pages;

import com.sdet.sdet_practice.models.CartItem;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CartPage extends BasePage{

    private final By cartTitle = By.cssSelector("span.maintext");
    private final By cartRows = By.cssSelector(".cart-info.product-list table tbody tr:not(:first-child)");
    private final By updateButton = By.id("cart_update");

    private final By itemName = By.cssSelector("td.align_left a");
    private final By quantityInput = By.cssSelector("input[id^='cart_quantity']");

    private final By totalsRows = By.cssSelector("#totals_table tr");

    public CartPage(WebDriver driver, WebDriverWait waiter) {
        super(driver, waiter);
    }

    @Step("Получить заголовок страницы корзины")
    public String getPageTitle(){
        return waiter.until(ExpectedConditions.visibilityOfElementLocated(cartTitle)).getText().trim();
    }

    @Step("Получить количество товаров в корзине")
    public int getItemsCount(){
        waiter.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(cartRows));
        return driver.findElements(cartRows).size();
    }

    @Step("Получить список товаров из корзины")
    public List<CartItem> getCartItems(){
        waiter.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(cartRows));
        List<WebElement> elements = driver.findElements(cartRows);

        List<CartItem> cartItems = new ArrayList<>();

        for(WebElement element : elements){
            String name = element.findElement(itemName).getText().trim();
            String unitPriceRaw = element.findElements(By.cssSelector("td.align_right")).get(0).getText().trim();

            BigDecimal unitPrice = parseMoney(unitPriceRaw);
            WebElement quantityElement = element.findElement(quantityInput);
            int quantity = Integer.parseInt(quantityElement.getAttribute("value").trim());

            cartItems.add(new CartItem(name, unitPrice, quantity));
        }
        return cartItems;
    }

    @Step("Найти самый дешевый товар в корзине")
    public CartItem findCheapProduct(){
        return getCartItems().stream()
                .min(Comparator.comparing(CartItem::getUnitPrice))
                .orElseThrow(() -> new IllegalStateException("Корзина пуста"));
    }

    @Step("Изменить количество товара")
    public CartPage updateQuantityByProductName(String productName, int newQuantity){
        waiter.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(cartRows));
        List<WebElement> rows = driver.findElements(cartRows);

        for(WebElement row : rows){
            String currentName = row.findElement(itemName).getText().trim();

            if(currentName.equals(productName)){
                WebElement quantity = row.findElement(quantityInput);

                quantity.clear();
                quantity.sendKeys(String.valueOf(newQuantity));
                return this;
            }
        }
        throw new IllegalArgumentException("Товар не найден в корзине: " + productName);
    }

    @Step("Нажать Update в корзине")
    public CartPage clickUpdate(){
        waiter.until(ExpectedConditions.elementToBeClickable(updateButton)).click();
        waiter.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(cartRows));
        return this;
    }

    @Step("Получить Sub-Total корзины")
    public BigDecimal getSubTotal(){
        return getAmountFromTotalsRow("Sub-Total:");
    }

    private BigDecimal getAmountFromTotalsRow(String title) {
        waiter.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(totalsRows));
        List<WebElement> rows = driver.findElements(totalsRows);

        for(WebElement row : rows){
            List<WebElement> cells = row.findElements(By.tagName("td"));
            if(cells.size() >= 2){
                String leftText = cells.get(0).getText().trim();
                String rightText = cells.get(1).getText().trim();

                if(leftText.equals(title)){
                    return parseMoney(rightText);
                }
            }
        }
        throw new IllegalStateException("Не найдена строка итогов: " + title);
    }

    public BigDecimal parseMoney(String raw){
        String normalized = raw.replace("$", "").replace(",", "").trim();
        return new BigDecimal(normalized);
    }

    @Step("Удалить товар из корзины по индексу")
    public CartPage removeItemByIndex(int index){
        waiter.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(cartRows));
        List<WebElement> rows = driver.findElements(cartRows);

        if(index < 0 || index >= rows.size()){
            throw new IllegalArgumentException("Некорректный индекс товара в корзине: " + index);
        }

        WebElement removeButton = rows.get(index).findElement(By.cssSelector("td.align_center a.btn.btn-sm.btn-default"));

        removeButton.click();

        waiter.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(cartRows));
        return this;
    }
}
