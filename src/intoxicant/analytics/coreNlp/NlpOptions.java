
package intoxicant.analytics.coreNlp;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import java.util.Properties;

/**
 * User: jconwell
 * Date: 5/17/13
 * Helper class to setup the options for the Stanford NLP engine
 */
public class NlpOptions {

    /**
     * Create NlpOptions configuration class to create a NLP analyzer that only does term tokenization
     * @param lemmatisation flag to turn lemmatisation on / off during tokenization
     */
    public static NlpOptions tokenizationOnly(boolean lemmatisation) {
        return new NlpOptions(lemmatisation, false, false, false, false, -1, false);
    }

    /**
     * Create NlpOptions configuration class to create a NLP analyzer that does named entity recognition,
     * but does NOT run name disambiguation or coreference analysis
     */
    public static NlpOptions namedEntityRecognition(boolean regexNER, boolean sentenceParser) {
        return new NlpOptions(true, true, regexNER, sentenceParser, false, -1, false);
    }

    /**
     * Create NlpOptions configuration class to create a NLP analyzer that does named entity recognition
     * coreference analysis.
     * @param corefMaxSentenceDist max sentence distance to evaluate coreference between tokens
     * @param corefPostProcessing do post procesing of coreference data to trim out singletons
     */
    public static NlpOptions namedEntitiesWithCoreferenceAnalysis(boolean regexNER, int corefMaxSentenceDist, boolean corefPostProcessing) {
        return new NlpOptions(true, true, regexNER, true, true, corefMaxSentenceDist, corefPostProcessing);
    }

    /**
     * Create NlpOptions configuration class to create a NLP analyzer that does sentence parsing