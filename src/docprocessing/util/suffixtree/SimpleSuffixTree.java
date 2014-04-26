/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package docprocessing.util.suffixtree;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Leandro Ordonez <leandro.ordonez.ante@gmail.com>
 */

/* Copyright (c) 2010 the authors listed at the following URL, and/or
 the authors of referenced articles or incorporated external code:
 http://en.literateprograms.org/Suffix_tree_(Java)?action=history&offset=20100123220137

 Permission is hereby granted, free of charge, to any person obtaining
 a copy of this software and associated documentation files (the
 "Software"), to deal in the Software without restriction, including
 without limitation the rights to use, copy, modify, merge, publish,
 distribute, sublicense, and/or sell copies of the Software, and to
 permit persons to whom the Software is furnished to do so, subject to
 the following conditions:

 The above copyright notice and this permission notice shall be
 included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

 Retrieved from: http://en.literateprograms.org/Suffix_tree_(Java)?oldid=16641
 */

public class SimpleSuffixTree extends AbstractSuffixTree {

    public SimpleSuffixTree(String text) {
        super(text);
        constructTree();
    }

    private void constructTree() {
        super.root = new SuffixTreeNode(this);
        best = root;
        bestNodes.add(0, best);
        char[] s = super.text.toCharArray();
        for (int i = 0; i < s.length; i++) {
            List<String> suffixList = new ArrayList<String>();
            for (int k = i; k < s.length; k++) {
                suffixList.add(s[k] + "");
            }
            super.root.addSuffix(suffixList, i + 1);
        }
    }

}
