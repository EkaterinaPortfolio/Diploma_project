package ru.netology.data;

import com.github.javafaker.CreditCardType;
import com.github.javafaker.Faker;
import lombok.Value;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Random;

public class DataGenerator {

    private static final Faker faker1 = new Faker(new Locale("ru"));
    private static final Faker faker = new Faker(new Locale("en"));

    public static String getNumberApprovedCard() {
        return "4444444444444441";
    }

    public static String getNumberDeclinedCard() {
        return "4444444444444442";
    }

    public static Status DECLINED() {
        return new Status("DECLINED");
    }

    public static Status APPROVED() {
        return new Status("APPROVED");
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
        var symbol = new String[]{"@$%^", "!%^&**", "**$%^<>?", ":?:&^^", "$%^&#", "@#$%&*(", "&^*#$$%", "@##^^*^"};
        return symbol[new Random().nextInt(symbol.length)];
    }

    public static String generateValidDate(int addMonths, int addYears, String pattern) {
        return LocalDate.now().plusMonths(addMonths).plusYears(addYears).format(DateTimeFormatter.ofPattern(pattern));
    }

    public static String generateLastYear(int minusYears, String pattern) {
        return LocalDate.now().minusYears(minusYears).format(DateTimeFormatter.ofPattern(pattern));
    }

    @Value
    public static class Status {
        String status;
    }

}
