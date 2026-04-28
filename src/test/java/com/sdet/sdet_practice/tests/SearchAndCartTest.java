package com.sdet.sdet_practice.tests;

import com.sdet.sdet_practice.models.CartItem;
import com.sdet.sdet_practice.pages.CartPage;
import com.sdet.sdet_practice.pages.HomePage;
import com.sdet.sdet_practice.pages.ProductPage;
import com.sdet.sdet_practice.pages.SearchResultsPage;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.List;

@Epic("Automation Test Store")
@Feature("Поиск и добавление в корзину товара Shirt")
public class SearchAndCartTest extends BaseTest {

    private CartPage cartPage;

    @Test(description = "Поиск shirt, добавление 2 и 3 товара в корзину")
    @Story("Пользователь ищет товары, добавляет их в корзину и проверяет итоговую сумму")
    @Severity(SeverityLevel.CRITICAL)
    public void shouldOpenSecondSearchResultAndPrepareForAddingToCart() {
        HomePage homePage = new HomePage(getDriver(), getWaitHelper());

        SearchResultsPage resultsPage = homePage
                .open()
                .searchFor("shirt");

        Assert.assertTrue(resultsPage.hasSorting(),
                "На странице должна быть доступна сортировка");

        Assert.assertTrue(resultsPage.getResultsCount() >= 3,
                "На странице должно быть более 3 товаров");

        CartItem secondItem = addProductFromSearchResults(homePage, 1);
        CartItem thirdItem = addProductFromSearchResults(homePage, 2);

        Assert.assertTrue(cartPage.getPageTitle().contains("SHOPPING CART"),
                "Должна открыться страница корзины");

        Assert.assertEquals(cartPage.getItemsCount(), 2,
                "В корзине должно быть 2 товара");

        CartItem cheapestItem = cartPage.findCheapProduct();
        int updatedQuantity = cheapestItem.getQuantity() * 2;

        cartPage.updateQuantityByProductName(cheapestItem.getName(), updatedQuantity);
        cartPage.waitForQuantityUpdated(cheapestItem.getName(), updatedQuantity);

        updateItemQuantityByName(
                List.of(secondItem, thirdItem),
                cheapestItem.getName(),
                updatedQuantity
        );

        BigDecimal expectedSubTotal = calculateExpectedSubtotal(List.of(secondItem, thirdItem));
        BigDecimal actualSubTotal = cartPage.getSubTotal();

        Assert.assertEquals(
                actualSubTotal,
                expectedSubTotal,
                "Sub-Total корзины должен совпадать с ожидаемой суммой"
        );
    }

    private CartItem addProductFromSearchResults(HomePage homePage, int startIndex) {
        SearchResultsPage resultsPage = homePage
                .open()
                .searchFor("shirt");

        resultsPage.sortByVisibleText("Name A - Z");

        ProductPage productPage = resultsPage.openFirstAvailableProduct(startIndex);

        Assert.assertTrue(
                productPage.canBeAddedProductToCart(),
                "Товар должен быть доступен для добавления в корзину"
        );

        String name = productPage.getProductName();
        BigDecimal price = productPage.getProductPrice();
        int quantity = getRandomQuantity();

        cartPage = productPage
                .selectRequiredOptionsIfPresent()
                .setQuantity(quantity)
                .addToBasket();

        return new CartItem(name, price, quantity);
    }

    private void updateItemQuantityByName(List<CartItem> items, String productName, int newQuantity) {
        items.stream()
                .filter(item -> item.getName().equals(productName))
                .findFirst()
                .ifPresent(item -> item.setQuantity(newQuantity));
    }

    private BigDecimal calculateExpectedSubtotal(List<CartItem> items) {
        return items.stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}