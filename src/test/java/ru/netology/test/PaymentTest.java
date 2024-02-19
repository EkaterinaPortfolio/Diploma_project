package ru.netology.test;


import com.codeborne.selenide.logevents.SelenideLogger;
import org.junit.jupiter.api.*;
import ru.netology.data.DataGenerator;
import ru.netology.data.SQLGenerator;
import ru.netology.page.PaymentPage;
import io.qameta.allure.selenide.AllureSelenide;


import java.time.Duration;

import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.netology.data.SQLGenerator.cleanDatabase;

public class PaymentTest {
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
    @DisplayName("1.1 Переход к форме покупки тура путем оплаты дебетовой картой")
    void setup() {
        paymentPage = open("http://localhost:8080", PaymentPage.class);
        paymentPage.purchasePage();
    }

    @AfterEach
    void tearDownAllSQL() {
        cleanDatabase();
    }

    @Test
    @DisplayName("1.2 Заполнение формы валидными данными")
    void transitionToPurchase() {
        paymentPage.cardNumber(DataGenerator.getNumberApprovedCard());
        paymentPage.monthNumber(DataGenerator.generateValidDate(0, 0, "MM"));
        paymentPage.yearNumber(DataGenerator.generateValidDate(0, 0, "yy"));
        paymentPage.fullName(DataGenerator.generateRandomSurname());
        paymentPage.CVC(DataGenerator.generateCVC_CVV());
        paymentPage.buttonContinue();
        paymentPage.successfully("Операция одобрена банком.");
        var actualStatusPayment = SQLGenerator.getStatusPayment();
        var actualStatusOrder_entityPayment = SQLGenerator.getStatusOrder_entityCredit();
        String expectedStatus = DataGenerator.APPROVED().getStatus();
        String expectedId = null;
        assertAll(() -> assertEquals(expectedStatus, actualStatusPayment),
                () -> assertEquals(expectedId, actualStatusOrder_entityPayment));
    }

    @Test
    @DisplayName("1.3 Отправка формы с введением номера карты со статусом DECLINED")
    void cardsWithTheDECLINEDStatus() {
        paymentPage.cardNumber(DataGenerator.getNumberDeclinedCard());
        paymentPage.monthNumber(DataGenerator.generateValidDate(0, 0, "MM"));
        paymentPage.yearNumber(DataGenerator.generateValidDate(0, 0, "yy"));
        paymentPage.fullName(DataGenerator.generateRandomSurname());
        paymentPage.CVC(DataGenerator.generateCVC_CVV());
        paymentPage.buttonContinue();
        paymentPage.bankRefused("Ошибка! Банк отказал в проведении операции.");
        var actualStatusPayment = SQLGenerator.getStatusPayment();
        var actualStatusOrder_entityPayment = SQLGenerator.getStatusOrder_entityCredit();
        String expectedStatus = DataGenerator.DECLINED().getStatus();
        String expectedId = null;
        assertAll(() -> assertEquals(expectedStatus, actualStatusPayment),
                () -> assertEquals(expectedId, actualStatusOrder_entityPayment));
    }

    @Test
    @DisplayName("1.4 Отправка формы с введением рандомного 16-значного номера карты, цифрами")
    void randomCardNumber() {
        paymentPage.cardNumber(DataGenerator.generateRandomCard());
        paymentPage.monthNumber(DataGenerator.generateValidDate(0, 0, "MM"));
        paymentPage.yearNumber(DataGenerator.generateValidDate(0, 0, "yy"));
        paymentPage.fullName(DataGenerator.generateRandomSurname());
        paymentPage.CVC(DataGenerator.generateCVC_CVV());
        paymentPage.buttonContinue();
        paymentPage.bankRefused("Ошибка! Банк отказал в проведении операции.");
        var actualStatusPayment = SQLGenerator.getStatusPayment();
        var actualStatusOrder_entityPayment = SQLGenerator.getStatusOrder_entityCredit();
        String expectedStatus = null;
        String expectedId = null;
        assertAll(() -> assertEquals(expectedStatus, actualStatusPayment),
                () -> assertEquals(expectedId, actualStatusOrder_entityPayment));
    }

    @Test
    @DisplayName("1.5 Отправка формы с введением 15-значного номера карты, цифрами")
    void shortCardNumber() {
        paymentPage.cardNumber(DataGenerator.generateNotValidCard());
        paymentPage.monthNumber(DataGenerator.generateValidDate(0, 0, "MM"));
        paymentPage.yearNumber(DataGenerator.generateValidDate(0, 0, "yy"));
        paymentPage.fullName(DataGenerator.generateRandomSurname());
        paymentPage.CVC(DataGenerator.generateCVC_CVV());
        paymentPage.buttonContinue();
        paymentPage.invalidFormat.shouldBe(visible, Duration.ofSeconds(15));
        var actualStatusPayment = SQLGenerator.getStatusPayment();
        var actualStatusOrder_entityPayment = SQLGenerator.getStatusOrder_entityCredit();
        String expectedStatus = null;
        String expectedId = null;
        assertAll(() -> assertEquals(expectedStatus, actualStatusPayment),
                () -> assertEquals(expectedId, actualStatusOrder_entityPayment));
    }

    @Test
    @DisplayName("1.6 Отправка формы, оставив пустым поле \"Номер карты\"")
    void leaveItBlankCardNumber() {
        paymentPage.cardNumber("");
        paymentPage.monthNumber(DataGenerator.generateValidDate(0, 0, "MM"));
        paymentPage.yearNumber(DataGenerator.generateValidDate(0, 0, "yy"));
        paymentPage.fullName(DataGenerator.generateRandomSurname());
        paymentPage.CVC(DataGenerator.generateCVC_CVV());
        paymentPage.buttonContinue();
        paymentPage.fieldIsRequired.shouldBe(visible);
        var actualStatusPayment = SQLGenerator.getStatusPayment();
        var actualStatusOrder_entityPayment = SQLGenerator.getStatusOrder_entityCredit();
        String expectedStatus = null;
        String expectedId = null;
        assertAll(() -> assertEquals(expectedStatus, actualStatusPayment),
                () -> assertEquals(expectedId, actualStatusOrder_entityPayment));
    }

    @Test
    @DisplayName("1.7 Отправка формы, заполнив поле \"Месяц\" НЕ валидным значением 00")
    void enteringMonth00() {
        paymentPage.cardNumber(DataGenerator.getNumberApprovedCard());
        paymentPage.monthNumber("00");
        paymentPage.yearNumber(DataGenerator.generateValidDate(0, 1, "yy"));
        paymentPage.fullName(DataGenerator.generateRandomSurname());
        paymentPage.CVC(DataGenerator.generateCVC_CVV());
        paymentPage.buttonContinue();
        paymentPage.incorrectExpirationDate.shouldBe(visible);
        var actualStatusPayment = SQLGenerator.getStatusPayment();
        var actualStatusOrder_entityPayment = SQLGenerator.getStatusOrder_entityCredit();
        String expectedStatus = null;
        String expectedId = null;
        assertAll(() -> assertEquals(expectedStatus, actualStatusPayment),
                () -> assertEquals(expectedId, actualStatusOrder_entityPayment));
    }

    @Test
    @DisplayName("1.8 Отправка формы, заполнив поле \"Месяц\" НЕ валидным значением 13")
    void enteringMonth13() {
        paymentPage.cardNumber(DataGenerator.getNumberApprovedCard());
        paymentPage.monthNumber("13");
        paymentPage.yearNumber(DataGenerator.generateValidDate(0, 0, "yy"));
        paymentPage.fullName(DataGenerator.generateRandomSurname());
        paymentPage.CVC(DataGenerator.generateCVC_CVV());
        paymentPage.buttonContinue();
        paymentPage.incorrectExpirationDate.shouldBe(visible);
        var actualStatusPayment = SQLGenerator.getStatusPayment();
        var actualStatusOrder_entityPayment = SQLGenerator.getStatusOrder_entityCredit();
        String expectedStatus = null;
        String expectedId = null;
        assertAll(() -> assertEquals(expectedStatus, actualStatusPayment),
                () -> assertEquals(expectedId, actualStatusOrder_entityPayment));
    }

    @Test
    @DisplayName("1.9 Отправка формы, оставив поле \"Месяц\" не заполненным")
    void leaveMonthEmpty() {
        paymentPage.cardNumber(DataGenerator.getNumberApprovedCard());
        paymentPage.monthNumber("");
        paymentPage.yearNumber(DataGenerator.generateValidDate(0, 0, "yy"));
        paymentPage.fullName(DataGenerator.generateRandomSurname());
        paymentPage.CVC(DataGenerator.generateCVC_CVV());
        paymentPage.buttonContinue();
        paymentPage.fieldIsRequired.shouldBe(visible);
        var actualStatusPayment = SQLGenerator.getStatusPayment();
        var actualStatusOrder_entityPayment = SQLGenerator.getStatusOrder_entityCredit();
        String expectedStatus = null;
        String expectedId = null;
        assertAll(() -> assertEquals(expectedStatus, actualStatusPayment),
                () -> assertEquals(expectedId, actualStatusOrder_entityPayment));
    }

    @Test
    @DisplayName("1.10 Отправка формы, введя \"Месяц\" на один меньше текущего и введя в поле \"Год\" текущий")
    void expiredСard() {
        paymentPage.cardNumber(DataGenerator.getNumberApprovedCard());
        paymentPage.monthNumber(DataGenerator.generateValidDate(11, 0, "MM"));
        paymentPage.yearNumber(DataGenerator.generateValidDate(0, 0, "yy"));
        paymentPage.fullName(DataGenerator.generateRandomSurname());
        paymentPage.CVC(DataGenerator.generateCVC_CVV());
        paymentPage.buttonContinue();
        paymentPage.theCardIsExpired.shouldBe(visible);
        var actualStatusPayment = SQLGenerator.getStatusPayment();
        var actualStatusOrder_entityPayment = SQLGenerator.getStatusOrder_entityCredit();
        String expectedStatus = null;
        String expectedId = null;
        assertAll(() -> assertEquals(expectedStatus, actualStatusPayment),
                () -> assertEquals(expectedId, actualStatusOrder_entityPayment));
    }

    @Test
    @DisplayName("1.11 Отправка формы, прибавив к текущему году 6 лет")
    void yearOverLimit() {
        paymentPage.cardNumber(DataGenerator.getNumberApprovedCard());
        paymentPage.monthNumber(DataGenerator.generateValidDate(0, 0, "MM"));
        paymentPage.yearNumber(DataGenerator.generateValidDate(0, 6, "yy"));
        paymentPage.fullName(DataGenerator.generateRandomSurname());
        paymentPage.CVC(DataGenerator.generateCVC_CVV());
        paymentPage.buttonContinue();
        paymentPage.incorrectExpirationDate.shouldBe(visible);
        var actualStatusPayment = SQLGenerator.getStatusPayment();
        var actualStatusOrder_entityPayment = SQLGenerator.getStatusOrder_entityCredit();
        String expectedStatus = null;
        String expectedId = null;
        assertAll(() -> assertEquals(expectedStatus, actualStatusPayment),
                () -> assertEquals(expectedId, actualStatusOrder_entityPayment));
    }

    @Test
    @DisplayName("1.12 Отправка формы, введя в поле \"Год\" на один меньше текущего")
    void lastYear() {
        paymentPage.cardNumber(DataGenerator.getNumberApprovedCard());
        paymentPage.monthNumber(DataGenerator.generateValidDate(0, 0, "MM"));
        paymentPage.yearNumber(DataGenerator.generateLastYear(1, "yy"));
        paymentPage.fullName(DataGenerator.generateRandomSurname());
        paymentPage.CVC(DataGenerator.generateCVC_CVV());
        paymentPage.buttonContinue();
        paymentPage.theCardIsExpired.shouldBe(visible);
        var actualStatusPayment = SQLGenerator.getStatusPayment();
        var actualStatusOrder_entityPayment = SQLGenerator.getStatusOrder_entityCredit();
        String expectedStatus = null;
        String expectedId = null;
        assertAll(() -> assertEquals(expectedStatus, actualStatusPayment),
                () -> assertEquals(expectedId, actualStatusOrder_entityPayment));
    }

    @Test
    @DisplayName("1.13 Отправка формы, оставив поле \"Год\" пустым")
    void yearFieldEmpty() {
        paymentPage.cardNumber(DataGenerator.getNumberApprovedCard());
        paymentPage.monthNumber(DataGenerator.generateValidDate(0, 0, "MM"));
        paymentPage.yearNumber("");
        paymentPage.fullName(DataGenerator.generateRandomSurname());
        paymentPage.CVC(DataGenerator.generateCVC_CVV());
        paymentPage.buttonContinue();
        paymentPage.fieldIsRequired.shouldBe(visible);
        var actualStatusPayment = SQLGenerator.getStatusPayment();
        var actualStatusOrder_entityPayment = SQLGenerator.getStatusOrder_entityCredit();
        String expectedStatus = null;
        String expectedId = null;
        assertAll(() -> assertEquals(expectedStatus, actualStatusPayment),
                () -> assertEquals(expectedId, actualStatusOrder_entityPayment));
    }

    @Test
    @DisplayName("1.14 Отправка формы, оставив поле \"Владелец\" пустым")
    void ownerFieldEmpty() {
        paymentPage.cardNumber(DataGenerator.getNumberApprovedCard());
        paymentPage.monthNumber(DataGenerator.generateValidDate(0, 0, "MM"));
        paymentPage.yearNumber(DataGenerator.generateValidDate(0, 0, "yy"));
        paymentPage.fullName("");
        paymentPage.CVC(DataGenerator.generateCVC_CVV());
        paymentPage.buttonContinue();
        paymentPage.fieldIsRequired.shouldBe(visible);
        var actualStatusPayment = SQLGenerator.getStatusPayment();
        var actualStatusOrder_entityPayment = SQLGenerator.getStatusOrder_entityCredit();
        String expectedStatus = null;
        String expectedId = null;
        assertAll(() -> assertEquals(expectedStatus, actualStatusPayment),
                () -> assertEquals(expectedId, actualStatusOrder_entityPayment));
    }

    @Test
    @DisplayName("1.15 Отправка формы, оставив поле \"CVC/CVV\" пустым")
    void emptyCVC() {
        paymentPage.cardNumber(DataGenerator.getNumberApprovedCard());
        paymentPage.monthNumber(DataGenerator.generateValidDate(0, 0, "MM"));
        paymentPage.yearNumber(DataGenerator.generateValidDate(0, 0, "yy"));
        paymentPage.fullName(DataGenerator.generateRandomSurname());
        paymentPage.CVC("");
        paymentPage.buttonContinue();
        paymentPage.fieldIsRequired.shouldBe(visible);
        paymentPage.invalidFormat.shouldBe(empty);
        var actualStatusPayment = SQLGenerator.getStatusPayment();
        var actualStatusOrder_entityPayment = SQLGenerator.getStatusOrder_entityCredit();
        String expectedStatus = null;
        String expectedId = null;
        assertAll(() -> assertEquals(expectedStatus, actualStatusPayment),
                () -> assertEquals(expectedId, actualStatusOrder_entityPayment));
    }

    @Test
    @DisplayName("1.16 Отправка формы, введя в поле \"CVC/CVV\" две цифры")
    void shortCVC() {
        paymentPage.cardNumber(DataGenerator.getNumberApprovedCard());
        paymentPage.monthNumber(DataGenerator.generateValidDate(0, 0, "MM"));
        paymentPage.yearNumber(DataGenerator.generateValidDate(0, 0, "yy"));
        paymentPage.fullName(DataGenerator.generateRandomSurname());
        paymentPage.CVC(DataGenerator.generateShortCVC_CVV());
        paymentPage.buttonContinue();
        paymentPage.invalidFormat.shouldBe(visible);
        var actualStatusPayment = SQLGenerator.getStatusPayment();
        var actualStatusOrder_entityPayment = SQLGenerator.getStatusOrder_entityCredit();
        String expectedStatus = null;
        String expectedId = null;
        assertAll(() -> assertEquals(expectedStatus, actualStatusPayment),
                () -> assertEquals(expectedId, actualStatusOrder_entityPayment));
    }

    @Test
    @DisplayName("1.17 Отправка формы, введя в поле \"Владелец\" НЕ валидное значение, кириллицей")
    void ownerCyrillic() {
        paymentPage.cardNumber(DataGenerator.getNumberApprovedCard());
        paymentPage.monthNumber(DataGenerator.generateValidDate(0, 0, "MM"));
        paymentPage.yearNumber(DataGenerator.generateValidDate(0, 0, "yy"));
        paymentPage.fullName(DataGenerator.generateRandomSurnameCyrillic());
        paymentPage.CVC(DataGenerator.generateCVC_CVV());
        paymentPage.buttonContinue();
        paymentPage.invalidFormat.shouldBe(visible);
        var actualStatusPayment = SQLGenerator.getStatusPayment();
        var actualStatusOrder_entityPayment = SQLGenerator.getStatusOrder_entityCredit();
        String expectedStatus = null;
        String expectedId = null;
        assertAll(() -> assertEquals(expectedStatus, actualStatusPayment),
                () -> assertEquals(expectedId, actualStatusOrder_entityPayment));
    }

    @Test
    @DisplayName("1.18 Отправка формы, введя в поле \"Владелец\" НЕ валидное значение, цифрами")
    void ownerNumbers() {
        paymentPage.cardNumber(DataGenerator.getNumberApprovedCard());
        paymentPage.monthNumber(DataGenerator.generateValidDate(0, 0, "MM"));
        paymentPage.yearNumber(DataGenerator.generateValidDate(0, 0, "yy"));
        paymentPage.fullName(DataGenerator.generateNotValidCard());
        paymentPage.CVC(DataGenerator.generateCVC_CVV());
        paymentPage.buttonContinue();
        paymentPage.invalidFormat.shouldBe(visible);
        var actualStatusPayment = SQLGenerator.getStatusPayment();
        var actualStatusOrder_entityPayment = SQLGenerator.getStatusOrder_entityCredit();
        String expectedStatus = null;
        String expectedId = null;
        assertAll(() -> assertEquals(expectedStatus, actualStatusPayment),
                () -> assertEquals(expectedId, actualStatusOrder_entityPayment));
    }

    @Test
    @DisplayName("1.19 Отправка формы, введя в поле \"Владелец\" НЕ валидное значение, спецсимволами")
    void ownerSpecialCharacters() {
        paymentPage.cardNumber(DataGenerator.getNumberApprovedCard());
        paymentPage.monthNumber(DataGenerator.generateValidDate(0, 0, "MM"));
        paymentPage.yearNumber(DataGenerator.generateValidDate(0, 0, "yy"));
        paymentPage.fullName(DataGenerator.randomSymbol());
        paymentPage.CVC(DataGenerator.generateCVC_CVV());
        paymentPage.buttonContinue();
        paymentPage.invalidFormat.shouldBe(visible);
        var actualStatusPayment = SQLGenerator.getStatusPayment();
        var actualStatusOrder_entityPayment = SQLGenerator.getStatusOrder_entityCredit();
        String expectedStatus = null;
        String expectedId = null;
        assertAll(() -> assertEquals(expectedStatus, actualStatusPayment),
                () -> assertEquals(expectedId, actualStatusOrder_entityPayment));
    }

    @Test
    @DisplayName("1.20 Отправка формы, введя в поле \"Владелец\" НЕ валидное значение, латиницей, только Имя")
    void justTheName() {
        paymentPage.cardNumber(DataGenerator.getNumberApprovedCard());
        paymentPage.monthNumber(DataGenerator.generateValidDate(0, 0, "MM"));
        paymentPage.yearNumber(DataGenerator.generateValidDate(0, 0, "yy"));
        paymentPage.fullName(DataGenerator.justTheName());
        paymentPage.CVC(DataGenerator.generateCVC_CVV());
        paymentPage.buttonContinue();
        paymentPage.invalidFormat.shouldBe(visible);
        var actualStatusPayment = SQLGenerator.getStatusPayment();
        var actualStatusOrder_entityPayment = SQLGenerator.getStatusOrder_entityCredit();
        String expectedStatus = null;
        String expectedId = null;
        assertAll(() -> assertEquals(expectedStatus, actualStatusPayment),
                () -> assertEquals(expectedId, actualStatusOrder_entityPayment));
    }

    @Test
    @DisplayName("1.21 Отправка формы, введя в поле \"Владелец\" НЕ валидное значение, пробелами")
    void nameWithSpaces() {
        paymentPage.cardNumber(DataGenerator.getNumberApprovedCard());
        paymentPage.monthNumber(DataGenerator.generateValidDate(0, 0, "MM"));
        paymentPage.yearNumber(DataGenerator.generateValidDate(0, 0, "yy"));
        paymentPage.fullName("       ");
        paymentPage.CVC(DataGenerator.generateCVC_CVV());
        paymentPage.buttonContinue();
        paymentPage.fieldIsRequired.shouldBe(visible);
        var actualStatusPayment = SQLGenerator.getStatusPayment();
        var actualStatusOrder_entityPayment = SQLGenerator.getStatusOrder_entityCredit();
        String expectedStatus = null;
        String expectedId = null;
        assertAll(() -> assertEquals(expectedStatus, actualStatusPayment),
                () -> assertEquals(expectedId, actualStatusOrder_entityPayment));
    }
}