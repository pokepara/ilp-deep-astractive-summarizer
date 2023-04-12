
package jaist.summarization.phrase;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import jaist.summarization.*;
import jaist.summarization.unit.Phrase;
import jaist.summarization.utils.StringUtils;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;

import java.io.Reader;
import java.io.StringReader;
import java.util.*;

public class PhraseExtractor {
    private static String PARSER_MODEL = "edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz";
    private static Integer sentenceId = 0;
    private InputDocument inputDocument;
    private PhraseMatrix indicatorMatrix;
    private HashSet<String> namedEntities;


    public PhraseExtractor(InputDocument inputDocument, PhraseMatrix indicatorMatrix){
        this.inputDocument = inputDocument;
        this.indicatorMatrix = indicatorMatrix;
    }

    public List<Phrase> extractAllPhrases() {
        List<Phrase> allPhrases = new ArrayList<>();

        List<CoreMap> sentences = inputDocument.getSentences();
        List<Thread> threads = new ArrayList<>();

        for (CoreMap sentence : sentences) {
            List<Phrase> phrasesInSentence = extractPhrasesFromSentence(sentence);

            allPhrases.addAll(phrasesInSentence);
            for (int i=0; i<phrasesInSentence.size()-1; i++){
                for (int j=i+1; j<phrasesInSentence.size(); j++){
                    Phrase a = phrasesInSentence.get(i);
                    Phrase b = phrasesInSentence.get(j);
                    if (a.isNP() && !b.isNP() && a.getSentenceNodeId() == b.getSentenceNodeId()){
                        indicatorMatrix.setValue(a, b, 1);
                    }
                }
            }
        }

        return allPhrases;
    }

    //Use this if we want a seperate LexicializedParser instead of ParserAnnotation
    private List<Phrase> extractPhrasesWithLexicalParser(String text){
        LexicalizedParser lexicalizedParser = LexicalizedParser.loadModel(PARSER_MODEL);
        List<Phrase> allPhrases = new ArrayList<>();

        Reader reader = new StringReader(text);
        DocumentPreprocessor dp = new DocumentPreprocessor(reader);
        for (List<HasWord> sentence : dp) {
            Tree tree = lexicalizedParser.parse(sentence);
            // ignore the root node
            tree = tree.children()[0];
            allPhrases.addAll(extractSentenceNode(tree, sentence.size()));
        }

        return allPhrases;
    }

    private String getPhrase(Tree tree) {
        List<String> words = new ArrayList<String>();
        List<Tree> leaves = tree.getLeaves();
        for (Tree leaf : leaves) {
            words.add(leaf.value());
        }
