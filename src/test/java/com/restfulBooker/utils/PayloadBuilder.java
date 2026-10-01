package com.restfulBooker.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PayloadBuilder
{
    private static final Pattern TOKEN_PATTERN = Pattern.compile("\\{\\{(.+?)}}");
    private static final Pattern QUOTED_TOKEN  = Pattern.compile("\"\\{\\{(.+?)}}\"");

    private PayloadBuilder()
    {
    }

    public static String loadAndFill(String templateFilePath, Map<String, Object> values) throws IOException
    {
        String content = new String(Files.readAllBytes(Paths.get(templateFilePath)));
        return fill(content, values);
    }

    public static String fill(String templateContent, Map<String, Object> values)
    {
        StringBuilder pass1 = new StringBuilder();
        Matcher m1 = QUOTED_TOKEN.matcher(templateContent);
        while (m1.find())
        {
            String text = getValue(m1.group(1), values);
            String replacement;
            if (text.matches("-?\\d+(\\.\\d+)?"))
            {
                replacement = text;
            }
            else if (text.equalsIgnoreCase("true") || text.equalsIgnoreCase("false"))
            {
                replacement = text.toLowerCase();
            }
            else
            {
                replacement = "\"" + text + "\"";
            }
            m1.appendReplacement(pass1, Matcher.quoteReplacement(replacement));
        }
        m1.appendTail(pass1);

        StringBuilder result = new StringBuilder();
        Matcher m2 = TOKEN_PATTERN.matcher(pass1);
        while (m2.find())
        {
            m2.appendReplacement(result, Matcher.quoteReplacement(getValue(m2.group(1), values)));
        }
        m2.appendTail(result);
        return result.toString();
    }

    private static String getValue(String token, Map<String, Object> values)
    {
        Object value = values.get(token);
        if (value == null) {
            throw new IllegalArgumentException(
                    "No value supplied for template token {{" + token + "}} -- check your data map/Excel row.");
        }
        return String.valueOf(value);
    }
}
