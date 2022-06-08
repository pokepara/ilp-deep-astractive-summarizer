package jaist.summarization;

import edu.stanford.nlp.pipeline.EntityMentionsAnnotator;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.logging.RedwoodConfiguration;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

/**
 * Created by chientran on 9/29/15.
 */
public class AnnotatorHub {
    private static AnnotatorHub instance = null;

    private StanfordCoreNLP pipeline = null;
    private EntityMentionsAnnotator entityMentionsAnnotator = null;

    protected AnnotatorHub(){
        Properties props = new Properties();
        try {
            String propFilename = "resources/config.properties";

            