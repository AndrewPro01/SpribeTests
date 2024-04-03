import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import java.time.Duration;
import static org.openqa.selenium.By.*;

public class AviatorAutoPlayTests {

    private WebDriver driver;

    @BeforeAll
    public static void setupClass() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    public void setupTest() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        driver.get("https://spribe.co/welcome");

        // Go to games tab and check if the url is correct
        driver.findElement(cssSelector("a[href='/games']")).click();
        wait.until(ExpectedConditions.urlToBe("https://spribe.co/games"));
        String gamesUrl = driver.getCurrentUrl();
        Assert.assertEquals(gamesUrl, "https://spribe.co/games");

        // Go to Aviator page and check if the url is correct
        driver.findElement(cssSelector("img[alt='Aviator']")).click();
        String gamesAviatorUrl = driver.getCurrentUrl();
        Assert.assertEquals(gamesAviatorUrl, "https://spribe.co/games/aviator");

        // Click on PLay Demo button and check if responsible gaming modal is displayed
        WebElement playDemoAviatorButton = driver.findElement(By.className("btn-demo"));
        Actions actions = new Actions(driver);
        actions.moveToElement(playDemoAviatorButton).click().build().perform();
        WebElement responsibleGamingModal = driver.findElement(By.className("modal-content"));
        Assert.assertTrue(responsibleGamingModal.isDisplayed(), "Modal content is not displayed.");

        // Click on I am over 18 button, go the new tab with Aviator demo game and check if the url is correct
        driver.findElement(By.xpath("//button[@type='button' and @class='btn btn-md btn-primary btn-age mt-3 mt-lg-0']")).click();
        String originalTab = driver.getWindowHandle();
        for (String newTab : driver.getWindowHandles()) {
            if (!newTab.equals(originalTab)) {
                driver.switchTo().window(newTab);
                String demoGameAviatorUrl = driver.getCurrentUrl();
                Assert.assertTrue(demoGameAviatorUrl.contains("https://aviator-demo.spribegaming.com"),
                        "New window URL does not contain the specified substring.");

                break;
            }
        }
    }

    @AfterEach
    public void afterEach() throws InterruptedException {
        Thread.sleep(3000);
        driver.quit();
    }

    @Test
    public void AutoPlayRedAlertsTest() {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        // Close second play modal
        driver.findElement(By.className("sec-hand-btn")).click();

        //Click on Auto Play button and check if auto play modal is displayed
        driver.findElement(By.xpath("//button[text()=' Auto ']")).click();
        driver.findElement(By.className("auto-play-btn")).click();
        WebElement autoPlayModal = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("modal-content")));
        Assert.assertTrue(autoPlayModal.isDisplayed(), "Modal dialog is not displayed.");

        // Click Start Play button and check if Set Number of rounds alert is displayed
        driver.findElement(By.className("start-btn")).click();
        WebElement setNumberOfRoundsAlert = driver.findElement(By.xpath("//div[contains(text(), 'Please, set number of rounds')]"));
        if (setNumberOfRoundsAlert.isDisplayed()) {
            System.out.println("Set number of rounds alert was displayed successfully");
        } else {
            System.out.println("Set number of rounds alert was not displayed.");
        }

        // Close Set Number of rounds alert, set number of rounds to 10, click Start Play button and check if Set Stop Point alert is displayed
        setNumberOfRoundsAlert.findElement(By.className("close")).click();
        driver.findElement(By.xpath("//button[text()=' 10 ']")).click();
        driver.findElement(By.className("start-btn")).click();
        WebElement setStopPointAlert = driver.findElement(By.xpath("//div[contains(text(), 'Please, specify decrease or exceed stop point')]"));
        if (setStopPointAlert.isDisplayed()) {
            System.out.println("Set stop point alert was displayed successfully");
        } else {
            System.out.println("Set stop point alert was not displayed.");
        }
    }

    @Test
    public void AutoCashOutInputFieldTest() {

        // Close second play modal
        driver.findElement(By.className("sec-hand-btn")).click();

        //Click on Auto Play button
        driver.findElement(By.xpath("//button[text()=' Auto ']")).click();

        // Toggle auto cash out switch
        WebElement cashoutBlock = driver.findElement(className("cashout-block"));
        cashoutBlock.findElement(By.className("input-switch")).click();

        WebElement autoCashOutInputField = cashoutBlock.findElement(By.className("font-weight-bold"));

        // First test case: to leave auto cash out input field empty
        autoCashOutInputField.sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
        driver.findElement(By.className("fun-mode")).click();
        String actualValue1 = autoCashOutInputField.getAttribute("value");
        if (actualValue1.equals("1.01")) {
            System.out.println("Value was automatically changed to 1.01 as expected.");
        } else {
            System.out.println("Value was not automatically changed to 1.01.");
        }

        // Second test case: to set auto cash out input field to 1.00
        autoCashOutInputField.sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
        autoCashOutInputField.sendKeys("1.00");
        driver.findElement(By.className("fun-mode")).click();
        String actualValue2 = autoCashOutInputField.getAttribute("value");
        if (actualValue2.equals("1.01")) {
            System.out.println("Value was automatically changed to 1.01 as expected.");
        } else {
            System.out.println("Value was not automatically changed to 1.01.");
        }

        // Third test case: to set auto cash out input field to 100.00
        autoCashOutInputField.sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
        autoCashOutInputField.sendKeys("100.01");
        driver.findElement(By.className("fun-mode")).click();
        String actualValue3 = autoCashOutInputField.getAttribute("value");
        if (actualValue3.equals("100.00")) {
            System.out.println("Value was automatically changed to 100.00 as expected.");
        } else {
            System.out.println("Value was not automatically changed to 100.00");
        }
    }
}
