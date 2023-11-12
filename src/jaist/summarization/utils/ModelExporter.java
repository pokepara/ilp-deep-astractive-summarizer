package jaist.summarization.utils;

import jaist.summarization.InputDocument;
import jaist.summarization.PhraseMatrix;
import jaist.summarization.unit.Paragraph;
import jaist.summarization.unit.Phrase;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by chientran on 3/15/16.
 */
public class ModelExporter {
    private String documentSetName;
    private String parentFolder;
    public ModelExporter(String statFolder, String documentSetName){
        this.parentFolder = statFolder + "/" + documentSetName + "/";
        File folder = new File(parentFolder);
        if (!folder.exists()){
            folder.mkdirs();
        }
    }

    public void savePhrasesToFile(List<Phrase> phrases){
        try {
            Document doc = generateXmlDocument();
            Element rootElement = doc.createElement("phrases");
            doc.appendChild(rootElement);

            for (Phrase phrase: phrases){
                Element phraseElement = doc.createElement("phrase");
                rootElement.appendChild(phraseElement);

                phraseElement.setAttribute("id", phrase.getId().toString());
                phraseElement.setAttribute("parentId", phrase.getParentId().toString());
                phraseElement.setAttribute("type", phrase.isNP() ? "NP" : "VP");
                phraseElement.setAttribute("sentenceLength", phrase.getSentenceLength().toString());
                phraseElement.setAttribute("length", phrase.getWordLength().toString());

                Element contentElement = doc.createElement("content");
                phraseElement.appendChild(contentElement);
                contentElement.setTextContent(phrase.getContent());

                Element conceptsElement = doc.createElement("concept