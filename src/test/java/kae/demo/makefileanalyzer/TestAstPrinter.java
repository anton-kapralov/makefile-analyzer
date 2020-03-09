/*
 * 
 * 
 * Kapralov A.
 * 03.05.15
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
 *         03.05.15 23:24
 */
public class TestAstPrinter {

  @Test
  public void testPrint() throws Exception {
    final InputStream in = getClass().getResourceAsStream("/simplest.mk");
    Parser parser = new Parser(new BufferedReader(new InputStreamReader(in)));

    final FileNode fileNode = parser.parseAst();
    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    AstPrinter astPrinter = new AstPrinter(new PrintStream(baos));
    astPrinter.print(fileNode);

    final String expected = StreamUtils.readStream(getClass().getResourceAsStream("/simplest_ast.txt"));
    final String actual = baos.toString();
    System.out.println(actual);

    Assert.assertEquals(expected, actual);
  }

}
