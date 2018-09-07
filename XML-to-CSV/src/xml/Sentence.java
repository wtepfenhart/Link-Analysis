package xml;

import java.util.List;

/**
 * A representative of a Sentence in a given file analyzed by the Core NLP.
 *
 * @author Todd Qualiano
 */
public class Sentence {

    int sentenceID;
    String parse;
    List<Dependency> dependencies;
    List<Token> tokens;

    public Sentence(int sentenceID, String parse, List<Dependency> dependencies, List<Token> tokens) {
        this.sentenceID = sentenceID;
        this.parse = parse;
        this.dependencies = dependencies;
        this.tokens = tokens;
    }
}
