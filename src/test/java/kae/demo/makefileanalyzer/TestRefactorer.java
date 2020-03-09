/*
 * 
 * 
 * Kapralov A.
 * 05.05.15
 */

package kae.demo.makefileanalyzer;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

import org.junit.Assert;
import org.junit.Test;

import kae.demo.makefileanalyzer.ast.FileNode;
import kae.demo.makefileanalyzer.parser.Parser;
import kae.util.StreamUtils;

/**
 * @author A. Kapralov
 *         05.05.15 21:48
 */
public class TestRefactorer {

  @Test
  public void testRename() throws Exception {
    final InputStream in = getClass().getResourceAsStream("/simplest.mk");
    Parser parser = new Parser(new BufferedReader(new InputStreamReader(in)));

    final FileNode fileNode = parser.parseAst();

    Refactorer refactorer = new Refactorer();
    refactorer.rename(fileNode, "main.cpp", "general.cxx");

    final String expected = StreamUtils
        .readStream(getClass().getResourceAsStream("/simplest_ast_renamed.txt"));

    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    AstPrinter astPrinter = new AstPrinter(new PrintStream(baos));
    astPrinter.print(fileNode);
    final String actual = baos.toString();
    System.out.println(actual);

    Assert.assertEquals(expected, actual);
  }

}
