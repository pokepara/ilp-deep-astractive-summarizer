
package jaist.summarization;

import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.util.StringUtils;
import jaist.summarization.phrase.PhraseExtractor;
import jaist.summarization.unit.Phrase;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.*;
import gurobi.*;
import jaist.summarization.utils.ModelExporter;
import org.apache.commons.cli.*;

/**
 * Created by chientran on 9/28/15.
 */

public class Parser {
    Properties props = null;
    StanfordCoreNLP pipeline = null;
    PhraseMatrix indicatorMatrix = null;
    PhraseMatrix compatibilityMatrix = null;
    Integer[][] similarityMatrix = null;
    Integer[][] sentenceGenerationMatrix = null;
    List<InputDocument> docs = null;

    PhraseMatrix alternativeVPs = null;
    PhraseMatrix alternativeNPs = null;
    HashMap<String, HashSet<String>> corefs = null;

    List<Phrase> nounPhrases;
    List<Phrase> verbPhrases;
    List<Phrase> allPhrases;

    HashMap<Integer, GRBVar> nounVariables;
    HashMap<Integer, GRBVar> verbVariables;
    HashMap<String, GRBVar> gammaVariables;
    HashMap<String, GRBVar> nounToNounVariables;