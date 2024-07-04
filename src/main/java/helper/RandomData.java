package helper;

import org.apache.commons.lang3.RandomStringUtils;
import java.util.Random;


public class RandomData {
    public static String  userRandomEmail(){
        String lowerCharacters = "abcdefghijklmnopqrstuvwxyz";
        String upperCharacters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String numberCharacters = "0123456789";

        return RandomStringUtils.random(3, lowerCharacters)+
                RandomStringUtils.random(3, upperCharacters)+
                RandomStringUtils.random(3, numberCharacters)+
                System.currentTimeMillis()+"@acname.com";
    }

//    public static String  userRandomPhoneNumber(){
//        String countryCodeNumber = "081";
//        String phoneDigits = "12345678";
//
//        return  countryCodeNumber+
//                RandomStringUtils.random(1, phoneDigits)+
//                System.currentTimeMillis();
//    }


    public static String userRandomPhoneNumber() {

        String countryCodeNumber = "081"; // The fixed prefix

// Generate exactly 7 random digits

        Random random = new Random();
        StringBuilder phoneDigits = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            phoneDigits.append(random.nextInt(10)); // Append a random digit (0-9)
        }

        return countryCodeNumber + phoneDigits.toString();

    }
}
