/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package docprocessing.util.suffixtree;

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

public class CompactSuffixTree extends AbstractSuffixTree {

    public CompactSuffixTree(SimpleSuffixTree simpleSuffixTree) {
        super(simpleSuffixTree.text);
        super.root = compactNodes(simpleSuffixTree.root, 0);
        super.best = simpleSuffixTree.best;
    }

    private SuffixTreeNode compactNodes(SuffixTreeNode node, int nodeDepth) {
        node.nodeDepth = nodeDepth;
        for (SuffixTreeNode child : node.children) {
            while (child.children.size() == 1) {
                SuffixTreeNode grandchild = child.children.iterator().next();
                child.incomingEdge.label += ", "
                        + grandchild.incomingEdge.label;
                child.stringDepth += grandchild.incomingEdge.label.length();
                child.children = grandchild.children;
                // for (SuffixTreeNode grandchild : child.children)
                grandchild.parent = node;
            }
            child = compactNodes(child, nodeDepth + 1);
        }
        return node;
    }

}
