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

        addFiveRandomProductsToCart(homePage);

        CartPage cartPage = openCart();

        Assert.assertTrue(
                cartPage.getPageTitle().contains("SHOPPING CART"),
                "Должна открыться страница корзины"
        );

        assertCartHasItems(cartPage, 5);

        removeEvenItems(cartPage);

        assertCartHasItems(cartPage, 3);

        assertSubtotalIsCorrect(cartPage);
    }

    private void addFiveRandomProductsToCart(HomePage homePage) {
        int totalProducts = homePage.getHomePageProductsCount();
        Assert.assertTrue(
                totalProducts >= 5,
                "На главной странице должно быть не меньше 5 товаров"
        );

        List<Integer> shuffledIndices = generateShuffledIndices(totalProducts);
        int actualCartCount = 0;

        for (Integer productIndex : shuffledIndices) {
            if (actualCartCount == 5) {
                break;
            }

            homePage.open();
            ProductPage productPage = homePage.openHomePageProductByIndex(productIndex);

            if (!productPage.canBeAddedProductToCart()) {
                continue;
            }

            productPage
                    .selectRequiredOptionsIfPresent()
                    .setQuantity(getRandomQuantity())
                    .addToBasket();

            actualCartCount = productPage.openCart().getItemsCount();
        }

        Assert.assertEquals(
                actualCartCount,
                5,
                "Должно быть фактически добавлено 5 товаров в корзину"
        );
    }

    private List<Integer> generateShuffledIndices(int size){
        List<Integer> indices = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            indices.add(i);
        }

        Collections.shuffle(indices);
        return indices;
    }

    private CartPage openCart() {
        return new ProductPage(getDriver(), getWaiter()).openCart();
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
