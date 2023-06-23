package jaist.summarization.unit;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.util.CollectionUtils;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.Pair;
import intoxicant.analytics.coreNlp.StopwordAnnotator;
import jaist.summarization.AnnotatorHub;
import jaist.summarization.StopwordRemover;
import jaist.summarization.utils.StringUtils;

import java.util.*;

/**
 * Created by chientran on 9/29/15.
 */
public class Phrase{
    String content = null;
    Boolean isNP = true;
    Double score = 0.0d;
    Integer parentId = -1;
    Integer sentenceNodeId = 0;
    int id;

    int sentenceLength = 0;

    private static int _npID = 0;
    private static int _vpID = 0;
    private static String[] pronouns = {"it", "i", "you", "he", "they", "we", "she", "who", "them", "me", "him", "one", "her", "us", "something", "nothing", "anything", "himself", "everything", "someone", "themselves", "everyone", "itself", "anyone", "myself"};

    private Set<String> concepts = null;

    public Phrase(String content, Boolean isNP){
        this.content = content;
        this.isNP = isNP;
        if (isNP){
            id = _npID;
            _npID += 1;
        }else{
            id = _vpID;
            _vpID += 1;
        }
    }

    public Phrase(String content, Boolean isNP, Integer parentId){
     