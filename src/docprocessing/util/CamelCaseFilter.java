/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package docprocessing.util;

import java.io.IOException;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

/**
 * This class implements a filter that allows to break up multiword terms
 * on its individual components.
 * 
 * @author TelComp2.0 Development Team
 * @version 1.0.0, 07/31/2012
 */
public class CamelCaseFilter extends TokenFilter {

    private final CharTermAttribute _termAtt;

    protected CamelCaseFilter(TokenStream input) {
        super(input);
        this._termAtt = addAttribute(CharTermAttribute.class);
    }

    @Override
    public boolean incrementToken() throws IOException {
        if (!input.incrementToken())
            return false;
        CharTermAttribute a = this.getAttribute(CharTermAttribute.class);
        String spliettedString = splitCamelCase(a.toString());
        _termAtt.setEmpty();
        _termAtt.append(spliettedString);
        return true;

    }

    /**
     * This method receives a camel case string, and split it into its components  
     *  
     * @param s 
     */
    public static String splitCamelCase(String s) {
           return s.replaceAll(
              String.format("%s|%s|%s",
                 "(?<=[A-Z])(?=[A-Z][a-z])",
                 "(?<=[^A-Z])(?=[A-Z])",
                 "(?<=[A-Za-z])(?=[^A-Za-z])"
              ),
              " "
           ).replaceAll("[-_]", "").replaceAll("  ", " ");
     }
}