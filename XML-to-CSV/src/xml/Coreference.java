package xml;

import java.util.LinkedList;
import java.util.List;

/**
 * A Coreference identifies equivalent entities between nouns within a file
 * analyzed by the NLP. A Coreference contains a representitive Mention and a
 * list of Mention objects which are understood as being equal to the
 * representative.
 *
 * @author Todd Qualiano
 */
public class Coreference {

    private Mention representative;
    private List<Mention> toReplace;

    public Coreference() {
        toReplace = new LinkedList<>();
    }

    public void setRepresentative(Mention m) {
        this.representative = m;
    }

    public void addToReplace(Mention m) {
        this.toReplace.add(m);
    }

}
