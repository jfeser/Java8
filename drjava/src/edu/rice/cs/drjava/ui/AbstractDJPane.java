/*BEGIN_COPYRIGHT_BLOCK
 *
 * This file is part of DrJava.  Download the current version of this project:
 * http://sourceforge.net/projects/drjava/ or http://www.drjava.org/
 *
 * DrJava Open Source License
 *
 * Copyright (C) 2001-2003 JavaPLT group at Rice University (javaplt@rice.edu)
 * All rights reserved.
 *
 * Developed by:   Java Programming Languages Team
 *                 Rice University
 *                 http://www.cs.rice.edu/~javaplt/
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal with the Software without restriction, including without
 * limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to
 * whom the Software is furnished to do so, subject to the following
 * conditions:
 *
 *     - Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimers.
 *     - Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimers in the
 *       documentation and/or other materials provided with the distribution.
 *     - Neither the names of DrJava, the JavaPLT, Rice University, nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this Software without specific prior written permission.
 *     - Products derived from this software may not be called "DrJava" nor
 *       use the term "DrJava" as part of their names without prior written
 *       permission from the JavaPLT group.  For permission, write to
 *       javaplt@rice.edu.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE CONTRIBUTORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS WITH THE SOFTWARE.
 *
 END_COPYRIGHT_BLOCK*/

package edu.rice.cs.drjava.ui;

import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.drjava.config.*;
import edu.rice.cs.drjava.model.*;

import edu.rice.cs.util.swing.*;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;

public abstract class AbstractDJPane extends JTextPane implements OptionConstants {
  
  // ------------ FIELDS -----------
  
  /**
   * Paren/brace/bracket matching highlight color.
   */
  public static DefaultHighlighter.DefaultHighlightPainter
    MATCH_PAINTER;

  static {
    Color highColor = DrJava.getConfig().getSetting(DEFINITIONS_MATCH_COLOR);

    MATCH_PAINTER =
      new DefaultHighlighter.DefaultHighlightPainter(highColor);
  }
  
  /**
   * Highlight painter for selected errors in the defs doc.
   */
  public static DefaultHighlighter.DefaultHighlightPainter
    ERROR_PAINTER =
    new DefaultHighlighter.DefaultHighlightPainter(DrJava.getConfig().getSetting(COMPILER_ERROR_COLOR));
  
  
  protected HighlightManager _highlightManager;
  
  /**
   * Looks for changes in the caret position to see if a paren/brace/bracket
   * highlight is needed.
   */
  protected CaretListener _matchListener = new CaretListener() {
    /**
     * Checks caret position to see if it needs to set or remove a highlight
     * from the document.
     * When the cursor is immediately right of ')', '}', or ']', it highlights
     * up to the matching open paren/brace/bracket.
     * @param e the event fired by the caret position change
     */
    public void caretUpdate(CaretEvent e) {
      //_doc().setCurrentLocation(getCaretPosition());
      getDJDocument().setCurrentLocation(getCaretPosition());
      _removePreviousHighlight();
      _updateMatchHighlight();
//      DrJava.consoleErr().println(getPromptPos());
    }
  };
  
  
  /**
   * Our current paren/brace/bracket matching highlight.
   */
  protected HighlightManager.HighlightInfo _matchHighlight = null;
  
  protected final StyledDocument NULL_DOCUMENT = new DefaultStyledDocument();
  
  //--------- CONSTRUCTOR ----------
  AbstractDJPane(StyledDocument doc) {
    super(doc);
    setContentType("text/java");
  }
  
  //--------- METHODS -----------
    
  /**
   * Adds a highlight to the document.  Called by _updateMatchHighlight().
   * @param from start of highlight
   * @param to end of highlight
   */
  protected void _addHighlight(int from, int to) {
    _matchHighlight = _highlightManager.addHighlight(from, to, MATCH_PAINTER);
  }
  
  protected abstract void _updateMatchHighlight();

  /**
   * Removes the previous highlight so document is cleared when caret position changes.
   */
  protected void _removePreviousHighlight() {
    if (_matchHighlight != null) {
      _matchHighlight.remove();
      //_highlightManager.removeHighlight((HighlightManager.HighlightInfo)_matchHighlight);
      _matchHighlight = null;
    }
  }
  
  /**
   * Returns the DJDocument held by the pane
   */
  public abstract DJDocument getDJDocument();
   
}