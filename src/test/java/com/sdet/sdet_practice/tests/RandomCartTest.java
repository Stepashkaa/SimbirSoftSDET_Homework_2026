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
public class RandomCartTest extends BaseTest{

    @Test(description = "Добавление 5 случайных товаров с главной страницы, удаление четных товаров из корзины и проверка итоговой суммы")
    @Story("Пользователь добавляет случайные товары в корзину, удаляет четные позиции и проверяет subtotal")
    @Severity(SeverityLevel.CRITICAL)
    public void addFiveRandomProductsRemoveAndValidateSubtotal(){
        HomePage homePage = new HomePage(driver, waiter);

        homePage.open();

        int totalProductsOnHomePage = homePage.getHomePageProductsCount();
        Assert.assertTrue(totalProductsOnHomePage >= 5, "На главной странице должно быть не меньше 5 товаров");

        List<Integer> shuffledIndices = new ArrayList<>();
        for (int i = 0; i < totalProductsOnHomePage; i++) {
            shuffledIndices.add(i);
        }
        Collections.shuffle(shuffledIndices);

        List<CartItem> addedItems = new ArrayList<>();

        for(Integer productIndex : shuffledIndices){
            if(addedItems.size() == 5){
                break;
            }

            homePage.open();
            ProductPage productPage = homePage.openHomePageProductByIndex(productIndex);

            if(!productPage.canBeAddedProductToCart()){
                continue;
            }

            String productName = productPage.getProductName();
            BigDecimal productPrice = productPage.getProductPrice();
            int quantity = getRandomQuantity();

            productPage.setQuantity(quantity).addToBasket();
            addedItems.add(new CartItem(productName, productPrice, quantity));
        }

        Assert.assertEquals(addedItems.size(), 5, "Должно быть добавлено 5 доступных товаров");

        CartPage cartPage = new ProductPage(driver, waiter).openCart();

        Assert.assertTrue(cartPage.getPageTitle().contains("SHOPPING CART"), "Должна открыться страница корзины");

        Assert.assertEquals(cartPage.getItemsCount(), 5, "В корзине должно быть 5 товаров");

        cartPage.removeItemByIndex(3);
        addedItems.remove(3);

        cartPage.removeItemByIndex(1);
        addedItems.remove(1);

        Assert.assertEquals(
                cartPage.getItemsCount(),
                3,
                "После удаления четных товаров в корзине должно остаться 3 товара"
        );

        BigDecimal subTotal = BigDecimal.ZERO;
        for(CartItem cartItem : addedItems){
            subTotal = subTotal.add(cartItem.getUnitPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
        }

        BigDecimal actualSubTotal = cartPage.getSubTotal();

        Assert.assertEquals(
                actualSubTotal,
                subTotal,
                "Sub-Total должен совпадать с ожидаемой суммой после удаления четных товаров"
        );
    }

    public int getRandomQuantity(){
        return new Random().nextInt(3) + 1;
    }
}
