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

    