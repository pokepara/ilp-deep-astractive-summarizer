
package jaist.summarization;

import jaist.summarization.unit.Paragraph;
import jaist.summarization.unit.Phrase;

import java.util.*;

/**
 * Created by chientran on 9/29/15.
 */
public class PhraseScorer {
    InputDocument inputDocument = null;

    Double B = 6.0;
    Double RHO = 0.5d;

    private static final String PARAGRAPH_SPLIT_REGEX = "(?m)(?=^\\s{4})";
