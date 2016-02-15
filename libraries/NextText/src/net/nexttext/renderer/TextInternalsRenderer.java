/*
  This file is part of the NextText project.
  http://www.nexttext.net/

  Copyright (c) 2004-08 Obx Labs / Jason Lewis

  NextText is free software: you can redistribute it and/or modify it under
  the terms of the GNU General Public License as published by the Free Software 
  Foundation, either version 2 of the License, or (at your option) any later 
  version.

  NextText is distributed in the hope that it will be useful, but WITHOUT ANY
  WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR 
  A PARTICULAR PURPOSE.  See the GNU General Public License for more details.

  You should have received a copy of the GNU General Public License along with 
  NextText.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.nexttext.renderer;

import java.util.Iterator;
import net.nexttext.*;

/**
 * This renderer prints out the internals of the TextPage tree to the console.
 * 
 * <p>It is useful for debugging when you want to know what's going on.</p>
 */
/* $Id$ */
public class TextInternalsRenderer extends TextPageRenderer {
    public TextInternalsRenderer() {
        super(null);
    }

    public void renderPage(TextPage textPage) {
        processNode(textPage.getTextRoot());
    }

    // Rendering is done recursively. A prefix is maintained so that objects
    // are indented further deeper in the tree.

    String prefix = "";

    void processNode(TextObject node) {
        if (node == null)
            return;
        System.out.print(prefix);
        if (node instanceof TextObjectGlyph) {
            System.out
                    .println("<glyph> " + ((TextObjectGlyph) node).getGlyph());
            processProperties(node);
        } else {
            System.out.println("<node>");
            processProperties(node);
            String oldPrefix = prefix;
            prefix = prefix + "  ";
            processNode(((TextObjectGroup) node).getLeftMostChild());
            prefix = oldPrefix;
        }
        processNode(node.getRightSibling());
    }

    void processProperties(TextObject node) {
        String pp = prefix + "    ";
        Iterator<String> i = node.getPropertyNames().iterator();
        while (i.hasNext()) {
            String name = (String) i.next();
            System.out.println(pp + name + " = " + node.getProperty(name));
        }
    }

}
