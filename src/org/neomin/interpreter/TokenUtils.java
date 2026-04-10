package org.neomin.interpreter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TokenUtils {

    public static int getRegisterIndex(String token) {
        if (!token.startsWith("i")) {
            throw new IllegalArgumentException("Invalid address: " + token);
        }

        return Integer.parseInt(token.substring(1));
    }

    public static String[] tokenize(String line) {
        List<String> tokens = new ArrayList<>();
        Matcher matcher = Pattern.compile("\"([^\"]*)\"|(\\S+)").matcher(line);

        while (matcher.find()) {
            if (matcher.group(1) != null) {
                tokens.add(matcher.group(1));
            } else {
                tokens.add(matcher.group(2));
            }
        }

        return tokens.toArray(new String[0]);
    }

    public static boolean isNumeric(String value) {

        if (value == null || value.isEmpty()) {
            return false;
        }

        for (int i = 0; i < value.length(); i++) {

            char c = value.charAt(i);

            if (c == '-' || c == '+') {
                if (i != 0) return false;
                continue;
            }

            if (!Character.isDigit(c)) {
                return false;
            }
        }

        return true;
    }

    public static float toFloat(Object o) {

        if (o instanceof Float) {
            return (Float) o;
        }

        if (o instanceof Integer) {
            return ((Integer) o).floatValue();
        }

        throw new IllegalArgumentException("Not a number");
    }

    public static boolean isFloatNumber(String value) {

        if (value == null || value.isEmpty()) {
            return false;
        }

        boolean dotSeen = false;
        boolean digitSeen = false;

        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);

            if (c == '.') {
                if (dotSeen) {
                    return false;
                }
                dotSeen = true;
                continue;
            }

            if (c == '-' || c == '+') {
                if (i != 0) {
                    return false;
                }
                continue;
            }

            if (Character.isDigit(c)) {
                digitSeen = true;
                continue;
            }

            return false;
        }

        return digitSeen;
    }
}
