package tests;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import io.qameta.allure.Allure;
import io.qameta.allure.Attachment;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static com.codeborne.selenide.Selenide.*;

public class ImdbTest {
    private List<String> celebrityNamesList = new ArrayList<>();

    @BeforeClass
    public void setUp() {
        System.setProperty("selenide.browser", "chrome");
        System.setProperty("chromeoptions.args", "--remote-allow-origins=*");
        Selenide.open("https://www.google.com");
    }

    @Test(description = "Step 1: Search for IMDb on Google")
    @Description("Search for IMDb on Google")
    public void testSearchForImdb() {
        searchForImdb();
    }

    @Step("Search for IMDb on Google")
    private void searchForImdb() {
        $(By.name("q")).setValue("imdb").pressEnter();
        Assert.assertTrue($(By.id("search")).exists(), "Google search results not found");
        attachScreenshot("Search Result");
    }

    @Test(dependsOnMethods = {"testSearchForImdb"}, description = "Step 2: Click on the IMDb website link")
    @Description("Click on the IMDb website link")
    public void testClickOnCorrectResult() {
        clickOnCorrectResult();
    }

    @Step("Click on the IMDb website link")
    private void clickOnCorrectResult() {
        $x("//a[contains(@href, 'imdb.com')]").shouldBe(Condition.visible).click();
        $("body").shouldHave(Condition.text("IMDb"));
        attachScreenshot("IMDb Home Page");
    }

    @Test(dependsOnMethods = {"testClickOnCorrectResult"}, description = "Step 3: Navigate to Menu")
    @Description("Navigate to Menu on IMDb")
    public void testNavigateMenu() {
        navigateMenu();
    }

    @Step("Navigate to Menu")
    private void navigateMenu() {
        $(By.id("imdbHeader-navDrawerOpen")).shouldBe(Condition.visible, Duration.ofSeconds(10)).click();
        attachScreenshot("IMDb Menu");
    }

    @Test(dependsOnMethods = {"testNavigateMenu"}, description = "Step 4: Navigate to 'Born Today' section")
    @Description("Navigate to 'Born Today' section")
    public void testNavigateToBornToday() {
        navigateToBornToday();
    }

    @Step("Navigate to 'Born Today' section")
    private void navigateToBornToday() {
        $("a[href='/feature/bornondate/?ref_=nv_cel_brn']")
                .shouldBe(Condition.visible, Duration.ofSeconds(5))
                .click();
        Selenide.sleep(2000);
        attachScreenshot("Born Today Section");
    }

    @Test(dependsOnMethods = {"testNavigateToBornToday"}, description = "Step 5: Print names of the first 5 celebrities born today")
    @Description("Print the names of the first 5 celebrities born today")
    public void testPrintTop5CelebritiesBornToday() {
        printTop5CelebritiesBornToday();
        attachCelebrityList();
    }

    @Step("Print names of the first 5 celebrities born today")
    private void printTop5CelebritiesBornToday() {
        ElementsCollection celebrityNames = $$x("//h3[@class='ipc-title__text']");
        attachScreenshot("Celebrities Born Today");
        Assert.assertTrue(celebrityNames.size() > 0, "No celebrities found in 'Born Today' section");

        for (int i = 0; i < Math.min(5, celebrityNames.size()); i++) {
            String celebrityName = celebrityNames.get(i).getText();
            celebrityNamesList.add(celebrityName);
            System.out.println((i + 1) + ". " + celebrityName);
            Allure.step("Celebrity " + (i + 1) + ": " + celebrityName);
        }
    }

    @Attachment(value = "{0}", type = "image/png")
    private byte[] attachScreenshot(String name) {
        return screenshot(OutputType.BYTES);
    }

    @Attachment(value = "List of Celebrities", type = "text/plain")
    private String attachCelebrityList() {
        StringBuilder names = new StringBuilder();
        for (String name : celebrityNamesList) {
            names.append(name).append("\n");
        }
        return names.toString();
    }
}