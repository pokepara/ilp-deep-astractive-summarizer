
package jaist.summarization;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.util.CoreMap;
import jaist.summarization.unit.Paragraph;
import jaist.summarization.utils.StringUtils;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.util.*;

/**
 * Created by chientran on 3/3/16.
 */
public class InputDocument {
    private Annotation annotation;
    private HashSet<String> namedEntities;
    private Map<String, String> wordToLemmaMap;
    private HashMap<String, HashSet<String>> corefs;
    private static final String PARAGRAPH_SPLIT_REGEX = "(?m)(?=^\\s{4})";
    private ArrayList<Paragraph> paragraphs;
    private String headline;

    public InputDocument(String text){
        this(text, false);
    }
    public InputDocument(String text, boolean isDucData){
        if (isDucData){
            text = processDucDocument(text);
        }

        this.annotation = new Annotation(text);
        AnnotatorHub.getInstance().getPipeline().annotate(annotation);

        extractNamedEntities();
        extractCoreferences();
        buildWordToLemmaMap();
        prepareParagraphs();
    }

    private String processDucDocument(String text){
        try{
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(new InputSource(new StringReader(text)));

            doc.getDocumentElement().normalize();
            Node rootNode = doc.getDocumentElement();
            Node headlineNode = doc.getDocumentElement().getElementsByTagName("HEADLINE").item(0);
            headline = headlineNode.getTextContent();
            Node textNode = doc.getElementsByTagName("TEXT").item(0);
            String documentText = textNode.getTextContent();
            return documentText;
        }catch(Exception e){
            e.printStackTrace();
        }
        return "";
    }

    private void extractNamedEntities(){
        this.namedEntities = new HashSet<>();

        for (CoreMap mention : annotation.get(CoreAnnotations.MentionsAnnotation.class)) {