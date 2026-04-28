package com.sdet.sdet_practice.tests;

import com.sdet.sdet_practice.models.CartItem;
import com.sdet.sdet_practice.pages.CartPage;
import com.sdet.sdet_practice.pages.HomePage;
import com.sdet.sdet_practice.pages.ProductPage;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Epic("Automation Test Store")
@Feature("Проверка корзины")
public class RandomCartTest extends BaseTest {

    @Test(description = "Добавление 5 случайных товаров с главной страницы, удаление четных товаров из корзины и проверка итоговой суммы")
    @Story("Пользователь добавляет случайные товары в корзину, удаляет четные позиции и проверяет subtotal")
    @Severity(SeverityLevel.CRITICAL)
    public void shouldAddFiveRandomProductsRemoveAndValidateSubtotal() {
        HomePage homePage = new HomePage(getDriver(), getWaitHelper());
        homePage.open();

        CartPage cartPage = addFiveRandomProductsToCart(homePage);


        Assert.assertTrue(
                cartPage.getPageTitle().contains("SHOPPING CART"),
                "Должна открыться страница корзины"
        );

        assertCartHasItems(cartPage, 5);

        removeEvenItems(cartPage);

        assertCartHasItems(cartPage, 3);

        assertSubtotalIsCorrect(cartPage);
    }

    private CartPage addFiveRandomProductsToCart(HomePage homePage) {

        int totalProducts = homePage.getHomePageProductsCount();

        Assert.assertTrue(
                totalProducts >= 5,
                "На главной странице должно быть не меньше 5 товаров"
        );

        List<Integer> shuffledIndices = generateShuffledIndices(totalProducts);

        CartPage cartPage = null;

        for (Integer productIndex : shuffledIndices) {
            if (cartPage != null && cartPage.getItemsCount() == 5) {
                break;
            }

            homePage.open();
            ProductPage productPage = homePage.openHomePageProductByIndex(productIndex);

            if (!productPage.canBeAddedProductToCart()) {
                continue;
            }

            cartPage = productPage
                    .selectRequiredOptionsIfPresent()
                    .setQuantity(getRandomQuantity())
                    .addToBasket();

        }
        return cartPage;
    }

    private List<Integer> generateShuffledIndices(int size){
        List<Integer> indices = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            indices.add(i);
        }

        Collections.shuffle(indices);
        return indices;
    }

    private void assertCartHasItems(CartPage cartPage, int expected) {
        Assert.assertEquals(
                cartPage.getItemsCount(),
                expected,
                "В корзине должно быть " + expected + " товаров"
        );
    }

    private void removeEvenItems(CartPage cartPage) {
        List<Integer> indexes = getEvenIndexes(cartPage.getItemsCount());

        Collections.reverse(indexes);

        for (Integer index : indexes) {
            cartPage.removeItemByIndex(index);
        }
    }

    private List<Integer> getEvenIndexes(int size) {
        List<Integer> indexes = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            int position = i + 1;
            if (position % 2 == 0) {
                indexes.add(i);
            }
        }

        return indexes;
    }

    private void assertSubtotalIsCorrect(CartPage cartPage) {
        List<CartItem> items = cartPage.getCartItems();

        BigDecimal expected = items.stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal actual = cartPage.getSubTotal();

        Assert.assertEquals(
                actual,
                expected,
                "Sub-Total должен совпадать"
        );
    }
}
