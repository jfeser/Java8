/*BEGIN_COPYRIGHT_BLOCK
 *
 * This file is a part of DrJava. Current versions of this project are available
 * at http://sourceforge.net/projects/drjava
 *
 * Copyright (C) 2001-2008 JavaPLT group at Rice University (drjava@rice.edu)
 * 
 * DrJava is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * DrJava is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * or see http://www.gnu.org/licenses/gpl.html
 *
 * In addition, as a special exception, the JavaPLT group at Rice University
 * (drjava@rice.edu) gives permission to link the code of DrJava with
 * the classes in the gj.util package, even if they are provided in binary-only
 * form, and distribute linked combinations including the DrJava and the
 * gj.util package. You must obey the GNU General Public License in all
 * respects for all of the code used other than these classes in the gj.util
 * package: Dictionary, HashtableEntry, ValueEnumerator, Enumeration,
 * KeyEnumerator, Vector, Hashtable, Stack, VectorEnumerator.
 *
 * If you modify this file, you may extend this exception to your version of the
 * file, but you are not obligated to do so. If you do not wish to
 * do so, delete this exception statement from your version. (However, the
 * present version of DrJava depends on these classes, so you'd want to
 * remove the dependency first!)
 *
 END_COPYRIGHT_BLOCK*/

package edu.rice.cs.drjava.eclipse;

/**
 * A collection of preference names relevant to the DrJava Plug-in for Eclipse.
 * This file is a blatant duplication of portions of OptionConstants, which
 * cannot be used in Eclipse because of generics and our custom configuration
 * framework.
 * 
 * @version $Id: DrJavaConstants.java 4314 2008-01-30 00:08:33Z mgricken $
 */
public interface DrJavaConstants {
  
  /**
   * The font used in the interactions pane.
   * (This is managed by the FontRegistry and an extension point in plugin.xml,
   * unlike the other preferences.)
   */
  public static final String FONT_MAIN  = "edu.rice.cs.drjava.InteractionsFont";
  
  /**
   * Whether to prompt before resetting the interactions pane.
   */
  public static final String INTERACTIONS_RESET_PROMPT =
    "interactions.reset.prompt";
  
  /**
   * Whether to allow users to access to all members in the Interactions Pane.
   */
  public static final String ALLOW_PRIVATE_ACCESS = "allow.private.access";

  /**
   * Whether to prompt when the interactions pane is unexpectedly reset.
   */
  public static final String INTERACTIONS_EXIT_PROMPT =
    "interactions.exit.prompt";
  
  /**
   * Number of lines to remember in the Interactions History.
   */
  public static final String HISTORY_MAX_SIZE = "history.max.size";

  /**
   * Optional arguments to the interpreter JVM.
   */
  public static final String JVM_ARGS = "jvm.args";
}
