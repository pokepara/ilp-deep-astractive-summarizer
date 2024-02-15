package jaist.summarization.utils;

import edu.stanford.nlp.util.CollectionUtils;

import java.util.*;

/**
 * Created by chientran on 3/3/16.
 */
public class StringUtils {
    public static int countWords(String text){
        return splitStringToWords(text).size();
    }

    public static List<String> generateUnigrams(S