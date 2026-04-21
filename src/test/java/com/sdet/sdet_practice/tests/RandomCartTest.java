package com.sdet.sdet_practice.tests;

import com.sdet.sdet_practice.models.CartItem;
import com.sdet.sdet_practice.pages.CartPage;
import com.sdet.sdet_practice.pages.HomePage;
import com.sdet.sdet_practice.pages.ProductPage;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Epic("Automation Test Store")
@Feature("Проверка корзины")
public class RandomCartTest extends BaseTest {

    @Test(description = "Добавление 5 случайных товаров с главной страницы, удаление четных товаров из корзины и проверка итоговой суммы")
    @Story("Пользователь добавляет случайные товары в корзину, удаляет четные позиции и проверяет subtotal")
    @Severity(SeverityLevel.CRITICAL)
    public void shouldAddFiveRandomProductsRemoveAndValidateSubtotal() {
        HomePage homePage = new HomePage(getDriver(), getWaiter());

        homePage.open();

        int totalProductsOnHomePage = homePage.getHomePageProductsCount();
        Assert.assertTrue(totalProductsOnHomePage >= 5, "На главной странице должно быть не меньше 5 товаров");

        List<Integer> shuffledIndices = new ArrayList<>();
        for (int i = 0; i < totalProductsOnHomePage; i++) {
            shuffledIndices.add(i);
        }
        Collections.shuffle(shuffledIndices);

        int actualCartCount = 0;

        for(Integer productIndex : shuffledIndices) {
            if(actualCartCount == 5) {
                break;
            }

            homePage.open();
            ProductPage productPage = homePage.openHomePageProductByIndex(productIndex);

            if(!productPage.canBeAddedProductToCart()) {
                continue;
            }

            productPage
                    .selectRequiredOptionsIfPresent()
                    .setQuantity(getRandomQuantity())
                    .addToBasket();

            CartPage cartPageAfterAdd = productPage.openCart();
            actualCartCount = cartPageAfterAdd.getItemsCount();
        }

        CartPage cartPage = new ProductPage(getDriver(), getWaiter()).openCart();

        Assert.assertTrue(cartPage.getPageTitle().contains("SHOPPING CART"), "Должна открыться страница корзины");

        Assert.assertEquals(cartPage.getItemsCount(), 5, "В корзине должно быть 5 товаров");

        List<Integer> evenIndexesToRemove = new ArrayList<>();
        for (int i = 0; i < cartPage.getItemsCount(); i++) {
            int position = i + 1;
            if (position % 2 == 0) {
                evenIndexesToRemove.add(i);
            }
        }

        Collections.reverse(evenIndexesToRemove);

        for (Integer index : evenIndexesToRemove) {
            cartPage.removeItemByIndex(index);
        }

        Assert.assertEquals(
                cartPage.getItemsCount(),
                3,
                "После удаления четных товаров в корзине должно остаться 3 товара"
        );

        List<CartItem> actualItemsInCart = cartPage.getCartItems();

        BigDecimal expectedSubTotal = BigDecimal.ZERO;
        for (CartItem cartItem : actualItemsInCart) {
            expectedSubTotal = expectedSubTotal.add(
                    cartItem.getUnitPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()))
            );
        }

        BigDecimal actualSubTotal = cartPage.getSubTotal();

        Assert.assertEquals(
                actualSubTotal,
                expectedSubTotal,
                "Sub-Total должен совпадать с ожидаемой суммой после удаления четных товаров"
        );
    }
}
