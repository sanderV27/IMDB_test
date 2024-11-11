package tests;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import io.github.bonigarcia.wdm.WebDriverManager;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import static com.codeborne.selenide.Selenide.*;

public class ImdbTest {

    private static final String IMDB_LINK_XPATH = "//a[contains(@href, 'imdb.com')]";
    private static final String BORN_TODAY_SELECTOR = ".lister-item.mode-detail .lister-item-header a";

    @BeforeClass
    public void setUp() {
        // Use WebDriverManager to manage ChromeDriver version
        WebDriverManager.chromedriver().setup();
        System.setProperty("selenide.browser", "chrome");
        System.setProperty("chromeoptions.args", "--remote-allow-origins=*");
    }

    @Test(description = "Automated test to fetch 5 celebrities born today from IMDb website")
    @Description("Searches IMDb on Google, navigates to the 'Born Today' section, and prints first 5 celebrities")
    public void testImdbBornTodayList() {
        openGoogleHomePage();
        searchForIMDbOnGoogle();
        clickOnIMDbLink();
        navigateToBornTodaySection();
        printFirstFiveCelebritiesBornToday();
    }

    @Step("Open Google.com")
    private void openGoogleHomePage() {
        Selenide.open("https://www.google.com");
        $(By.name("q")).shouldBe(Condition.visible);
    }

    @Step("Search for IMDb on Google")
    private void searchForIMDbOnGoogle() {
        $(By.name("q")).setValue("imdb").pressEnter();
    }

    @Step("Click on the IMDb website link")
    private void clickOnIMDbLink() {
        $x(IMDB_LINK_XPATH).shouldBe(Condition.visible).click();
        $("body").shouldHave(Condition.text("IMDb"));
    }

    @Step("Navigate to 'Born Today' section in the Menu")
    private void navigateToBornTodaySection() {
        $(By.id("imdbHeader-navDrawerOpen--desktop")).click();
        $(By.xpath("//a[contains(text(), 'Born Today')]")).click();
        $("h1").shouldHave(Condition.text("Born Today"));
    }

    @Step("Print the names of the first 5 celebrities born today")
    private void printFirstFiveCelebritiesBornToday() {
        List<String> celebrities = $$(BORN_TODAY_SELECTOR).texts();
        for (int i = 0; i < Math.min(5, celebrities.size()); i++) {
            System.out.println((i + 1) + ". " + celebrities.get(i));
        }
        assert !celebrities.isEmpty() : "No celebrities found!";
    }
}