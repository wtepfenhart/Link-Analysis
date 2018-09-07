package xml;

import java.util.LinkedList;
import java.util.List;

/**
 * Abstract representation of a Token in an XML output for the Stanford Core
 * NLP.
 *
 * @author ta03q
 */
public class Token {

    private int sentID;
    private int id;
    private String word;
    private String lemma;
    private String pos;
    private String ner;
    private List<Dependency> governor;
    private List<Dependency> dependent;
    private Token representitive;

    public Token(int sentID, int id, String word, String lemma, String pos, String ner) {
        this.sentID = sentID;
        this.id = id;
        this.word = word;
        this.lemma = lemma;
        this.pos = pos;
        this.ner = ner;
        this.governor = new LinkedList<>();
        this.dependent = new LinkedList<>();
    }

    public void addGovernor(Dependency d) {
        governor.add(d);
    }

    public void addDependent(Dependency d) {
        dependent.add(d);
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @return the lemma
     */
    public String getLemma() {
        return lemma;
    }

    /**
     * @return the ner
     */
    public String getNer() {
        return ner;
    }

    /**
     * @return the pos
     */
    public String getPos() {
        return pos;
    }

    /**
     * @return the word
     */
    public String getWord() {
        return word;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @param lemma the lemma to set
     */
    public void setLemma(String lemma) {
        this.lemma = lemma;
    }

    /**
     * @param ner the ner to set
     */
    public void setNer(String ner) {
        this.ner = ner;
    }

    /**
     * @param pos the pos to set
     */
    public void setPos(String pos) {
        this.pos = pos;
    }

    /**
     * @param word the word to set
     */
    public void setWord(String word) {
        this.word = word;
    }

    /**
     * @return the sentID
     */
    public int getSentID() {
        return sentID;
    }

    /**
     * @param sentID the sentID to set
     */
    public void setSentID(int sentID) {
        this.sentID = sentID;
    }

}
