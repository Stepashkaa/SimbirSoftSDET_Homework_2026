package com.sdet.sdet_practice.tests;

import com.sdet.sdet_practice.models.CartItem;
import com.sdet.sdet_practice.pages.CartPage;
import com.sdet.sdet_practice.pages.HomePage;
import com.sdet.sdet_practice.pages.ProductPage;
import com.sdet.sdet_practice.pages.SearchResultsPage;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.Random;

@Epic("Automation Test Store")
@Feature("Поиск и добавление в корзину товара Shirt")
public class SearchAndCartTest extends BaseTest {

    @Test(description = "Поиск shirt, добавление 2 и 3 товара в корзину")
    @Story("Пользователь ищет товары, добавляет их в корзину и проверяет итоговую сумму")
    @Severity(SeverityLevel.CRITICAL)
    public void shouldOpenSecondSearchResultAndPrepareForAddingToCart() {
        HomePage homePage = new HomePage(driver, waiter);
        SearchResultsPage resultsPage = homePage
                .open()
                .searchFor("shirt");

        Assert.assertTrue(resultsPage.hasSorting(),
                "На странице должна быть доступна сортировка");

        Assert.assertTrue(resultsPage.getResultsCount() >= 3,
                "На странице должно быть более 3 товаров");

        resultsPage.sortByVisibleText("Name A - Z");

        int quantitySecondRandomProduct = getRandomQuantity();
        ProductPage secondProductPage  = resultsPage.openFirstAvailableProduct(1);

        String secondName = secondProductPage.getProductName();
        BigDecimal secondPrice = secondProductPage.getProductPrice();

        Assert.assertTrue(secondProductPage.canBeAddedProductToCart(), "Второй товар может быть добавлен в корзину");

        secondProductPage
                .selectRequiredOptionsIfPresent()
                .setQuantity(quantitySecondRandomProduct)
                .addToBasket();
        CartItem secondItem = new CartItem(secondName, secondPrice, quantitySecondRandomProduct);

        resultsPage = homePage
                .open()
                .searchFor("shirt");

        resultsPage.sortByVisibleText("Name A - Z");

        int quantityThirdRandomProduct = getRandomQuantity();
        ProductPage thirdProductPage = resultsPage.openFirstAvailableProduct(2);

        String thirdName = thirdProductPage.getProductName();
        BigDecimal thirdPrice = thirdProductPage.getProductPrice();

        Assert.assertTrue(thirdProductPage.canBeAddedProductToCart(),
                "Третий товар может быть добавлен в корзину");

        thirdProductPage
                .selectRequiredOptionsIfPresent()
                .setQuantity(quantityThirdRandomProduct)
                .addToBasket();
        CartItem thirdItem = new CartItem(thirdName, thirdPrice, quantityThirdRandomProduct);

        CartPage cartPage = thirdProductPage.openCart();

        Assert.assertTrue(cartPage.getPageTitle().contains("SHOPPING CART"),
                "Должна открыться страница корзины");
        Assert.assertEquals(cartPage.getItemsCount(), 2,
                "В корзине должно быть 2 товара");

        CartItem cheapestItem = cartPage.findCheapProduct();
        int updatedQuantity = cheapestItem.getQuantity()*2;

        cartPage.updateQuantityByProductName(cheapestItem.getName(), updatedQuantity);

        if(secondItem.getName().equals(cheapestItem.getName())) {
            secondItem.setQuantity(updatedQuantity);
        } else if(thirdItem.getName().equals(cheapestItem.getName())) {
            thirdItem.setQuantity(updatedQuantity);
        }
        BigDecimal expectedSubTotal = secondItem.getUnitPrice()
                .multiply(BigDecimal.valueOf(secondItem.getQuantity()))
                .add(
                        thirdItem.getUnitPrice()
                                .multiply(BigDecimal.valueOf(thirdItem.getQuantity()))
                );

        BigDecimal actualSubTotal = cartPage.getSubTotal();
        Assert.assertEquals(
                actualSubTotal,
                expectedSubTotal,
                "Sub-Total корзины должен совпадать с ожидаемой суммой"
        );
    }
}
