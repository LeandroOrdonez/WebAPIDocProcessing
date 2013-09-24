/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package docprocessing.util;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Leandro Ordonez <leandro.ordonez.ante@gmail.com>
 */
public class POSTagger {
    // Private constructor prevents instantiation from other classes

    public static MaxentTagger TAGGER;

    private POSTagger() {
        try {
            if (TAGGER == null) {
                Properties config = new Properties();
                config.load(POSTagger.class.getClassLoader().getResourceAsStream("./config/config.properties"));
                TAGGER = new MaxentTagger(config.getProperty("pos_tagger"));
            }
        } catch (IOException ex) {
            Logger.getLogger(POSTagger.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * SingletonHolder is loaded on the first execution of
     * Singleton.getInstance() or the first access to SingletonHolder.INSTANCE,
     * not before.
     */
    private static class SingletonHolder {

        public static final POSTagger INSTANCE = new POSTagger();
    }

    public static POSTagger getInstance() {
        return SingletonHolder.INSTANCE;
    }
    
    /**
     *
     * @param toTag
     * @return
     */
    public String tagString(String toTag) {
        return TAGGER.tagString(toTag);
    }
    
}
