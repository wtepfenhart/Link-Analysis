package entity;

/**
 * A NamedEntity is an object that the NLP recognizes as a proper noun.  We base the
 * idea off of the NER subelement of the token element in the XML output of the
 * NLP. Although NER is attributed to a single token, the author wrote a method () to
 * recognize a sequence of tokens with the same NER value as a singular entity.
 * There are three categories defined: PERSON, LOCATION, and ORGANIZATION.
 * The author is debating whether or not to declare an NER enum to represent the
 * 
 * @author Todd Qualiano
 */

public class NamedEntity {
    private String name;
    private String ner;
    
    
    public NamedEntity(String name, String ner){
        this.name = name;
        this.ner = ner;
    }
    
    /**
     * Returns the data of this NamedEntity as a String is properly formatted
     * for a CSV file to import into Neo4j.
     * @return 
     */
    public String formatAsCSVLine(){
        return "\"" + this.ner + "\"," + "\"" + this.name + "\"\n";
    }
    
    public String getNER(){
        return this.ner;
    }
}
