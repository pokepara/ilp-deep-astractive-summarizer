
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
    HashMap<String, GRBVar> verbToVerbVariables;

    HashSet<String> nouns;
    HashSet<String> verbs;

    DocumentProcessor processor;

    static int DEFAULT_MAXIMUM_SENTENCE = 10;
    static double DEFAULT_ALTERNATIVE_VP_THRESHOLD = 0.75;
    static int DEFAULT_MAX_WORD_LENGTH = 100;
    static int MIN_SENTENCE_LENGTH = 5;
    static int MINIMUM_VERB_LENGTH = 2;

    int max_sentence = 10;
    double alternative_vp_threshold = 0.75;
    int max_word_length = 100;

    int threads = 0;

    long previousMarkedTime;

    public Parser(int max_sentence, double alternative_vp_threshold, int max_word_length, int threads, boolean isDucData){
        this(max_sentence, alternative_vp_threshold, max_word_length, isDucData);
        this.threads = threads;
    }

    public Parser(int max_sentence, double alternative_vp_threshold, int max_word_length, boolean isDucData) {
        this.max_sentence = max_sentence;
        this.alternative_vp_threshold = alternative_vp_threshold;
        this.max_word_length = max_word_length;

        this.props = new Properties();
        pipeline = AnnotatorHub.getInstance().getPipeline();
        indicatorMatrix = new PhraseMatrix();
        compatibilityMatrix = new PhraseMatrix();
        alternativeVPs = new PhraseMatrix();
        alternativeNPs = new PhraseMatrix();

        nounPhrases = new ArrayList<>();
        verbPhrases = new ArrayList<>();
        allPhrases = new ArrayList<>();
        corefs = new HashMap<>();

        nouns = new HashSet<>();
        verbs = new HashSet<>();

        docs = new ArrayList<>();

        processor = new DocumentProcessor(isDucData, indicatorMatrix);
    }

    public Parser(){
        this(DEFAULT_MAXIMUM_SENTENCE, DEFAULT_ALTERNATIVE_VP_THRESHOLD, DEFAULT_MAX_WORD_LENGTH, false);
    }

    public Parser(int max_words){
        this();
        this.max_word_length = max_words;
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Start at: " + System.currentTimeMillis());
        Options options = new Options();

        options.addOption("help", false, "print command usage");
        options.addOption("word_length", true, "maximum word length");
        options.addOption("vp_threshold", true, "Alternative VP threshold");
        options.addOption("max_sent", true, "maximum # of sentences");
        options.addOption("in", true, "input folder containing all text files");
        options.addOption("out", true, "Output file");
        options.addOption("threads", true, "Number of threads");
        options.addOption("duc", false, "Is DUC data");
        options.addOption("export_only", false, "Should we find the solution or just export the phrases?");

        CommandLineParser commandLineParser = new DefaultParser();
        CommandLine cmd = commandLineParser.parse(options, args);

        int word_length = DEFAULT_MAX_WORD_LENGTH;

        if (cmd.hasOption("help")){
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp( "usage", options );
            return;
        }

        if (cmd.hasOption("word_length")){
            word_length = Integer.parseInt(cmd.getOptionValue("word_length"));
        }

        int sentence_length = DEFAULT_MAXIMUM_SENTENCE;
        if (cmd.hasOption("max_sent")){
            sentence_length = Integer.parseInt(cmd.getOptionValue("max_sent"));
        }

        double vp_threshold = DEFAULT_ALTERNATIVE_VP_THRESHOLD;
        if (cmd.hasOption("vp_threshold")){
            vp_threshold = Double.parseDouble(cmd.getOptionValue("vp_threshold"));
        }

        int threads = 0;
        if (cmd.hasOption("threads")){
            threads = Integer.parseInt(cmd.getOptionValue("threads"));
        }

        boolean isDucData = cmd.hasOption("duc");
        boolean isExportOnly = cmd.hasOption("export_only");

        String[] folders = cmd.getOptionValue("in").split(",");

        for (String folderName: folders){
            File folder = new File(folderName);

            String outputFilename = folder.getName();

            File[] fileNames = null;

            if (folder.isDirectory()){
                fileNames = folder.listFiles();
            }else{
                outputFilename = folder.getParent().substring(folder.getParent().lastIndexOf("/")+1);
                System.out.println("Single document summarization. For multi-doc summarization, please specify a folder");
                fileNames = new File[]{folder};
            }

            Parser parser = new Parser(sentence_length, vp_threshold, word_length, threads, isDucData);
            System.out.println("Stanford CoreNLP loaded at " + System.currentTimeMillis());
            for (File filepath: fileNames){
                if (filepath.getName().startsWith(".")) continue;
                System.out.println(filepath.getAbsolutePath());

                String text = IOUtils.slurpFile(filepath);
                parser.processDocument(text);
            }

            parser.updateModel();

            parser.saveDataToFiles(outputFilename);

            if (isExportOnly){
                continue;
            }

            String summary = parser.generateSummary();

            PrintWriter out = null;
            try {
                String summaryFolderName = "summary_results";
                File summaryFolder = new File(summaryFolderName);
                if (!summaryFolder.exists()){
                    summaryFolder.mkdir();
                }

                out = new PrintWriter(summaryFolderName + "/" + outputFilename + "_system.txt");
                out.print(summary);
            } catch (FileNotFoundException ex) {
                System.out.println(ex.getMessage());
            } finally {
                out.close();
            }
        }

    }

    public void processDocument(String text){
        processor.processDocument(text);
    }

    public void processDocuments(File[] files, boolean isDucData){
        DocumentProcessor processor = new DocumentProcessor(isDucData, indicatorMatrix);
        try {
            processor.processDocuments(files);
            nounPhrases = processor.getNounPhrases();
            verbPhrases = processor.getVerbPhrases();
            allPhrases = processor.getAllPhrases();
            corefs = processor.getCorefs();
            nouns = processor.getNouns();
            verbs = processor.getVerbs();
            docs = processor.getDocs();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void updateModel(){
        nounPhrases = processor.getNounPhrases();
        verbPhrases = processor.getVerbPhrases();
        allPhrases = processor.getAllPhrases();
        corefs = processor.getCorefs();
        nouns = processor.getNouns();
        verbs = processor.getVerbs();
        docs = processor.getDocs();
    }

    public void saveDataToFiles(String documentSetName){
        String statFolderName = "stats";
        File statFolder = new File(statFolderName);
        if (!statFolder.exists()){
            statFolder.mkdir();
        }

        ModelExporter exporter = new ModelExporter(statFolderName, documentSetName);
        exporter.savePhrasesToFile(allPhrases);
        exporter.saveCoreferencesToFile(corefs);
        exporter.saveIndicatorMatrixToFile(indicatorMatrix);
        exporter.saveParagraphsToFile(docs);
    }

    public String generateSummary(){
        System.out.println("Start scoring at " + System.currentTimeMillis());
        scorePhrases();
        System.out.println("Finish scoring at " + System.currentTimeMillis());

        printLog();

        return findOptimalSolution();
    }

    private void buildCompatibilityMatrix() {
        int npLength = this.nounPhrases.size();
        int vpLength = this.verbPhrases.size();

        for (int p = 0; p < npLength; p++) {
            for (int q = 0; q < vpLength; q++) {
                Phrase noun = nounPhrases.get(p);
                Phrase verb = verbPhrases.get(q);

                int related = 0;

                for (int i = 0; i < npLength; i++) {
                    Phrase otherNoun = nounPhrases.get(i);

                    if (alternativeNPs.exists(noun, otherNoun) && indicatorMatrix.exists(otherNoun, verb)) {
                        related = 1;
                        break;
                    }
                }

                if (related == 0){
                    for (int i=0; i<vpLength; i++){
                        Phrase otherVerb = verbPhrases.get(i);

                        if (alternativeVPs.exists(verb, otherVerb) && indicatorMatrix.exists(noun, otherVerb)){
                            related = 1;
                            break;
                        }
                    }
                }

                if (related == 0 && indicatorMatrix.exists(noun, verb)){
                    related = 1;
                }
                compatibilityMatrix.setValue(this.nounPhrases.get(p), this.verbPhrases.get(q), related);
            }
        }
    }

    private String startOptimization() throws GRBException{
        log("Start building optimization model");