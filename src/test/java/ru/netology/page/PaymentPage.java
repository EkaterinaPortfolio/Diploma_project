package ru.netology.page;


import com.codeborne.selenide.SelenideElement;
import lombok.Value;
import org.junit.jupiter.api.DisplayName;
import ru.netology.data.DataGenerator;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class PaymentPage {
    private final SelenideElement heading = $(byText("Путешествие дня"));
    private final SelenideElement purchase = $$("button.button").findBy(text("Купить"));
    private final SelenideElement buyOnCredit = $$("button.button").findBy(text("Купить в кредит"));
    private final SelenideElement paymentByCard = $(byText("Оплата по карте"));
    private final SelenideElement credit = $(byText("Кредит по данным карты"));
    private final SelenideElement fieldsetCardNumber = $$(".form-field").findBy(text("Номер карты")).find(".input__control");
    private final SelenideElement fieldsetMonth = $$(".form-field .input-group__input-case").findBy(text("Месяц")).find(".input__control");
    private final SelenideElement fieldsetYear = $$(".form-field .input-group__input-case").findBy(text("Год")).find(".input__control");
    private final SelenideElement fieldsetOwner = $$(".form-field .input-group__input-case").findBy(text("Владелец")).find(".input__control");
    private final SelenideElement fieldsetCVC_CVV = $$(".form-field .input-group__input-case").findBy(text("CVC/CVV")).find(".input__control");
    private final SelenideElement resume = $$(".button__text").findBy(text("Продолжить"));
    private final SelenideElement successfully = $(".notification_status_ok .notification__content");
    private final SelenideElement bankRefused = $(".notification_status_error .notification__content");
    private final SelenideElement invalidFormat = $$(".form-field .input__sub").findBy(text("Неверный формат"));
    private final SelenideElement fieldIsRequired = $$(".form-field .input__sub").findBy(text("Поле обязательно для заполнения"));
    private final SelenideElement incorrectExpirationDate = $$(".form-field .input__sub").findBy(text("Неверно указан срок действия карты"));
    private final SelenideElement theCardIsExpired = $$(".form-field .input__sub").findBy(text("Истёк срок действия карты"));


    public void purchasePage() {    //переход к покупке
        heading.shouldHave(visible);
        purchase.click();
        paymentByCard.shouldHave(visible);
    }

    public void loanPage() {   // переход к кредиту
        heading.shouldHave(visible);
        buyOnCredit.click();
        credit.shouldHave(visible);
    }

    //номера карт
    public void cardFieldApprovedCard() {       // вставляем номер карты
        fieldsetCardNumber.setValue(String.valueOf(DataGenerator.getNumberApprovedCard()));
    }

    public void declinedCard() {
        fieldsetCardNumber.setValue(String.valueOf(DataGenerator.getNumberDeclinedCard()));
    }

    public void randomCard() {
        fieldsetCardNumber.setValue(String.valueOf(DataGenerator.generateRandomCard()));
    }

    public void shortCard() {
        fieldsetCardNumber.setValue(String.valueOf(DataGenerator.generateNotValidCard()));
    }

    public void leaveItBlankCard() {
        fieldsetCardNumber.setValue(String.valueOf(DataGenerator.numberEmptinessCard()));
    }

///////

    private String generateValidDate(int addMonths, int addYears, String pattern) {
        return LocalDate.now().plusMonths(addMonths).plusYears(addYears).format(DateTimeFormatter.ofPattern(pattern));
    }

    private String generateLastYear(int minusYears, String pattern) {
        return LocalDate.now().minusYears(minusYears).format(DateTimeFormatter.ofPattern(pattern));
    }

    //месяц
    public void monthValidField() {   // вставлям месяц
        fieldsetMonth.setValue(generateValidDate(0, 0, "MM"));
    }

    public void month00() {
        fieldsetMonth.setValue(String.valueOf(DataGenerator.month("00")));
    }

    public void month13() {
        fieldsetMonth.setValue(String.valueOf(DataGenerator.month("13")));
    }

    public void monthNotFilledIn() {
        fieldsetMonth.setValue(String.valueOf(DataGenerator.month("")));
    }

    public void expiredСards() {   // вставлям месяц
        fieldsetMonth.setValue(generateValidDate(11, 0, "MM"));
    }

    // год
    public void yearValidField() { // вставлям year
        fieldsetYear.setValue(generateValidDate(0, 0, "yy"));
    }

    public void yearMoreCurrent() { // вставлям year
        fieldsetYear.setValue(generateValidDate(0, 1, "yy"));
    }

    public void yearOverLimit() { // вставлям year
        fieldsetYear.setValue(generateValidDate(0, 6, "yy"));
    }

    public void generateLastYear() { // вставлям year
        fieldsetYear.setValue(generateLastYear(1, "yy"));
    }

    public void year() {
        fieldsetYear.setValue(String.valueOf(DataGenerator.month("")));
    }

    // владелец
    public void ownerValidField() { // вставлям владедльца
        fieldsetOwner.setValue(DataGenerator.generateRandomSurname());
    }

    public void owner() {
        fieldsetOwner.setValue("");
    }

    public void nameWithSpaces() {
        fieldsetOwner.setValue("       ");
    }

    public void cyrillic() { // вставлям владедльца
        fieldsetOwner.setValue(DataGenerator.generateRandomSurnameCyrillic());
    }

    public void numbers() {
        fieldsetOwner.setValue(String.valueOf(DataGenerator.generateNotValidCard()));
    }

    public void randomSymbol() {
        fieldsetOwner.setValue(String.valueOf(DataGenerator.randomSymbol()));
    }

    public void justTheName() {
        fieldsetOwner.setValue(String.valueOf(DataGenerator.justTheName()));
    }


    //CVC
    public void CVCValidField() { // вставлям CVC
        fieldsetCVC_CVV.setValue(DataGenerator.generateCVC_CVV());
    }

    public void emptyCVC() {
        fieldsetCVC_CVV.setValue("");
    }

    public void shortCVC() {
        fieldsetCVC_CVV.setValue(DataGenerator.generateShortCVC_CVV());
    }


    public void buttonContinue() {
        resume.click();
    }

    // ошибки
    @DisplayName("Операция одобрена банком.")
    public void successfully(String expectedText) {    //проверяет  появление ошибки
        successfully
                .shouldBe(visible, Duration.ofSeconds(15))
                .shouldHave(exactText(expectedText)).shouldBe(visible);
    }

    @DisplayName("Ошибка! Банк отказал в проведении операции.")
    public void bankRefused(String expectedText) {
        bankRefused
                .shouldBe(visible, Duration.ofSeconds(15))
                .shouldHave(exactText(expectedText)).shouldBe(visible);
    }

    @DisplayName("Неверный формат")
    public void invalidFormat(String expectedText) {
        invalidFormat
                .shouldHave(exactText(expectedText)).shouldBe(visible);
    }

    @DisplayName("Поле обязательно для заполнения")
    public void fieldIsRequired(String expectedText) {
        fieldIsRequired
                .shouldHave(exactText(expectedText)).shouldBe(visible);
    }

    @DisplayName("Неверно указан срок действия карты")
    public void incorrectExpirationDate(String expectedText) {
        incorrectExpirationDate
                .shouldHave(exactText(expectedText)).shouldBe(visible);
    }

    @DisplayName("Истёк срок действия карты")
    public void theCardIsExpired(String expectedText) {
        theCardIsExpired
                .shouldHave(exactText(expectedText)).shouldBe(visible);
    }


}
