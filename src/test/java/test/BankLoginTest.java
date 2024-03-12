package test;

import com.codeborne.selenide.Configuration;
import data.DataHelper;
import data.SQLHelper;
import org.junit.jupiter.api.*;
import org.openqa.selenium.chrome.ChromeOptions;
import page.LoginPage;

import java.util.HashMap;
import java.util.Map;

import static com.codeborne.selenide.Selenide.open;
import static data.SQLHelper.cleanAuthCode;
import static data.SQLHelper.cleanDatabase;

public class BankLoginTest {
    LoginPage loginPage;

    @AfterEach
    void tearDown() {
        cleanAuthCode();
    }

    @AfterAll
    static void tearDownAll() {
        cleanDatabase();
    }

    @BeforeEach

    void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        Map<String, Object> prefs = new HashMap<String, Object>();
        prefs.put("credentials_enable_service", false);
        prefs.put("password_manager_enabled", false);
        options.setExperimentalOption("prefs", prefs);
        Configuration.browserCapabilities = options;
        loginPage = open("http://localhost:9999", LoginPage.class);
    }

    @Test
    @DisplayName("ShouldSuccessfullyLogInToTheDashboardUsingAnExistingLoginAndPasswordFromTheSutTestData")
    void shouldSuccessfulLogin() {
        var authInfo = DataHelper.getAuthInfoWithTestData();
        var verificationPage = loginPage.validLogin(authInfo);
        verificationPage.verifyVerificationPageVisibility();
        var verificationCode = SQLHelper.getVerificationCode();
        verificationPage.validVerify(verificationCode.getCode());
    }
    @Test
    @DisplayName("ShouldGetErrorNotificationIfLoginWithExistInBaseAndActiveUserAndRandomVerificationCode")
    void shouldGetErrorNotificationWithRandomVerificationCode() {
        var authInfo = DataHelper.getAuthInfoWithTestData();
        var verificationPage = loginPage.validLogin(authInfo);
        verificationPage.verifyVerificationPageVisibility();
        var verificationCode = DataHelper.generateRandomVerificationCode();
        verificationPage.verify(verificationCode.getCode());
        verificationPage.verifyErrorNotification("Ошибка! \nНеверно указан код! Попробуйте ещё раз.");
    }

    @Test
    @DisplayName("ShouldGetErrorNotificationIfUserIsNotExistInBase")
    void shouldGetErrorNotificationIfUserIsNotExistInBase() {
            var authInfo = DataHelper.generateRandomUser();
            loginPage.validLogin(authInfo);
            loginPage.verifyErrorNotification("Ошибка! \nНеверно указан логин или пароль");
    }
}
