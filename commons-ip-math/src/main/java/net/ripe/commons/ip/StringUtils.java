package net.ripe.commons.ip;

public class StringUtils {

    public static String join(String[] strings, String delimiter) {
        return join(strings, delimiter, 0, strings.length);
    }

    public static String join(String[] strings, String delimiter, int from, int end) {
        if (end == 0 || from == end) {
            return "";
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = from; i < (end - 1); i++) {
            stringBuilder.append(strings[i]).append(delimiter);
        }
        return stringBuilder.append(strings[end - 1]).toString();
    }
}
