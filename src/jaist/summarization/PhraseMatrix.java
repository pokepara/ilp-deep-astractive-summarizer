package jaist.summarization;

import jaist.summarization.unit.Phrase;

import java.util.HashMap;
import java.util.Set;

/**
 * Created by chientran on 10/6/15.
 */
public class PhraseMatrix {
    private HashMap<String, Object> matrix;

    public PhraseMatrix(){
        matrix = new HashMap<>();
    }

    public void setValue(Phrase nounPhrase, Phrase verbPhrase, Object value){
        matrix.put(buildKey(nounPhrase, verbPhrase), value);
        //matrix.put(buildKey(verbPhrase, nounPhrase), value);
    }

    public Object getValue(String key){
        return matrix.get(key);
    }

    public Object getValue