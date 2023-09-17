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
import javax.x