package labs.pm.optional.regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author hatzp
 **/
public class PatternExample {

    public static void main(String[] args) {
        String t = "It was the best of times";

        Pattern pattern = Pattern.compile("the");

        //matcher will use the pattern to match in the String t
        Matcher matcher = pattern.matcher(t);

        System.out.println(matcher.find());

    }
}
