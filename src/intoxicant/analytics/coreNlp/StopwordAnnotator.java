
package intoxicant.analytics.coreNlp;

import edu.stanford.nlp.ling.CoreAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.Annotator;
import edu.stanford.nlp.util.Pair;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.util.Version;

import java.util.*;

/**
 * User: jconwell
 * CoreNlp Annotator that checks if in coming token is a stopword
 */
public class StopwordAnnotator implements Annotator, CoreAnnotation<Pair<Boolean, Boolean>> {

    /**
     * stopword annotator class name used in annotators property
     */
    public static final String ANNOTATOR_CLASS = "stopword";

    public static final String STANFORD_STOPWORD = ANNOTATOR_CLASS;
    public static final Requirement STOPWORD_REQUIREMENT = new Requirement(STANFORD_STOPWORD);
