package ru.netology.page;


import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.DisplayName;

import java.time.Duration;

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
    public final SelenideElement invalidFormat = $$(".form-field .input__sub").findBy(text("Неверный формат"));
    public final SelenideElement fieldIsRequired = $$(".form-field .input__sub").findBy(text("Поле обязательно для заполнения"));
    public final SelenideElement incorrectExpirationDate = $$(".form-field .input__sub").findBy(text("Неверно указан срок действия карты"));
    public final SelenideElement theCardIsExpired = $$(".form-field .input__sub").findBy(text("Истёк срок действия карты"));


    public void purchasePage() {
        heading.shouldHave(visible);
        purchase.click();
        paymentByCard.shouldHave(visible);
    }

    public void loanPage() {
        heading.shouldHave(visible);
        buyOnCredit.click();
        credit.shouldHave(visible);
    }

    public void cardNumber(String getCardNumber) {
        fieldsetCardNumber.setValue(String.valueOf(getCardNumber));
    }

    public void monthNumber(String getMonthNumber) {
        fieldsetMonth.setValue(String.valueOf(getMonthNumber));
    }

    public void yearNumber(String getYears) {
        fieldsetYear.setValue(String.valueOf(getYears));
    }

    public void fullName(String getName) {
        fieldsetOwner.setValue(String.valueOf(getName));
    }

    public void CVC(String getCVC) {
        fieldsetCVC_CVV.setValue(String.valueOf(getCVC));
    }

    public void buttonContinue() {
        resume.click();
    }


    @DisplayName("Операция одобрена банком.")
    public void successfully(String expectedText) {
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
}
