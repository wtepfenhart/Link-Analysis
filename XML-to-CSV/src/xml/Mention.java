package xml;

/**
 * A Mention is a subelement of a Coreference that represents an entity that is
 * referenced by another Token in the same file that is analyzed by the CoreNLP.
 *
 * @author Todd Qualiano
 */
public class Mention {

    private boolean isRepresentitive;
    private int sentID;
    private int start;
    private int end;
    private int head;
    private String text;

    public Mention(
            boolean isRepresentitive,
            int sentID,
            int start,
            int end,
            int head,
            String text) {
        this.isRepresentitive = isRepresentitive;
        this.sentID = sentID;
        this.start = start;
        this.end = end;
        this.head = head;
        this.text = text;
    }

    public boolean isRep() {
        return this.isRepresentitive;
    }
}
