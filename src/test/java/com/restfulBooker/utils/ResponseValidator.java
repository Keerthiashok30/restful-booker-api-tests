package com.restfulBooker.utils;

import org.json.JSONException;
import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.comparator.CustomComparator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class ResponseValidator
{
    public void assertEqualsIgnoringNodes(String label, String actualJson, String expectedJson, List<String> ignoredPaths, JSONCompareMode mode) throws JSONException
    {
        assertEquals(label, actualJson, expectedJson, ignoredPaths, null, mode);
    }

    public void assertEqualsWithShapeChecks(String label, String actualJson, String expectedJson, Map<String, Pattern> patternPaths, JSONCompareMode mode) throws JSONException
    {
        assertEquals(label, actualJson, expectedJson, null, patternPaths, mode);
    }

    public void assertEquals(String label, String actualJson, String expectedJson, List<String> ignoredPaths, Map<String, Pattern> patternPaths,JSONCompareMode mode) throws JSONException
    {
        List<Customization> customizations = new ArrayList<>();
        if (ignoredPaths != null)
        {
            for (String path : ignoredPaths)
            {
                customizations.add(new Customization(path, (o1, o2) -> true));
            }
        }
        if (patternPaths != null) {
            for (Map.Entry<String, Pattern> entry : patternPaths.entrySet())
            {
                Pattern pattern = entry.getValue();
                customizations.add(new Customization(entry.getKey(),
                        (actualValue, expectedValue) -> pattern.matcher(String.valueOf(actualValue)).matches()));
            }
        }
        CustomComparator comparator = new CustomComparator(mode, customizations.toArray(new Customization[0]));
        JSONAssert.assertEquals(label, expectedJson, actualJson, comparator);
    }
}
