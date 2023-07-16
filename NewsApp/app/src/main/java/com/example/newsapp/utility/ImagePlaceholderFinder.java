package com.example.newsapp.utility;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImagePlaceholderFinder {

    public static List<Integer> GetIndeces(String pattern, String text){
        List<Integer> result = new ArrayList<>();

        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(text);

        while (matcher.find()) {
            int startIndex = matcher.start();
            result.add(startIndex);
        }
        return result;
    }
}
