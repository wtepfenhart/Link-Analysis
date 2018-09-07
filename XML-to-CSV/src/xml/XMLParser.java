package xml;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * This class is designed to parse an XML file (which was the output of a text
 * file being input into the Core NLP) and create objects based on the parsing.
 *
 * The method names represent which elements are being extracted and created.
 * 
 * @author Todd Qualiano
 */
public class XMLParser {

    private final Document document;
    private final List<Token> tokens;

    public XMLParser(File f) {
        document = this.makeDocument(f);
        tokens = this.parseFileTokens();
    }

    private Document makeDocument(File f) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(f);
            doc.getDocumentElement().normalize();
            return doc;
        } catch (IOException | ParserConfigurationException | SAXException e) {
            e.getMessage();
            return null;
        }
    }

    private List<Token> parseFileTokens() {
        List<Token> retVal = new LinkedList<>();

        NodeList tempSentenceList = document.getElementsByTagName("sentences");
        Node tempSentence = tempSentenceList.item(0);
        Element tempElement = (Element) tempSentence;
        NodeList sentenceList = tempElement.getElementsByTagName("sentence");

        for (int i = 0; i < sentenceList.getLength(); i++) {
            Node sentence = sentenceList.item(i);
            if (sentence.getNodeType() == Node.ELEMENT_NODE) {

                Element sentenceI = (Element) sentence;
                int sentID = Integer.parseInt(sentenceI.getAttribute("id"));

                NodeList tokenz = sentenceI.getElementsByTagName("token");

                for (int j = 0; j < tokenz.getLength(); j++) {
                    Node token = tokenz.item(j);
                    if (token.getNodeType() == Node.ELEMENT_NODE) {
                        Element tokenJ = (Element) token;
                        Token t = new Token(
                                sentID,
                                Integer.parseInt(tokenJ.getAttribute("id")),
                                tokenJ.getElementsByTagName("word").item(0).getTextContent(),
                                tokenJ.getElementsByTagName("lemma").item(0).getTextContent(),
                                tokenJ.getElementsByTagName("POS").item(0).getTextContent(),
                                tokenJ.getElementsByTagName("NER").item(0).getTextContent()
                        );
                        retVal.add(t);
                    }
                }

            }
        }
        return retVal;
    }

    public List<Token> getTokens() {
        return this.tokens;
    }

    public List<Sentence> parseFileSentences() {
        List<Sentence> ret = new LinkedList<>();

        NodeList tempSentenceList = document.getElementsByTagName("sentences");
        Node tempSentence = tempSentenceList.item(0);
        Element tempElement = (Element) tempSentence;
        NodeList sentenceList = tempElement.getElementsByTagName("sentence");

        for (int i = 0; i < sentenceList.getLength(); i++) {
            Node sentence = sentenceList.item(i);
            System.out.println("\nCurrent Element: " + sentence.getNodeName());
            if (sentence.getNodeType() == Node.ELEMENT_NODE) {

                Element sentenceI = (Element) sentence;
                List<Dependency> dependencies = parseDependencies(sentenceI, "collapsed-ccprocessed-dependencies");

                int sentID = Integer.parseInt(sentenceI.getAttribute("id"));

                NodeList parseList = sentenceI.getElementsByTagName("parse");
                Node parseNode = parseList.item(0);
                String parse = parseNode.getTextContent();

                List<Token> sentTokens = getTokensBySentence(sentID);

                Sentence newSentence = new Sentence(sentID, parse, dependencies, sentTokens);

                ret.add(newSentence);
            }
        }
        return ret;
    }

    public Token getToken(int sentID, int id) {
        if (id == 0) {
            return new Token(sentID, 0, "ROOT", null, null, null);
        }
        for (Token t : tokens) {
            if (t.getSentID() == sentID && t.getId() == id) {
                return t;
            }
        }

        return null;
    }

    public List<Token> getTokensBySentence(int sentID) {
        List<Token> ret = new LinkedList<>();
        tokens.stream().filter((t) -> (t.getSentID() == sentID)).forEachOrdered((t) -> {
            ret.add(t);
        });
        return ret;
    }

    /**
     *
     * @param sentence
     * @param type what type of dependency type we want to work with
     * @return
     */
    public List<Dependency> parseDependencies(Element sentence, String type) {
        List<Dependency> ret = new LinkedList<>();

        int sentID = Integer.parseInt(sentence.getAttribute("id"));
        NodeList depList = sentence.getElementsByTagName("dependencies");
        Node dependents;
        switch (type) {
            case "basic-dependencies": {
                dependents = depList.item(0);
                break;
            }
            case "collapsed-dependencies": {
                dependents = depList.item(1);
                break;
            }
            case "collapsed-ccprocessed-dependencies": {
                dependents = depList.item(2);
                break;
            }
            default: {
                throw new RuntimeException("NO DEPENDENCY TYPE GIVEN");
            }
        }
        Element dependency = (Element) dependents;
        NodeList dependencies = dependency.getElementsByTagName("dep");

        for (int i = 0; i < dependencies.getLength(); i++) {
            Node dep = dependencies.item(i);
            Element eDep = (Element) dep;

            Token governor, dependent;

            NodeList govList = eDep.getElementsByTagName("governor");
            Node govNode = govList.item(0);
            Element eGov = (Element) govNode;

            NodeList dependentList = eDep.getElementsByTagName("dependent");
            Node dependentNode = dependentList.item(0);
            Element eDependent = (Element) dependentNode;

            int govID = Integer.parseInt(eGov.getAttribute("idx"));
            governor = getToken(sentID, govID);

            int dependentID = Integer.parseInt(eDependent.getAttribute("idx"));
            dependent = getToken(sentID, dependentID);

            Dependency toAdd = new Dependency(
                    sentID,
                    eDep.getAttribute("type"),
                    governor,
                    dependent);

            governor.addGovernor(toAdd);
            dependent.addDependent(toAdd);

            ret.add(toAdd);

            //get the governor and dependent tokens
        }
        return ret;
    }

    public void parseCoreferences() {
        NodeList rootCoreferenceList = document.getElementsByTagName("coreference");
        Node rootCoreferenceNode = rootCoreferenceList.item(0);
        Element rootCoreferenceElement = (Element) rootCoreferenceNode;

        NodeList coreferenceList = rootCoreferenceElement.getElementsByTagName("coreference");
        for (int i = 0; i < coreferenceList.getLength(); i++) {
            Node coreferenceNode = coreferenceList.item(i);
            Element coreferenceElement = (Element) coreferenceNode;
            NodeList mentionList = coreferenceElement.getElementsByTagName("mention");

            Coreference coreference = new Coreference();

            for (int j = 0; j < mentionList.getLength(); j++) {
                Node mentionNode = mentionList.item(j);
                Element eMention = (Element) mentionNode;
                boolean isRepresentative = false;

                if (eMention.hasAttribute("representative")) {
                    if (eMention.getAttribute("representative").equals("true")) {
                        isRepresentative = true;
                    }
                }
                Mention mention = new Mention(isRepresentative,
                        Integer.parseInt(eMention.getElementsByTagName("sentence").item(0).getTextContent()),
                        Integer.parseInt(eMention.getElementsByTagName("start").item(0).getTextContent()),
                        Integer.parseInt(eMention.getElementsByTagName("end").item(0).getTextContent()),
                        Integer.parseInt(eMention.getElementsByTagName("head").item(0).getTextContent()),
                        eMention.getElementsByTagName("text").item(0).getTextContent());

                if (mention.isRep()) {
                    coreference.setRepresentative(mention);
                } else {
                    coreference.addToReplace(mention);
                }
            }
        }

    }

    //ONLY USED FOR TESTING/DEBUGGING METHODS
    /*
    public static void main(String[] args) {
        File file = new File("C:\\Users\\ta03q\\Documents\\Research\\Barack_Obama\\Barack_Obama\\xml_paragraphs\\Barack_Obama-P5.xml");
        XMLParser parser = new XMLParser(file);
        List<Sentence> sentences = parser.parseFileSentences();
        for (Sentence s : sentences) {

        }
    }
     */
}
