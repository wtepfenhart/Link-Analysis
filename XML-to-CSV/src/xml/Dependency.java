package xml;

/**
 * A Dependency is a relationship between two Token objects in the same
 * Sentence. A Dependency is a subelement of a Sentence element in the XML
 * output of the CoreNLP. Each Dependency has a type which defines the
 * relationship between the two tokens. Governor and dependent Token objects
 * describes which Token is defining the relation on the other.
 *
 * @author Todd Qualiano
 */
public class Dependency {

    private int sentID;
    private String type;
    private Token governor;
    private Token dependent;

    public Dependency(int sentID, String type, Token governor, Token dependent) {
        this.sentID = sentID;
        this.type = type;
        this.governor = governor;
        this.dependent = dependent;
    }

    /**
     * @return the dependent
     */
    public Token getDependent() {
        return dependent;
    }

    /**
     * @return the governor
     */
    public Token getGovernor() {
        return governor;
    }

    /**
     * @return the sentID
     */
    public int getSentID() {
        return sentID;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param dependent the dependent to set
     */
    public void setDependent(Token dependent) {
        this.dependent = dependent;
    }

    /**
     * @param governor the governor to set
     */
    public void setGovernor(Token governor) {
        this.governor = governor;
    }

    /**
     * @param sentID the sentID to set
     */
    public void setSentID(int sentID) {
        this.sentID = sentID;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }
}
