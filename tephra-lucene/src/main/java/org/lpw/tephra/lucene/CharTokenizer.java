package org.lpw.tephra.lucene;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;

/**
 * @author lpw
 */
public final class CharTokenizer extends Tokenizer {
    private final CharTermAttribute charTermAttribute = addAttribute(CharTermAttribute.class);

    @Override
    public boolean incrementToken() throws IOException {
        clearAttributes();
        int n = input.read();
        if (n == -1 || Character.isWhitespace(n))
            return false;

        charTermAttribute.append((char) n);
        charTermAttribute.setLength(1);

        return true;
    }
}
