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
    private String 