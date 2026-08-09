package com.sdet.sdet_practice.tests;

import com.sdet.sdet_practice.pages.CategoryPage;
import com.sdet.sdet_practice.pages.HomePage;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Epic("Automation Test Store")
@Feature("Фильтрация товаров в категориях")
public class CategoryFilterTest extends BaseTest {

    private CategoryPage categoryPage;
    private String categoryTitle;

    @BeforeMethod
    public void setUpPage() {
        HomePage homePage = new HomePage(getDriver(), getWaitHelper());
        categoryPage = homePage.openFirstCategoryWithAtLeastFourProducts();

        categoryTitle = categoryPage.getPageTitle();

        Assert.assertFalse(
                categoryTitle.isBlank(),
                "Должен открыться заголовок категории"
        );

        Assert.assertTrue(
                categoryPage.hasSorting(),
                "На странице категории должна быть кнопка для сортировки"
        );

        Assert.assertTrue(
                categoryPage.getProductsCount() >= 4,
                "В категории должно быть не менее 4 товаров"
        );
    }

    @Test(dataProvider = "sortingData", description = "Проверка сортировки товаров")
    @Story("Пользователь проверяет сортировку товаров")
    @Severity(SeverityLevel.CRITICAL)
    public void shouldSortProducts(String sortOption, boolean ascending) {

        categoryPage.sortByVisibleText(sortOption);

        if (sortOption.contains("Name")) {

            List<String> actual = categoryPage.getProductNames();
            List<String> expected = new ArrayList<>(actual);

            expected.sort(
                    ascending
                            ? String.CASE_INSENSITIVE_ORDER
                            : String.CASE_INSENSITIVE_ORDER.reversed()
            );

            Assert.assertEquals(
                    actual,
                    expected,
                    "Товары в категории '" + categoryTitle + "' должны быть отсортированы по имени"
            );

        } else {

            List<BigDecimal> actual = categoryPage.getProductPrices();
            List<BigDecimal> expected = new ArrayList<>(actual);

            expected.sort(
                    ascending
                            ? Comparator.naturalOrder()
                            : Comparator.reverseOrder()
            );

            Assert.assertEquals(
                    actual,
                    expected,
                    "Товары в категории '" + categoryTitle + "' должны быть отсортированы по цене"
            );
        }
    }

    @DataProvider(name = "sortingData")
    public Object[][] sortingData() {
        return new Object[][]{
                {"Name A - Z", true},
                {"Name Z - A", false},
                {"Price Low > High", true},
                {"Price High > Low", false}
        };
    }
}
