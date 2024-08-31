package labs.pm.optional.regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author hatzp
 **/
public class CustomCharClassExamples{
    public static void main(String[] args) {
        String t = "It was the best of times";

        Pattern p1 = Pattern.compile("w.s");
        Matcher m1 = p1.matcher(t);
        System.out.println(m1.find());

        Pattern p2 = Pattern.compile("w[abc]s");
        Matcher m2 = p2.matcher(t);
        System.out.println(m2.find());

        Pattern p3 = Pattern.compile("t[^aeou].es");
        Matcher m3 = p3.matcher(t);
        System.out.println(m3.find());

    }
}
