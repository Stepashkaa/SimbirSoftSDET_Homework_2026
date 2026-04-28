package com.sdet.sdet_practice.pages;

import com.sdet.sdet_practice.helpers.WaitHelper;
import com.sdet.sdet_practice.models.CartItem;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.sdet.sdet_practice.utilits.MoneyUtils.parseMoney;

public class CartPage extends BasePage {

    private final By cartTitle = By.cssSelector("span.maintext");
    private final By cartRows = By.cssSelector(".cart-info.product-list table tbody tr:not(:first-child)");
    private final By updateButton = By.id("cart_update");

    private final By itemName = By.cssSelector("td.align_left a");
    private final By quantityInput = By.cssSelector("input[id^='cart_quantity']");

    private final By totalsRows = By.cssSelector("#totals_table tr");

    public CartPage(WebDriver driver, WaitHelper waitHelper) {
        super(driver, waitHelper);
    }

    @Step("Получить заголовок страницы корзины")
    public String getPageTitle() {
        return waitHelper.visible(cartTitle).getText().trim();
    }

    @Step("Получить количество товаров в корзине")
    public int getItemsCount() {
        return waitHelper.visibleAll(cartRows).size();
    }

    @Step("Получить список товаров из корзины")
    public List<CartItem> getCartItems() {
        List<WebElement> elements = waitHelper.visibleAll(cartRows);

        List<CartItem> cartItems = new ArrayList<>();

        for(WebElement element : elements) {
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
    public CartItem findCheapProduct() {
        return getCartItems().stream()
                .min(Comparator.comparing(CartItem::getUnitPrice))
                .orElseThrow(() -> new IllegalStateException("Корзина пуста"));
    }

    @Step("Изменить количество товара")
    public CartPage updateQuantityByProductName(String productName, int newQuantity) {
        waitHelper.visibleAll(cartRows);

        BigDecimal oldSubTotal = getSubTotal();

        List<WebElement> rows = driver.findElements(cartRows);

        for (WebElement row : rows) {
            String currentName = row.findElement(itemName).getText().trim();

            if (currentName.equals(productName)) {
                WebElement quantity = row.findElement(quantityInput);
                clearAndType(quantity, String.valueOf(newQuantity));
                break;
            }
        }

        waitHelper.clickable(updateButton).click();

        waitHelper.until(driver -> {
            try {
                for (WebElement refreshedRow : driver.findElements(cartRows)) {
                    String refreshedName = refreshedRow.findElement(itemName).getText().trim();

                    if (refreshedName.equals(productName)) {
                        WebElement refreshedQuantity = refreshedRow.findElement(quantityInput);
                        String actualValue = refreshedQuantity.getAttribute("value");
                        return String.valueOf(newQuantity).equals(actualValue);
                    }
                }
                return false;
            } catch (StaleElementReferenceException e) {
                return false;
            }
        });

        waitHelper.until(driver -> {
            try {
                return getSubTotal().compareTo(oldSubTotal) != 0;
            } catch (StaleElementReferenceException e) {
                return false;
            }
        });

        return this;
    }

    @Step("Получить Sub-Total корзины")
    public BigDecimal getSubTotal() {
        return getAmountFromTotalsRow("Sub-Total:");
    }

    private BigDecimal getAmountFromTotalsRow(String title) {
        return waitHelper.until(driver -> {
            try {
                List<WebElement> rows = driver.findElements(totalsRows);

                for (WebElement row : rows) {
                    List<WebElement> cells = row.findElements(By.tagName("td"));

                    if (cells.size() >= 2) {
                        String leftText = cells.get(0).getText().trim();

                        if (leftText.equals(title)) {
                            String rightText = cells.get(1).getText().trim();
                            return parseMoney(rightText);
                        }
                    }
                }
                return null;
            } catch (StaleElementReferenceException e) {
                return null;
            }
        });
    }

    @Step("Удалить товар из корзины по индексу")
    public CartPage removeItemByIndex(int index) {
        List<WebElement> rows = waitHelper.visibleAll(cartRows);

        if(index < 0 || index >= rows.size()) {
            throw new IllegalArgumentException("Некорректный индекс товара в корзине: " + index);
        }

        int oldSize = rows.size();
        WebElement rowToRemove = rows.get(index);
        WebElement removeButton = rowToRemove.findElement(By.cssSelector("td.align_center a.btn.btn-sm.btn-default"));

        waitHelper.clickable(removeButton).click();

        waitHelper.stale(rowToRemove);
        waitHelper.until(driver -> driver.findElements(cartRows).size() == oldSize - 1);

        return this;
    }
}
