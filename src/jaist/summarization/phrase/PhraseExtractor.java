
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