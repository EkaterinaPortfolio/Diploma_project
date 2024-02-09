package ru.netology.test;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import ru.netology.data.DataGenerator;
import ru.netology.data.SQLGenerator;
import ru.netology.page.PaymentPage;

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
        paymentPage.cardFieldApprovedCard();
        paymentPage.monthValidField();
        paymentPage.yearValidField();
        paymentPage.ownerValidField();
        paymentPage.CVCValidField();
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
        paymentPage.declinedCard();
        paymentPage.monthValidField();
        paymentPage.yearValidField();
        paymentPage.ownerValidField();
        paymentPage.CVCValidField();
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
        paymentPage.randomCard();
        paymentPage.monthValidField();
        paymentPage.yearValidField();
        paymentPage.ownerValidField();
        paymentPage.CVCValidField();
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
        paymentPage.shortCard();
        paymentPage.monthValidField();
        paymentPage.yearValidField();
        paymentPage.ownerValidField();
        paymentPage.CVCValidField();
        paymentPage.buttonContinue();
        paymentPage.invalidFormat("Неверный формат");
    }

    @Test
    @DisplayName("1.27 Отправка формы, оставив пустым поле \"Номер карты\"")
    void leaveItBlankCardNumberСredit() {
        paymentPage.leaveItBlankCard();
        paymentPage.monthValidField();
        paymentPage.yearValidField();
        paymentPage.ownerValidField();
        paymentPage.CVCValidField();
        paymentPage.buttonContinue();
        paymentPage.fieldIsRequired("Поле обязательно для заполнения");
    }

    @Test
    @DisplayName("1.28 Отправка формы, заполнив поле \"Месяц\" НЕ валидным значением 00")
    void enteringMonth00Сredit() {
        paymentPage.cardFieldApprovedCard();
        paymentPage.month00();
        paymentPage.yearMoreCurrent();
        paymentPage.ownerValidField();
        paymentPage.CVCValidField();
        paymentPage.buttonContinue();
        paymentPage.incorrectExpirationDate("Неверно указан срок действия карты");
    }

    @Test
    @DisplayName("1.29 Отправка формы, заполнив поле \"Месяц\" НЕ валидным значением 13")
    void enteringMonth13Сredit() {
        paymentPage.cardFieldApprovedCard();
        paymentPage.month13();
        paymentPage.yearValidField();
        paymentPage.ownerValidField();
        paymentPage.CVCValidField();
        paymentPage.buttonContinue();
        paymentPage.incorrectExpirationDate("Неверно указан срок действия карты");
    }

    @Test
    @DisplayName("1.30 Отправка формы, оставив поле \"Месяц\" не заполненным")
    void leaveMonthEmptyСredit() {
        paymentPage.cardFieldApprovedCard();
        paymentPage.monthNotFilledIn();
        paymentPage.yearValidField();
        paymentPage.ownerValidField();
        paymentPage.CVCValidField();
        paymentPage.buttonContinue();
        paymentPage.fieldIsRequired("Поле обязательно для заполнения");
    }

    @Test
    @DisplayName("1.31 Отправка формы, введя \"Месяц\" на один меньше текущего и введя в поле \"Год\" текущий")
    void expiredСardСredit() {
        paymentPage.cardFieldApprovedCard();
        paymentPage.expiredСards();
        paymentPage.yearValidField();
        paymentPage.ownerValidField();
        paymentPage.CVCValidField();
        paymentPage.buttonContinue();
        paymentPage.theCardIsExpired("Истёк срок действия карты");
    }

    @Test
    @DisplayName("1.32 Отправка формы, прибавив к текущему году 6 лет")
    void yearOverLimitСredit() {
        paymentPage.cardFieldApprovedCard();
        paymentPage.monthValidField();
        paymentPage.yearOverLimit();
        paymentPage.ownerValidField();
        paymentPage.CVCValidField();
        paymentPage.buttonContinue();
        paymentPage.incorrectExpirationDate("Неверно указан срок действия карты");
    }

    @Test
    @DisplayName("1.33 Отправка формы, введя в поле \"Год\" на один меньше текущего")
    void lastYearСredit() {
        paymentPage.cardFieldApprovedCard();
        paymentPage.monthValidField();
        paymentPage.generateLastYear();
        paymentPage.ownerValidField();
        paymentPage.CVCValidField();
        paymentPage.buttonContinue();
        paymentPage.theCardIsExpired("Истёк срок действия карты");
    }

    @Test
    @DisplayName("1.34 Отправка формы, оставив поле \"Год\" пустым")
    void yearFieldEmptyСredit() {
        paymentPage.cardFieldApprovedCard();
        paymentPage.monthValidField();
        paymentPage.year();
        paymentPage.ownerValidField();
        paymentPage.CVCValidField();
        paymentPage.buttonContinue();
        paymentPage.fieldIsRequired("Поле обязательно для заполнения");
    }

    @Test
    @DisplayName("1.35 Отправка формы, оставив поле \"Владелец\" пустым")
    void ownerFieldEmptyСredit() {
        paymentPage.cardFieldApprovedCard();
        paymentPage.monthValidField();
        paymentPage.yearValidField();
        paymentPage.owner();
        paymentPage.CVCValidField();
        paymentPage.buttonContinue();
        paymentPage.fieldIsRequired("Поле обязательно для заполнения");
    }

    @Test
    @DisplayName("1.36 Отправка формы, оставив поле \"CVC/CVV\" пустым")
    void emptyCVCСredit() {
        paymentPage.cardFieldApprovedCard();
        paymentPage.monthValidField();
        paymentPage.yearValidField();
        paymentPage.ownerValidField();
        paymentPage.emptyCVC();
        paymentPage.buttonContinue();
        paymentPage.fieldIsRequired("Поле обязательно для заполнения");
    }
    @Test
    @DisplayName("1.37 Отправка формы, введя в поле \"CVC/CVV\" две цифры")
    void shortCVCСredit() {
        paymentPage.cardFieldApprovedCard();
        paymentPage.monthValidField();
        paymentPage.yearValidField();
        paymentPage.ownerValidField();
        paymentPage.shortCVC();
        paymentPage.buttonContinue();
        paymentPage.invalidFormat("Неверный формат");
    }

    @Test
    @DisplayName("1.38 Отправка формы, введя в поле \"Владелец\" НЕ валидное значение, кириллицей")
    void ownerCyrillicСredit() {
        paymentPage.cardFieldApprovedCard();
        paymentPage.monthValidField();
        paymentPage.yearValidField();
        paymentPage.cyrillic();
        paymentPage.CVCValidField();
        paymentPage.buttonContinue();
        paymentPage.invalidFormat("Неверный формат");
    }

    @Test
    @DisplayName("1.39 Отправка формы, введя в поле \"Владелец\" НЕ валидное значение, цифрами")
    void ownerNumbersСredit() {
        paymentPage.cardFieldApprovedCard();
        paymentPage.monthValidField();
        paymentPage.yearValidField();
        paymentPage.numbers();
        paymentPage.CVCValidField();
        paymentPage.buttonContinue();
        paymentPage.invalidFormat("Неверный формат");
    }

    @Test
    @DisplayName("1.40 Отправка формы, введя в поле \"Владелец\" НЕ валидное значение, спецсимволами")
    void ownerSpecialCharactersСredit() {
        paymentPage.cardFieldApprovedCard();
        paymentPage.monthValidField();
        paymentPage.yearValidField();
        paymentPage.randomSymbol();
        paymentPage.CVCValidField();
        paymentPage.buttonContinue();
        paymentPage.invalidFormat("Неверный формат");
    }

    @Test
    @DisplayName("1.41 Отправка формы, введя в поле \"Владелец\" НЕ валидное значение, латиницей, только Имя")
    void justTheNameСredit() {
        paymentPage.cardFieldApprovedCard();
        paymentPage.monthValidField();
        paymentPage.yearValidField();
        paymentPage.justTheName();
        paymentPage.CVCValidField();
        paymentPage.buttonContinue();
        paymentPage.invalidFormat("Неверный формат");
    }

    @Test
    @DisplayName("1.42 Отправка формы, введя в поле \"Владелец\" НЕ валидное значение, пробелами")
    void nameWithSpacesСredit() {
        paymentPage.cardFieldApprovedCard();
        paymentPage.monthValidField();
        paymentPage.yearValidField();
        paymentPage.nameWithSpaces();
        paymentPage.CVCValidField();
        paymentPage.buttonContinue();
        paymentPage.fieldIsRequired("Поле обязательно для заполнения");
    }
}
