package ru.netology.data;

import com.github.javafaker.CreditCardType;
import com.github.javafaker.Faker;
import lombok.Getter;
import lombok.Value;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Random;

public class DataGenerator {

    private static final Faker faker1 = new Faker(new Locale("ru"));
    private static final Faker faker = new Faker(new Locale("en"));
    public DataGenerator() {
    }
    public static CardInfo getNumberApprovedCard() {
        return new CardInfo("4444444444444441");
    }
    public static CardInfo getNumberDeclinedCard() {
        return new CardInfo("4444444444444442");
    }

    public static Status DECLINED() {
        return new Status( "DECLINED");
    }
    public static Status APPROVED() {
        return new Status( "APPROVED");
    }

    public static String generateRandomCard() {      // рандомная карта 16 цифр
        return faker.finance().creditCard(CreditCardType.MASTERCARD);
    }

    public static String generateNotValidCard() {   //  рандомная карта 15 цифр
        return faker.numerify("#### #### #### ###");
    }
    public static String generateCVC_CVV() {
        return faker.numerify("###");
    }
    public static String generateShortCVC_CVV() {
        return faker.numerify("##");
    }

    public static String numberEmptinessCard() {   //  пустота
        return faker.numerify("");
    }

    public static String generateRandomSurname() {
        return faker.name().fullName();
    }
    public static String justTheName() {
        return faker.name().firstName();
    }
    public static String generateRandomSurnameCyrillic() {
        return faker1.name().fullName();
    }
    public static String randomSymbol() {
        var cities = new String[]{"@$%^", "!%^&**", "**$%^<>?", ":?:&^^", "$%^&#", "@#$%&*(", "&^*#$$%", "@##^^*^"};
        return cities[new Random().nextInt(cities.length)];
    }

    public static Month month(String month) {return new Month(month);
    }

    @Value
    public static class CardInfo {
        String cardNumber;
    }
    @Value
    public static class Month {
        String month;
    }
    @Value
    public static class FullName {
        String fullName;
    }
    @Value
    public static class Status {
        String status;
    }
    @Value
    public static class Id {
        String id;
    }

}
