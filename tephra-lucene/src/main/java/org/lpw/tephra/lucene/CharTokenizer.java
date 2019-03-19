package org.lpw.tephra.lucene;

/**
 * @author lpw
 */
public class CharTokenizer extends org.apache.lucene.analysis.util.CharTokenizer {
    @Override
    protected boolean isTokenChar(int c) {
        return true;
    }
}
