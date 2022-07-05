
package jaist.summarization;

import edu.stanford.nlp.hcoref.data.InputDoc;
import edu.stanford.nlp.io.IOUtils;
import jaist.summarization.phrase.PhraseExtractor;
import jaist.summarization.unit.Phrase;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by chientran on 3/15/16.
 */
public class DocumentProcessor {
    private boolean isDucData;
    private HashMap<String, HashSet<String>> corefs = null;

    private List<InputDocument> docs;
    private PhraseMatrix indicatorMatrix;

    List<Phrase> nounPhrases;
    List<Phrase> verbPhrases;
    List<Phrase> allPhrases;

    HashSet<String> nouns;
    HashSet<String> verbs;

    public DocumentProcessor(boolean isDucData, PhraseMatrix indicatorMatrix){
        this.isDucData = isDucData;
        this.docs = new ArrayList<>();
        this.indicatorMatrix = indicatorMatrix;
        this.nounPhrases = new ArrayList<>();
        this.verbPhrases = new ArrayList<>();
        this.allPhrases = new ArrayList<>();

        this.nouns = new HashSet<>();
        this.verbs = new HashSet<>();
        this.corefs = new HashMap<>();
    }

    public void processDocuments(File[] fileNames) throws IOException{