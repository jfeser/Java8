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

package edu.rice.cs.util.jar;

import junit.framework.TestCase;
import java.util.jar.Manifest;
import java.util.jar.Attributes;
import java.util.jar.JarInputStream;
import java.util.jar.JarEntry;
import java.util.*;
import java.io.*;

public class JarCreationTest extends TestCase {
  /**
   * Tests the creation of manifest files through the ManifestWriter class
   */
  public void testCreateManifest() {
    ManifestWriter mw = new ManifestWriter();
    Manifest manifest = mw.getManifest();
    assertTrue("should have version attribute", manifest.getMainAttributes().containsKey(Attributes.Name.MANIFEST_VERSION));
    assertEquals("should have version attribute", "1.0", manifest.getMainAttributes().get(Attributes.Name.MANIFEST_VERSION));
    assertEquals("should only have manifest attribute", 1, manifest.getMainAttributes().size());

    mw.setMainClass("edu.rice.cs.drjava.DrJava");
    manifest = mw.getManifest();
    assertTrue("should have version attribute", manifest.getMainAttributes().containsKey(Attributes.Name.MANIFEST_VERSION));
    assertEquals("should have version attribute", "1.0", manifest.getMainAttributes().get(Attributes.Name.MANIFEST_VERSION));
    assertTrue("should have main class attribute", manifest.getMainAttributes().containsKey(Attributes.Name.MAIN_CLASS));
    assertEquals("should have main class attribute", "edu.rice.cs.drjava.DrJava", manifest.getMainAttributes().get(Attributes.Name.MAIN_CLASS));
    assertEquals("should only have manifest attribute", 2, manifest.getMainAttributes().size());


    mw = new ManifestWriter();
    mw.addClassPath("koala.dynamicjava");
    manifest = mw.getManifest();
    assertTrue("should have version attribute", manifest.getMainAttributes().containsKey(Attributes.Name.MANIFEST_VERSION));
    assertEquals("should have version attribute", "1.0", manifest.getMainAttributes().get(Attributes.Name.MANIFEST_VERSION));
    assertTrue("should have classpath attribute", manifest.getMainAttributes().containsKey(Attributes.Name.CLASS_PATH));
    assertEquals("should have correct classpath", "koala.dynamicjava", manifest.getMainAttributes().get(Attributes.Name.CLASS_PATH));
    assertEquals("have version and classpath", 2, manifest.getMainAttributes().size());

    mw.addClassPath("edu.rice.cs.util");
    manifest = mw.getManifest();
    assertTrue("should have version attribute", manifest.getMainAttributes().containsKey(Attributes.Name.MANIFEST_VERSION));
    assertEquals("should have version attribute", "1.0", manifest.getMainAttributes().get(Attributes.Name.MANIFEST_VERSION));
    assertTrue("should have classpath attribute", manifest.getMainAttributes().containsKey(Attributes.Name.CLASS_PATH));
    assertEquals("should have correct classpath", "koala.dynamicjava edu.rice.cs.util", manifest.getMainAttributes().get(Attributes.Name.CLASS_PATH));
    assertEquals("have version and classpath", 2, manifest.getMainAttributes().size());

    mw.setMainClass("edu.rice.cs.drjava.DrJava");
    manifest = mw.getManifest();
    assertTrue("should have version attribute", manifest.getMainAttributes().containsKey(Attributes.Name.MANIFEST_VERSION));
    assertEquals("should have version attribute", "1.0", manifest.getMainAttributes().get(Attributes.Name.MANIFEST_VERSION));
    assertTrue("should have classpath attribute", manifest.getMainAttributes().containsKey(Attributes.Name.CLASS_PATH));
    assertEquals("should have correct classpath", "koala.dynamicjava edu.rice.cs.util", manifest.getMainAttributes().get(Attributes.Name.CLASS_PATH));
    assertTrue("should have main class attribute", manifest.getMainAttributes().containsKey(Attributes.Name.MAIN_CLASS));
    assertEquals("should have main class attribute", "edu.rice.cs.drjava.DrJava", manifest.getMainAttributes().get(Attributes.Name.MAIN_CLASS));
    assertEquals("have version and classpath", 3, manifest.getMainAttributes().size());
  }

  /**
   * Test create addDirectoryRecursive
   */
  public void testCreateJarFromDirectoryRecursive() {
    File dir = new File("temp_dir");
    dir.mkdir();
    File dir1 = new File(dir, "dir");
    dir1.mkdir();
    File[] files = new File[]{new File(dir, "test.java"),
                              new File(dir, "test.class"),
                              new File(dir, "p1.tmp"),
                              new File(dir1, "test1.java"),
                              new File(dir1, "out.class"),
                              new File(dir1, "out.out.out.class"),
                              new File(dir1, "that.java")};
    dir.deleteOnExit();
    dir1.deleteOnExit();
    for(int i = 0; i < files.length; i++)
      files[i].deleteOnExit();
    try {

      PrintWriter pw = null;
      for (int i = 0; i < files.length; i++) {
        pw = new PrintWriter(new FileOutputStream(files[i]));
        pw.write(files[i].getName());
        pw.close();
      }

      File f = new File("test~.jar");
      f.deleteOnExit();
      JarBuilder jb = new JarBuilder(f);
      jb.addDirectoryRecursive(dir, "");
      jb.close();

      testArchive(f,
              new TreeSet<String>(Arrays.asList(new String[]{files[0].getName(),
                                                             files[1].getName(),
                                                             files[2].getName(),
                                                             "dir/" + files[3].getName(),
                                                             "dir/" + files[4].getName(),
                                                             "dir/" + files[5].getName(),
                                                             "dir/" + files[6].getName()})));

      jb = new JarBuilder(f);
      jb.addDirectoryRecursive(dir, "", new FileFilter() {
        public boolean accept(File pathname) {
          return pathname.getName().endsWith(".class") || pathname.isDirectory();
        }
      });
      jb.close();

      testArchive(f,
              new TreeSet<String>(Arrays.asList(new String[]{files[1].getName(),
                                                             "dir/" + files[4].getName(),
                                                             "dir/" + files[5].getName()})));

      jb = new JarBuilder(f);
      jb.addDirectoryRecursive(dir, "", new FileFilter() {
        public boolean accept(File pathname) {
          return pathname.getName().endsWith(".java") || pathname.isDirectory();
        }
      });
      jb.close();

      testArchive(f,
              new TreeSet<String>(Arrays.asList(new String[]{files[0].getName(),
                                                             "dir/" + files[3].getName(),
                                                             "dir/" + files[6].getName()})));

    }
    catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    catch (IOException e) {
      e.printStackTrace();
    }

  }

  /**
   * Test the manual creation of jar files
   */
  public void testCreateJar() {
    File f = new File("test.jar");
    f.deleteOnExit();
    File add = null;
    try {
      String fileContents = "public class JarTest {" +
              "\tpublic String getClassName() {" +
              "\t\treturn \"JarTest\";" +
              "\t}" +
              "}";
      byte[] b = new byte[fileContents.getBytes("UTF-8").length];

      add = File.createTempFile("JarTest",".java");
      add.deleteOnExit();

      PrintWriter pw = new PrintWriter(new FileOutputStream(add));
      pw.write(fileContents);
      pw.close();

      JarBuilder jb = new JarBuilder(f);
      jb.addFile(add, "", "JarTest.java");
      jb.addFile(add, "dir", "JarTest.java");
      jb.close();


      testArchive(f,
              new TreeSet<String>(Arrays.asList(new String[]{"JarTest.java",
                                                             "dir/JarTest.java"})));

      JarInputStream jarStream = new JarInputStream(new FileInputStream(f), true);

      JarEntry ent = jarStream.getNextJarEntry();
      assertTrue("should have JarTest", ent != null);
      assertEquals("names should match", "JarTest.java", ent.getName());

      ent = jarStream.getNextJarEntry();
      assertTrue("should have JarTest", ent != null);
      assertEquals("names should match", "dir/JarTest.java", ent.getName());
    }
    catch (IOException e) {
      e.printStackTrace();
      fail("failed test");
    }
  }

  /**
   * Check that all files in an a Set are in the jar file
   * @param jar the jar file to check
   * @param fileNames the set of the names of files
   */
  private void testArchive(File jar, Set<String> fileNames) {
    JarInputStream jarStream = null;
    try {
      jarStream = new JarInputStream(new FileInputStream(jar), true);

      JarEntry ent = null;
      while( (ent = jarStream.getNextJarEntry()) != null ) {
        assertTrue("found "+ent.getName()+" should be in list", fileNames.contains(ent.getName()));
        fileNames.remove(ent.getName());
      }
    }
    catch (IOException e) {
      fail("couldn't open file");
    } finally {
      if( jarStream != null)
        try {
          jarStream.close();
        }
        catch (IOException e) {
          e.printStackTrace();
        }
    }
    assertEquals("all listed files should have been in archive", 0, fileNames.size());
  }
}