package ru.netology.test;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import ru.netology.data.DataGenerator;
import ru.netology.data.SQLGenerator;
import ru.netology.page.PaymentPage;

import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.netology.data.SQLGenerator.cleanDatabase;

public class CreditTest {
    PaymentPage paymentPage;

    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @BeforeEach
    @DisplayName("1.22 Переход к форме покупки тура путем покупки в кредит")
    void setup() {
        paymentPage = open("http://localhost:8080", PaymentPage.class);
        paymentPage.loanPage();
    }

    @AfterEach
    void tearDownAllSQL() {
        cleanDatabase();
    }

    @Test
    @DisplayName("1.23 Заполнение формы валидными данными")
    void transitionToPurchaseСredit() {
        paymentPage.cardNumber(DataGenerator.getNumberApprovedCard());
        paymentPage.monthNumber(DataGenerator.generateValidDate(0, 0, "MM"));
        paymentPage.yearNumber(DataGenerator.generateValidDate(0, 0, "yy"));
        paymentPage.fullName(DataGenerator.generateRandomSurname());
        paymentPage.CVC(DataGenerator.generateCVC_CVV());
        paymentPage.buttonContinue();
        paymentPage.successfully("Операция одобрена банком.");
        var actualStatusPayment = SQLGenerator.getStatusPaymentCredit();
        var actualStatusOrder_entityPayment = SQLGenerator.getStatusOrder_entityPayment();
        String expectedStatus = DataGenerator.APPROVED().getStatus();
        String expectedId = null;
        assertAll(() -> assertEquals(expectedStatus, actualStatusPayment),
                () -> assertEquals(expectedId, actualStatusOrder_entityPayment));
    }

    @Test
    @DisplayName("1.24 Отправка формы с введением номера карты со статусом DECLINED")
    void cardsWithTheDECLINEDStatusСredit() {
        paymentPage.cardNumber(DataGenerator.getNumberDeclinedCard());
        paymentPage.monthNumber(DataGenerator.generateValidDate(0, 0, "MM"));
        paymentPage.yearNumber(DataGenerator.generateValidDate(0, 0, "yy"));
        paymentPage.fullName(DataGenerator.generateRandomSurname());
        paymentPage.CVC(DataGenerator.generateCVC_CVV());
        paymentPage.buttonContinue();
        paymentPage.bankRefused("Ошибка! Банк отказал в проведении операции.");
        var actualStatusPayment = SQLGenerator.getStatusPaymentCredit();
        var actualStatusOrder_entityPayment = SQLGenerator.getStatusOrder_entityPayment();
        String expectedStatus = DataGenerator.DECLINED().getStatus();
        String expectedId = null;
        assertAll(() -> assertEquals(expectedStatus, actualStatusPayment),
                () -> assertEquals(expectedId, actualStatusOrder_entityPayment));
    }

    @Test
    @DisplayName("1.25 Отправка формы с введением рандомного 16-значного номера карты, цифрами")
    void randomCardNumberСredit() {
        paymentPage.cardNumber(DataGenerator.generateRandomCard());
        paymentPage.monthNumber(DataGenerator.generateValidDate(0, 0, "MM"));
        paymentPage.yearNumber(DataGenerator.generateValidDate(0, 0, "yy"));
        paymentPage.fullName(DataGenerator.generateRandomSurname());
        paymentPage.CVC(DataGenerator.generateCVC_CVV());
        paymentPage.buttonContinue();
        paymentPage.bankRefused("Ошибка! Банк отказал в проведении операции.");
        var actualStatusPayment = SQLGenerator.getStatusPaymentCredit();
        var actualStatusOrder_entityPayment = SQLGenerator.getStatusOrder_entityPayment();
        String expectedStatus = null;
        String expectedId = null;
        assertAll(() -> assertEquals(expectedStatus, actualStatusPayment),
                () -> assertEquals(expectedId, actualStatusOrder_entityPayment));
    }

    @Test
    @DisplayName("1.26 Отправка формы с введением 15-значного номера карты, цифрами")
    void shortCardNumberСredit() {
        paymentPage.cardNumber(DataGenerator.generateNotValidCard());
        paymentPage.monthNumber(DataGenerator.generateValidDate(0, 0, "MM"));
        paymentPage.yearNumber(DataGenerator.generateValidDate(0, 0, "yy"));
        paymentPage.fullName(DataGenerator.generateRandomSurname());
        paymentPage.CVC(DataGenerator.generateCVC_CVV());
        paymentPage.buttonContinue();
        paymentPage.invalidFormat.shouldBe(visible);
        var actualStatusPayment = SQLGenerator.getStatusPaymentCredit();
        var actualStatusOrder_entityPayment = SQLGenerator.getStatusOrder_entityPayment();
        String expectedStatus = null;
        String expectedId = null;
        assertAll(() -> assertEquals(expectedStatus, actualStatusPayment),
                () -> assertEquals(expectedId, actualStatusOrder_entityPayment));
    }

    @Test
    @DisplayName("1.27 Отправка формы, оставив пустым поле \"Номер карты\"")
    void leaveItBlankCardNumberСredit() {
        paymentPage.cardNumber("");
        paymentPage.monthNumber(DataGenerator.generateValidDate(0, 0, "MM"));
        paymentPage.yearNumber(DataGenerator.generateValidDate(0, 0, "yy"));
        paymentPage.fullName(DataGenerator.generateRandomSurname());
        paymentPage.CVC(DataGenerator.generateCVC_CVV());
        paymentPage.buttonContinue();
        paymentPage.fieldIsRequired.shouldBe(visible);
        var actualStatusPayment = SQLGenerator.getStatusPaymentCredit();
        var actualStatusOrder_entityPayment = SQLGenerator.getStatusOrder_entityPayment();
        String expectedStatus = null;
        String expectedId = null;
        assertAll(() -> assertEquals(expectedStatus, actualStatusPayment),
                () -> assertEquals(expectedId, actualStatusOrder_entityPayment));
    }

    @Test
    @DisplayName("1.28 Отправка формы, заполнив поле \"Месяц\" НЕ валидным значением 00")
    void enteringMonth00Сredit() {
        paymentPage.cardNumber(DataGenerator.getNumberApprovedCard());
        paymentPage.monthNumber("00");
        paymentPage.yearNumber(DataGenerator.generateValidDate(0, 1, "yy"));
        paymentPage.fullName(DataGenerator.generateRandomSurname());
        paymentPage.CVC(DataGenerator.generateCVC_CVV());
        paymentPage.buttonContinue();
        paymentPage.incorrectExpirationDate.shouldBe(visible);
        var actualStatusPayment = SQLGenerator.getStatusPaymentCredit();
        var actualStatusOrder_entityPayment = SQLGenerator.getStatusOrder_entityPayment();
        String expectedStatus = null;
        String expectedId = null;
        assertAll(() -> assertEquals(expectedStatus, actualStatusPayment),
                () -> assertEquals(expectedId, actualStatusOrder_entityPayment));
    }

    @Test
    @DisplayName("1.29 Отправка формы, заполнив поле \"Месяц\" НЕ валидным значением 13")
    void enteringMonth13Сredit() {
        paymentPage.cardNumber(DataGenerator.getNumberApprovedCard());
        paymentPage.monthNumber("13");
        paymentPage.yearNumber(DataGenerator.generateValidDate(0, 0, "yy"));
        paymentPage.fullName(DataGenerator.generateRandomSurname());
        paymentPage.CVC(DataGenerator.generateCVC_CVV());
        paymentPage.buttonContinue();
        paymentPage.incorrectExpirationDate.shouldBe(visible);
        var actualStatusPayment = SQLGenerator.getStatusPaymentCredit();
        var actualStatusOrder_entityPayment = SQLGenerator.getStatusOrder_entityPayment();
        String expectedStatus = null;
        String expectedId = null;
        assertAll(() -> assertEquals(expectedStatus, actualStatusPayment),
                () -> assertEquals(expectedId, actualStatusOrder_entityPayment));
    }

    @Test
    @DisplayName("1.30 Отправка формы, оставив поле \"Месяц\" не заполненным")
    void leaveMonthEmptyСredit() {
        paymentPage.cardNumber(DataGenerator.getNumberApprovedCard());
        paymentPage.monthNumber("");
        paymentPage.yearNumber(DataGenerator.generateValidDate(0, 0, "yy"));
        paymentPage.fullName(DataGenerator.generateRandomSurname());
        paymentPage.CVC(DataGenerator.generateCVC_CVV());
        paymentPage.buttonContinue();
        paymentPage.fieldIsRequired.shouldBe(visible);
        var actualStatusPayment = SQLGenerator.getStatusPaymentCredit();
        var actualStatusOrder_entityPayment = SQLGenerator.getStatusOrder_entityPayment();
        String expectedStatus = null;
        String expectedId = null;
        assertAll(() -> assertEquals(expectedStatus, actualStatusPayment),
                () -> assertEquals(expectedId, actualStatusOrder_entityPayment));
    }

    @Test
    @DisplayName("1.31 Отправка формы, введя \"Месяц\" на один меньше текущего и введя в поле \"Год\" текущий")
    void expiredСardСredit() {
        paymentPage.cardNumber(DataGenerator.getNumberApprovedCard());
        paymentPage.monthNumber(DataGenerator.generateValidDate(11, 0, "MM"));
        paymentPage.yearNumber(DataGenerator.generateValidDate(0, 0, "yy"));
        paymentPage.fullName(DataGenerator.generateRandomSurname());
        paymentPage.CVC(DataGenerator.generateCVC_CVV());
        paymentPage.buttonContinue();
        paymentPage.theCardIsExpired.shouldBe(visible);
        var actualStatusPayment = SQLGenerator.getStatusPaymentCredit();
        var actualStatusOrder_entityPayment = SQLGenerator.getStatusOrder_entityPayment();
        String expectedStatus = null;
        String expectedId = null;
        assertAll(() -> assertEquals(expectedStatus, actualStatusPayment),
                () -> assertEquals(expectedId, actualStatusOrder_entityPayment));
    }

    @Test
    @DisplayName("1.32 Отправка формы, прибавив к текущему году 6 лет")
    void yearOverLimitСredit() {
        paymentPage.cardNumber(DataGenerator.getNumberApprovedCard());
        paymentPage.monthNumber(DataGenerator.generateValidDate(0, 0, "MM"));
        paymentPage.yearNumber(DataGenerator.generateValidDate(0, 6, "yy"));
        paymentPage.fullName(DataGenerator.generateRandomSurname());
        paymentPage.CVC(DataGenerator.generateCVC_CVV());
        paymentPage.buttonContinue();
        paymentPage.incorrectExpirationDate.shouldBe(visible);
        var actualStatusPayment = SQLGenerator.getStatusPaymentCredit();
        var actualStatusOrder_entityPayment = SQLGenerator.getStatusOrder_entityPayment();
        String expectedStatus = null;
        String expectedId = null;
        assertAll(() -> assertEquals(expectedStatus, actualStatusPayment),
                () -> assertEquals(expectedId, actualStatusOrder_entityPayment));
    }

    @Test
    @DisplayName("1.33 Отправка формы, введя в поле \"Год\" на один меньше текущего")
    void lastYearСredit() {
        paymentPage.cardNumber(DataGenerator.getNumberApprovedCard());
        paymentPage.monthNumber(DataGenerator.generateValidDate(0, 0, "MM"));
        paymentPage.yearNumber(DataGenerator.generateLastYear(1, "yy"));
        paymentPage.fullName(DataGenerator.generateRandomSurname());
        paymentPage.CVC(DataGenerator.generateCVC_CVV());
        paymentPage.buttonContinue();
        paymentPage.theCardIsExpired.shouldBe(visible);
        var actualStatusPayment = SQLGenerator.getStatusPaymentCredit();
        var actualStatusOrder_entityPayment = SQLGenerator.getStatusOrder_entityPayment();
        String expectedStatus = null;
        String expectedId = null;
        assertAll(() -> assertEquals(expectedStatus, actualStatusPayment),
                () -> assertEquals(expectedId, actualStatusOrder_entityPayment));
    }

    @Test
    @DisplayName("1.34 Отправка формы, оставив поле \"Год\" пустым")
    void yearFieldEmptyСredit() {
        paymentPage.cardNumber(DataGenerator.getNumberApprovedCard());
        paymentPage.monthNumber(DataGenerator.generateValidDate(0, 0, "MM"));
        paymentPage.yearNumber("");
        paymentPage.fullName(DataGenerator.generateRandomSurname());
        paymentPage.CVC(DataGenerator.generateCVC_CVV());
        paymentPage.buttonContinue();
        paymentPage.fieldIsRequired.shouldBe(visible);
        var actualStatusPayment = SQLGenerator.getStatusPaymentCredit();
        var actualStatusOrder_entityPayment = SQLGenerator.getStatusOrder_entityPayment();
        String expectedStatus = null;
        String expectedId = null;
        assertAll(() -> assertEquals(expectedStatus, actualStatusPayment),
                () -> assertEquals(expectedId, actualStatusOrder_entityPayment));
    }

    @Test
    @DisplayName("1.35 Отправка формы, оставив поле \"Владелец\" пустым")
    void ownerFieldEmptyСredit() {
        paymentPage.cardNumber(DataGenerator.getNumberApprovedCard());
        paymentPage.monthNumber(DataGenerator.generateValidDate(0, 0, "MM"));
        paymentPage.yearNumber(DataGenerator.generateValidDate(0, 0, "yy"));
        paymentPage.fullName("");
        paymentPage.CVC(DataGenerator.generateCVC_CVV());
        paymentPage.buttonContinue();
        paymentPage.fieldIsRequired.shouldBe(visible);
        var actualStatusPayment = SQLGenerator.getStatusPaymentCredit();
        var actualStatusOrder_entityPayment = SQLGenerator.getStatusOrder_entityPayment();
        String expectedStatus = null;
        String expectedId = null;
        assertAll(() -> assertEquals(expectedStatus, actualStatusPayment),
                () -> assertEquals(expectedId, actualStatusOrder_entityPayment));
    }

    @Test
    @DisplayName("1.36 Отправка формы, оставив поле \"CVC/CVV\" пустым")
    void emptyCVCСredit() {
        paymentPage.cardNumber(DataGenerator.getNumberApprovedCard());
        paymentPage.monthNumber(DataGenerator.generateValidDate(0, 0, "MM"));
        paymentPage.yearNumber(DataGenerator.generateValidDate(0, 0, "yy"));
        paymentPage.fullName(DataGenerator.generateRandomSurname());
        paymentPage.CVC("");
        paymentPage.buttonContinue();
        paymentPage.fieldIsRequired.shouldBe(visible);
        paymentPage.invalidFormat.shouldBe(empty);
        var actualStatusPayment = SQLGenerator.getStatusPaymentCredit();
        var actualStatusOrder_entityPayment = SQLGenerator.getStatusOrder_entityPayment();
        String expectedStatus = null;
        String expectedId = null;
        assertAll(() -> assertEquals(expectedStatus, actualStatusPayment),
                () -> assertEquals(expectedId, actualStatusOrder_entityPayment));
    }

    @Test
    @DisplayName("1.37 Отправка формы, введя в поле \"CVC/CVV\" две цифры")
    void shortCVCСredit() {
        paymentPage.cardNumber(DataGenerator.getNumberApprovedCard());
        paymentPage.monthNumber(DataGenerator.generateValidDate(0, 0, "MM"));
        paymentPage.yearNumber(DataGenerator.generateValidDate(0, 0, "yy"));
        paymentPage.fullName(DataGenerator.generateRandomSurname());
        paymentPage.CVC(DataGenerator.generateShortCVC_CVV());
        paymentPage.buttonContinue();
        paymentPage.invalidFormat.shouldBe(visible);
        var actualStatusPayment = SQLGenerator.getStatusPaymentCredit();
        var actualStatusOrder_entityPayment = SQLGenerator.getStatusOrder_entityPayment();
        String expectedStatus = null;
        String expectedId = null;
        assertAll(() -> assertEquals(expectedStatus, actualStatusPayment),
                () -> assertEquals(expectedId, actualStatusOrder_entityPayment));
    }

    @Test
    @DisplayName("1.38 Отправка формы, введя в поле \"Владелец\" НЕ валидное значение, кириллицей")
    void ownerCyrillicСredit() {
        paymentPage.cardNumber(DataGenerator.getNumberApprovedCard());
        paymentPage.monthNumber(DataGenerator.generateValidDate(0, 0, "MM"));
        paymentPage.yearNumber(DataGenerator.generateValidDate(0, 0, "yy"));
        paymentPage.fullName(DataGenerator.generateRandomSurnameCyrillic());
        paymentPage.CVC(DataGenerator.generateCVC_CVV());
        paymentPage.buttonContinue();
        paymentPage.invalidFormat.shouldBe(visible);
        var actualStatusPayment = SQLGenerator.getStatusPaymentCredit();
        var actualStatusOrder_entityPayment = SQLGenerator.getStatusOrder_entityPayment();
        String expectedStatus = null;
        String expectedId = null;
        assertAll(() -> assertEquals(expectedStatus, actualStatusPayment),
                () -> assertEquals(expectedId, actualStatusOrder_entityPayment));
    }

    @Test
    @DisplayName("1.39 Отправка формы, введя в поле \"Владелец\" НЕ валидное значение, цифрами")
    void ownerNumbersСredit() {
        paymentPage.cardNumber(DataGenerator.getNumberApprovedCard());
        paymentPage.monthNumber(DataGenerator.generateValidDate(0, 0, "MM"));
        paymentPage.yearNumber(DataGenerator.generateValidDate(0, 0, "yy"));
        paymentPage.fullName(DataGenerator.generateNotValidCard());
        paymentPage.CVC(DataGenerator.generateCVC_CVV());
        paymentPage.buttonContinue();
        paymentPage.invalidFormat.shouldBe(visible);
        var actualStatusPayment = SQLGenerator.getStatusPaymentCredit();
        var actualStatusOrder_entityPayment = SQLGenerator.getStatusOrder_entityPayment();
        String expectedStatus = null;
        String expectedId = null;
        assertAll(() -> assertEquals(expectedStatus, actualStatusPayment),
                () -> assertEquals(expectedId, actualStatusOrder_entityPayment));
    }

    @Test
    @DisplayName("1.40 Отправка формы, введя в поле \"Владелец\" НЕ валидное значение, спецсимволами")
    void ownerSpecialCharactersСredit() {
        paymentPage.cardNumber(DataGenerator.getNumberApprovedCard());
        paymentPage.monthNumber(DataGenerator.generateValidDate(0, 0, "MM"));
        paymentPage.yearNumber(DataGenerator.generateValidDate(0, 0, "yy"));
        paymentPage.fullName(DataGenerator.randomSymbol());
        paymentPage.CVC(DataGenerator.generateCVC_CVV());
        paymentPage.buttonContinue();
        paymentPage.invalidFormat.shouldBe(visible);
        var actualStatusPayment = SQLGenerator.getStatusPaymentCredit();
        var actualStatusOrder_entityPayment = SQLGenerator.getStatusOrder_entityPayment();
        String expectedStatus = null;
        String expectedId = null;
        assertAll(() -> assertEquals(expectedStatus, actualStatusPayment),
                () -> assertEquals(expectedId, actualStatusOrder_entityPayment));
    }

    @Test
    @DisplayName("1.41 Отправка формы, введя в поле \"Владелец\" НЕ валидное значение, латиницей, только Имя")
    void justTheNameСredit() {
        paymentPage.cardNumber(DataGenerator.getNumberApprovedCard());
        paymentPage.monthNumber(DataGenerator.generateValidDate(0, 0, "MM"));
        paymentPage.yearNumber(DataGenerator.generateValidDate(0, 0, "yy"));
        paymentPage.fullName(DataGenerator.justTheName());
        paymentPage.CVC(DataGenerator.generateCVC_CVV());
        paymentPage.buttonContinue();
        paymentPage.invalidFormat.shouldBe(visible);
        var actualStatusPayment = SQLGenerator.getStatusPaymentCredit();
        var actualStatusOrder_entityPayment = SQLGenerator.getStatusOrder_entityPayment();
        String expectedStatus = null;
        String expectedId = null;
        assertAll(() -> assertEquals(expectedStatus, actualStatusPayment),
                () -> assertEquals(expectedId, actualStatusOrder_entityPayment));
    }

    @Test
    @DisplayName("1.42 Отправка формы, введя в поле \"Владелец\" НЕ валидное значение, пробелами")
    void nameWithSpacesСredit() {
        paymentPage.cardNumber(DataGenerator.getNumberApprovedCard());
        paymentPage.monthNumber(DataGenerator.generateValidDate(0, 0, "MM"));
        paymentPage.yearNumber(DataGenerator.generateValidDate(0, 0, "yy"));
        paymentPage.fullName("       ");
        paymentPage.CVC(DataGenerator.generateCVC_CVV());
        paymentPage.buttonContinue();
        paymentPage.fieldIsRequired.shouldBe(visible);
        var actualStatusPayment = SQLGenerator.getStatusPaymentCredit();
        var actualStatusOrder_entityPayment = SQLGenerator.getStatusOrder_entityPayment();
        String expectedStatus = null;
        String expectedId = null;
        assertAll(() -> assertEquals(expectedStatus, actualStatusPayment),
                () -> assertEquals(expectedId, actualStatusOrder_entityPayment));
    }
}
