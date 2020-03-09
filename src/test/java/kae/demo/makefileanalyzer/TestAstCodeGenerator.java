/*
 * 
 * 
 * Kapralov A.
 * 06.05.15
 */

package kae.demo.makefileanalyzer;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

import org.junit.Assert;
import org.junit.Test;

import kae.demo.makefileanalyzer.ast.CommandNode;
import kae.demo.makefileanalyzer.ast.FileNode;
import kae.demo.makefileanalyzer.ast.IdNode;
import kae.demo.makefileanalyzer.ast.PrerequisitesNode;
import kae.demo.makefileanalyzer.ast.PunctuationNode;
import kae.demo.makefileanalyzer.ast.RuleNode;
import kae.demo.makefileanalyzer.ast.TargetNode;
import kae.demo.makefileanalyzer.parser.Parser;
import kae.util.StreamUtils;

/**
 * @author A. Kapralov
 *         06.05.15 2:04
 */
public class TestAstCodeGenerator {

  @Test
  public void testToSourceCode() throws Exception {
    final RuleNode main_oRuleNode = new RuleNode();
    final TargetNode main_oTargetNode = new TargetNode();
    final IdNode main_oIdNode = new IdNode("main.o");
    main_oTargetNode.addIdNode(main_oIdNode);
    final PrerequisitesNode main_oPrerequisitesNode = new PrerequisitesNode();
    final IdNode main_cppIdNode = new IdNode("main.cpp");
    main_oPrerequisitesNode.add(main_cppIdNode);
    main_oTargetNode.setPrerequisitesNode(main_oPrerequisitesNode);
    main_oTargetNode.setPunctuationNode(new PunctuationNode(":"));
    main_oRuleNode.setTargetNode(main_oTargetNode);
    main_oRuleNode.setCommandNode(new CommandNode("g++ -c main.cpp"));

    final RuleNode helloRuleNode = new RuleNode();
    final TargetNode helloTargetNode = new TargetNode();
    final IdNode helloIdNode = new IdNode("hello");
    helloTargetNode.addIdNode(helloIdNode);
    final PrerequisitesNode helloPrerequisitesNode = new PrerequisitesNode();
    final IdNode _main_oIdNode = new IdNode("main.o");
    _main_oIdNode.setParentTargetNode(main_oTargetNode);
    helloPrerequisitesNode.add(_main_oIdNode);
    helloTargetNode.setPrerequisitesNode(helloPrerequisitesNode);
    helloTargetNode.setPunctuationNode(new PunctuationNode(":"));
    helloRuleNode.setTargetNode(helloTargetNode);
    helloRuleNode.setCommandNode(new CommandNode("g++ main.o -o hello"));

    final RuleNode allRuleNode = new RuleNode();
    final TargetNode allTargetNode = new TargetNode();
    allTargetNode.addIdNode(new IdNode("all"));
    final PrerequisitesNode allPrerequisitesNode = new PrerequisitesNode();
    final IdNode _helloIdNode = new IdNode("hello");
    _helloIdNode.setParentTargetNode(helloTargetNode);
    allPrerequisitesNode.add(_helloIdNode);
    allTargetNode.setPrerequisitesNode(allPrerequisitesNode);
    allTargetNode.setPunctuationNode(new PunctuationNode(":"));
    allRuleNode.setTargetNode(allTargetNode);
    allRuleNode.setCommandNode(new CommandNode(""));

    final RuleNode cleanRuleNode = new RuleNode();
    final TargetNode cleanTargetNode = new TargetNode();
    cleanTargetNode.addIdNode(new IdNode("clean"));
    cleanTargetNode.setPunctuationNode(new PunctuationNode(":"));
    cleanRuleNode.setTargetNode(cleanTargetNode);
    cleanRuleNode.setCommandNode(new CommandNode("rm *o hello"));

    FileNode fileNode = new FileNode();
    fileNode.add(allRuleNode);
    fileNode.add(helloRuleNode);
    fileNode.add(main_oRuleNode);
    fileNode.add(cleanRuleNode);

    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    AstCodeGenerator generator = new AstCodeGenerator(new PrintStream(baos));
    generator.printMakefile(fileNode);
    final String actual = baos.toString();
    Assert.assertNotNull(actual);
    System.out.println(actual);

    final String expected = StreamUtils
        .readStream(getClass().getResourceAsStream("/simplest.mk"));

    Assert.assertEquals(expected, actual);
  }

  @Test
  public void testToSourceCodeWithExtras() throws Exception {
    final InputStream in = getClass().getResourceAsStream("/simplest_with_extras.mk");
    Parser parser = new Parser(new BufferedReader(new InputStreamReader(in)));
    FileNode fileNode = parser.parseAst();
    in.close();

    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    AstCodeGenerator generator = new AstCodeGenerator(new PrintStream(baos));
    generator.printMakefile(fileNode);
    final String actual = baos.toString();
    Assert.assertNotNull(actual);
    System.out.println(actual);

    final String expected = StreamUtils
        .readStream(getClass().getResourceAsStream("/simplest_with_extras.mk"));

    Assert.assertEquals(expected, actual);
  }

  @Test
  public void testToSourceCodeEmptyWithExtras() throws Exception {
    final InputStream in = getClass().getResourceAsStream("/empty_with_extras.mk");
    Parser parser = new Parser(new BufferedReader(new InputStreamReader(in)));
    FileNode fileNode = parser.parseAst();
    in.close();

    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    AstCodeGenerator generator = new AstCodeGenerator(new PrintStream(baos));
    generator.printMakefile(fileNode);
    final String actual = baos.toString();
    Assert.assertNotNull(actual);
    System.out.println(actual);

    final String expected = StreamUtils
        .readStream(getClass().getResourceAsStream("/empty_with_extras.mk"));

    Assert.assertEquals(expected, actual);
  }

}
