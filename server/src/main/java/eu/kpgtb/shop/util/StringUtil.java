package eu.kpgtb.shop.util;

import java.util.Arrays;
import java.util.stream.Collectors;

public class StringUtil {
    public static String convertSneakToTitleCase(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        return Arrays
                .stream(text.split("_"))
                .map(word -> word.isEmpty()
                        ? word
                        : Character.toTitleCase(word.charAt(0)) + word
                        .substring(1)
                        .toLowerCase())
                .collect(Collectors.joining(" "));
    }
}
