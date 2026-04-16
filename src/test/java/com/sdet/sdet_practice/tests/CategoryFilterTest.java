package com.sdet.sdet_practice.tests;

import com.sdet.sdet_practice.pages.CategoryPage;
import com.sdet.sdet_practice.pages.HomePage;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Epic("Automation Test Store")
@Feature("Фильтрация товаров в категориях")
public class CategoryFilterTest extends BaseTest{

    private CategoryPage categoryPage;

    @BeforeMethod
    @Step("Открываем категорию Shoes")
    public void setUpPage() {
        HomePage homePage = new HomePage(driver, waiter);
        categoryPage = homePage.openShoesCategory();

        Assert.assertTrue(
                categoryPage.getPageTitle().equalsIgnoreCase("SHOES"),
                "Должна открыться страница категории Shoes"
        );

        Assert.assertTrue(
                categoryPage.getProductsCount() >= 4,
                "В категории должно быть не менее 4 товаров"
        );
    }

    @Test(description = "Сортировка товаров по имени A-Z")
    @Story("Пользователь сортирует товары по имени по возрастанию")
    @Severity(SeverityLevel.CRITICAL)
    public void shouldSortProductsByNameAscending(){

        categoryPage.sortByVisibleText("Name A - Z");

        List<String> actualNames = categoryPage.getProductNames();
        List<String> expectedNames = new ArrayList<>(actualNames);

        expectedNames.sort(String.CASE_INSENSITIVE_ORDER);
        Assert.assertEquals(
                actualNames,
                expectedNames,
                "Товары должны быть отсортированы по имени по возрастанию"
        );
    }

    @Test(description = "Сортировка товаров по имени Z-A")
    @Story("Пользователь сортирует товары по имени по убыванию")
    @Severity(SeverityLevel.CRITICAL)
    public void shouldSortProductsByNameDescending(){

        categoryPage.sortByVisibleText("Name Z - A");

        List<String> actualNames = categoryPage.getProductNames();
        List<String> expectedNames = new ArrayList<>(actualNames);

        expectedNames.sort(String.CASE_INSENSITIVE_ORDER.reversed());
        Assert.assertEquals(
                actualNames,
                expectedNames,
                "Товары должны быть отсортированы по имени по убыванию"
        );
    }

    @Test(description = "Сортировка товаров по цене по возрастанию")
    @Story("Пользователь сортирует товары по цене Low -> High")
    @Severity(SeverityLevel.CRITICAL)
    public void shouldSortProductsByPriceAscending(){

        categoryPage.sortByVisibleText("Price Low > High");

        List<BigDecimal> actualPrices = categoryPage.getProductPrices();
        List<BigDecimal> expectedPrices  = new ArrayList<>(actualPrices);

        expectedPrices.sort(Comparator.naturalOrder());
        Assert.assertEquals(
                actualPrices,
                expectedPrices,
                "Товары должны быть отсортированы по цене по возрастанию"
        );
    }

    @Test(description = "Сортировка товаров по цене по убыванию")
    @Story("Пользователь сортирует товары по цене High -> Low")
    @Severity(SeverityLevel.CRITICAL)
    public void shouldSortProductsByPriceDescending(){

        categoryPage.sortByVisibleText("Price High > Low");

        List<BigDecimal> actualPrices = categoryPage.getProductPrices();
        List<BigDecimal> expectedPrices  = new ArrayList<>(actualPrices);

        expectedPrices.sort(Comparator.reverseOrder());
        Assert.assertEquals(
                actualPrices,
                expectedPrices,
                "Товары должны быть отсортированы по цене по убыванию"
        );
    }

}
