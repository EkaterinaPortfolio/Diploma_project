package ru.netology.test;


import com.codeborne.selenide.logevents.SelenideLogger;
import org.junit.jupiter.api.*;
import ru.netology.data.DataGenerator;
import ru.netology.data.SQLGenerator;
import ru.netology.page.PaymentPage;
import io.qameta.allure.selenide.AllureSelenide;


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
        paymentPage.cardFieldApprovedCard();
        paymentPage.monthValidField();
        paymentPage.yearValidField();
        paymentPage.ownerValidField();
        paymentPage.CVCValidField();
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
        paymentPage.declinedCard();
        paymentPage.monthValidField();
        paymentPage.yearValidField();
        paymentPage.ownerValidField();
        paymentPage.CVCValidField();
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
        paymentPage.randomCard();
        paymentPage.monthValidField();
        paymentPage.yearValidField();
        paymentPage.ownerValidField();
        paymentPage.CVCValidField();
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
        paymentPage.shortCard();
        paymentPage.monthValidField();
        paymentPage.yearValidField();
        paymentPage.ownerValidField();
        paymentPage.CVCValidField();
        paymentPage.buttonContinue();
        paymentPage.invalidFormat("Неверный формат");
    }

    @Test
    @DisplayName("1.6 Отправка формы, оставив пустым поле \"Номер карты\"")
    void leaveItBlankCardNumber() {
        paymentPage.leaveItBlankCard();
        paymentPage.monthValidField();
        paymentPage.yearValidField();
        paymentPage.ownerValidField();
        paymentPage.CVCValidField();
        paymentPage.buttonContinue();
        paymentPage.fieldIsRequired("Поле обязательно для заполнения");
    }

    @Test
    @DisplayName("1.7 Отправка формы, заполнив поле \"Месяц\" НЕ валидным значением 00")
    void enteringMonth00() {
        paymentPage.cardFieldApprovedCard();
        paymentPage.month00();
        paymentPage.yearMoreCurrent();
        paymentPage.ownerValidField();
        paymentPage.CVCValidField();
        paymentPage.buttonContinue();
        paymentPage.incorrectExpirationDate("Неверно указан срок действия карты");
        var actualStatusPayment = SQLGenerator.getStatusPayment();
        var actualStatusOrder_entityPayment = SQLGenerator.getStatusOrder_entityCredit();
        String expectedStatus = DataGenerator.APPROVED().getStatus();
        String expectedId = null;
        assertAll(() -> assertEquals(expectedStatus, actualStatusPayment),
                () -> assertEquals(expectedId, actualStatusOrder_entityPayment));
    }

    @Test
    @DisplayName("1.8 Отправка формы, заполнив поле \"Месяц\" НЕ валидным значением 13")
    void enteringMonth13() {
        paymentPage.cardFieldApprovedCard();
        paymentPage.month13();
        paymentPage.yearValidField();
        paymentPage.ownerValidField();
        paymentPage.CVCValidField();
        paymentPage.buttonContinue();
        paymentPage.incorrectExpirationDate("Неверно указан срок действия карты");
    }

    @Test
    @DisplayName("1.9 Отправка формы, оставив поле \"Месяц\" не заполненным")
    void leaveMonthEmpty() {
        paymentPage.cardFieldApprovedCard();
        paymentPage.monthNotFilledIn();
        paymentPage.yearValidField();
        paymentPage.ownerValidField();
        paymentPage.CVCValidField();
        paymentPage.buttonContinue();
        paymentPage.fieldIsRequired("Поле обязательно для заполнения");
    }

    @Test
    @DisplayName("1.10 Отправка формы, введя \"Месяц\" на один меньше текущего и введя в поле \"Год\" текущий")
    void expiredСard() {
        paymentPage.cardFieldApprovedCard();
        paymentPage.expiredСards();
        paymentPage.yearValidField();
        paymentPage.ownerValidField();
        paymentPage.CVCValidField();
        paymentPage.buttonContinue();
        paymentPage.theCardIsExpired("Истёк срок действия карты");
    }

    @Test
    @DisplayName("1.11 Отправка формы, прибавив к текущему году 6 лет")
    void yearOverLimit() {
        paymentPage.cardFieldApprovedCard();
        paymentPage.monthValidField();
        paymentPage.yearOverLimit();
        paymentPage.ownerValidField();
        paymentPage.CVCValidField();
        paymentPage.buttonContinue();
        paymentPage.incorrectExpirationDate("Неверно указан срок действия карты");
    }

    @Test
    @DisplayName("1.12 Отправка формы, введя в поле \"Год\" на один меньше текущего")
    void lastYear() {
        paymentPage.cardFieldApprovedCard();
        paymentPage.monthValidField();
        paymentPage.generateLastYear();
        paymentPage.ownerValidField();
        paymentPage.CVCValidField();
        paymentPage.buttonContinue();
        paymentPage.theCardIsExpired("Истёк срок действия карты");
    }

    @Test
    @DisplayName("1.13 Отправка формы, оставив поле \"Год\" пустым")
    void yearFieldEmpty() {
        paymentPage.cardFieldApprovedCard();
        paymentPage.monthValidField();
        paymentPage.year();
        paymentPage.ownerValidField();
        paymentPage.CVCValidField();
        paymentPage.buttonContinue();
        paymentPage.fieldIsRequired("Поле обязательно для заполнения");
    }

    @Test
    @DisplayName("1.14 Отправка формы, оставив поле \"Владелец\" пустым")
    void ownerFieldEmpty() {
        paymentPage.cardFieldApprovedCard();
        paymentPage.monthValidField();
        paymentPage.yearValidField();
        paymentPage.owner();
        paymentPage.CVCValidField();
        paymentPage.buttonContinue();
        paymentPage.fieldIsRequired("Поле обязательно для заполнения");
    }

    @Test
    @DisplayName("1.15 Отправка формы, оставив поле \"CVC/CVV\" пустым")
    void emptyCVC() {
        paymentPage.cardFieldApprovedCard();
        paymentPage.monthValidField();
        paymentPage.yearValidField();
        paymentPage.ownerValidField();
        paymentPage.emptyCVC();
        paymentPage.buttonContinue();
        paymentPage.fieldIsRequired("Поле обязательно для заполнения");
    }

    @Test
    @DisplayName("1.16 Отправка формы, введя в поле \"CVC/CVV\" две цифры")
    void shortCVC() {
        paymentPage.cardFieldApprovedCard();
        paymentPage.monthValidField();
        paymentPage.yearValidField();
        paymentPage.ownerValidField();
        paymentPage.shortCVC();
        paymentPage.buttonContinue();
        paymentPage.invalidFormat("Неверный формат");
    }

    @Test
    @DisplayName("1.17 Отправка формы, введя в поле \"Владелец\" НЕ валидное значение, кириллицей")
    void ownerCyrillic() {
        paymentPage.cardFieldApprovedCard();
        paymentPage.monthValidField();
        paymentPage.yearValidField();
        paymentPage.cyrillic();
        paymentPage.CVCValidField();
        paymentPage.buttonContinue();
        paymentPage.invalidFormat("Неверный формат");
    }

    @Test
    @DisplayName("1.18 Отправка формы, введя в поле \"Владелец\" НЕ валидное значение, цифрами")
    void ownerNumbers() {
        paymentPage.cardFieldApprovedCard();
        paymentPage.monthValidField();
        paymentPage.yearValidField();
        paymentPage.numbers();
        paymentPage.CVCValidField();
        paymentPage.buttonContinue();
        paymentPage.invalidFormat("Неверный формат");
    }

    @Test
    @DisplayName("1.19 Отправка формы, введя в поле \"Владелец\" НЕ валидное значение, спецсимволами")
    void ownerSpecialCharacters() {
        paymentPage.cardFieldApprovedCard();
        paymentPage.monthValidField();
        paymentPage.yearValidField();
        paymentPage.randomSymbol();
        paymentPage.CVCValidField();
        paymentPage.buttonContinue();
        paymentPage.invalidFormat("Неверный формат");
    }

    @Test
    @DisplayName("1.20 Отправка формы, введя в поле \"Владелец\" НЕ валидное значение, латиницей, только Имя")
    void justTheName() {
        paymentPage.cardFieldApprovedCard();
        paymentPage.monthValidField();
        paymentPage.yearValidField();
        paymentPage.justTheName();
        paymentPage.CVCValidField();
        paymentPage.buttonContinue();
        paymentPage.invalidFormat("Неверный формат");
    }

    @Test
    @DisplayName("1.21 Отправка формы, введя в поле \"Владелец\" НЕ валидное значение, пробелами")
    void nameWithSpaces() {
        paymentPage.cardFieldApprovedCard();
        paymentPage.monthValidField();
        paymentPage.yearValidField();
        paymentPage.nameWithSpaces();
        paymentPage.CVCValidField();
        paymentPage.buttonContinue();
        paymentPage.fieldIsRequired("Поле обязательно для заполнения");
    }
}