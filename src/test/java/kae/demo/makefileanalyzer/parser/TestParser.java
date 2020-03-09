/*
 * 
 * 
 * Kapralov A.
 * 01.05.15
 */

package kae.demo.makefileanalyzer.parser;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import kae.demo.makefileanalyzer.AstPrinter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import kae.demo.makefileanalyzer.ast.CommandNode;
import kae.demo.makefileanalyzer.ast.FileNode;
import kae.demo.makefileanalyzer.ast.IdNode;
import kae.demo.makefileanalyzer.ast.PrerequisitesNode;
import kae.demo.makefileanalyzer.ast.PunctuationNode;
import kae.demo.makefileanalyzer.ast.RuleNode;
import kae.demo.makefileanalyzer.ast.TargetNode;
import kae.util.StreamUtils;

/**
 * @author A. Kapralov
 *         01.05.15 18:41
 */
public class TestParser {

  private Parser parser;
  private FileNode fileNode;

  @Before
  public void setUp() throws Exception {
    parser = new Parser(null);
    fileNode = new FileNode();

    final Field field = Parser.class.getDeclaredField("fileNode");
    field.setAccessible(true);
    field.set(parser, fileNode);
  }

  @Test
  public void testParseAst() throws Exception {
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
//    allRuleNode.setCommandNode(new CommandNode(""));

    final RuleNode cleanRuleNode = new RuleNode();
    final TargetNode cleanTargetNode = new TargetNode();
    cleanTargetNode.addIdNode(new IdNode("clean"));
    cleanTargetNode.setPunctuationNode(new PunctuationNode(":"));
    cleanRuleNode.setTargetNode(cleanTargetNode);
    cleanRuleNode.setCommandNode(new CommandNode("rm *o hello"));

    List<RuleNode> expectedRuleNodes = new ArrayList<RuleNode>(4);
    expectedRuleNodes.add(allRuleNode);
    expectedRuleNodes.add(helloRuleNode);
    expectedRuleNodes.add(main_oRuleNode);
    expectedRuleNodes.add(cleanRuleNode);

    final InputStream in = getClass().getResourceAsStream("/simplest.mk");
    Parser parser = new Parser(new BufferedReader(new InputStreamReader(in)));

    final FileNode fileNode = parser.parseAst();
    Assert.assertNotNull(fileNode);

    final Iterable<RuleNode> ruleNodes = fileNode.getRuleNodes();
    Assert.assertNotNull(ruleNodes);
    Assert.assertEquals(4, fileNode.size());

    for (int i = 0; i < fileNode.size(); i++) {
      RuleNode expectedRuleNode = expectedRuleNodes.get(i);
      RuleNode ruleNode = fileNode.get(i);

      final TargetNode expected = expectedRuleNode.getTargetNode();
      final TargetNode actual = ruleNode.getTargetNode();
      Assert.assertEquals(String.valueOf(i), expected.getIdNodes(),
          actual.getIdNodes());
      Assert.assertEquals(String.valueOf(i), expected.getPunctuationNode(),
          actual.getPunctuationNode());
      Assert.assertEquals(String.valueOf(i), expected.getPrerequisitesNode(),
          actual.getPrerequisitesNode());

      Assert.assertEquals(String.valueOf(i), expectedRuleNode.getCommandNode(),
          ruleNode.getCommandNode());
    }
  }

  @Test
  public void testProcessTargetRuleStart() throws Exception {
    final String targetId = "all";
    parser.process(new Identifier(0, targetId));

    Assert.assertEquals(1, fileNode.size());

    final RuleNode ruleNode = fileNode.get(0);
    final TargetNode targetNode = ruleNode.getTargetNode();
    Assert.assertNotNull(targetNode);

    Assert.assertEquals(1, targetNode.getIdNodesCount());

    final IdNode idNode = targetNode.getIdNode(0);
    Assert.assertNotNull(idNode);
    Assert.assertEquals(targetId, idNode.getFilename().getId());
  }

  @Test
  public void testProcessTargetRuleStartMultipleTargets() throws Exception {
    final RuleNode ruleNode = new RuleNode();
    final Field field = Parser.class.getDeclaredField("ruleNode");
    field.setAccessible(true);
    field.set(parser, ruleNode);

    fileNode.add(ruleNode);
    final TargetNode targetNode = new TargetNode();
    final String firstTargetId = "all";
    targetNode.addIdNode(new IdNode(firstTargetId));
    ruleNode.setTargetNode(targetNode);

    final String secondTargetId = "hello";
    parser.process(new Identifier(0, secondTargetId));

    Assert.assertEquals(1, fileNode.size());

    Assert.assertEquals(2, targetNode.getIdNodesCount());

    IdNode idNode = targetNode.getIdNode(0);
    Assert.assertNotNull(idNode);
    Assert.assertEquals(firstTargetId, idNode.getFilename().getId());

    idNode = targetNode.getIdNode(1);
    Assert.assertNotNull(idNode);
    Assert.assertEquals(secondTargetId, idNode.getFilename().getId());
  }

  @Test
  public void testProcessColon() throws Exception {
    final RuleNode ruleNode = new RuleNode();
    final Field field = Parser.class.getDeclaredField("ruleNode");
    field.setAccessible(true);
    field.set(parser, ruleNode);

    fileNode.add(ruleNode);
    final TargetNode targetNode = new TargetNode();
    targetNode.addIdNode(new IdNode("all"));
    ruleNode.setTargetNode(targetNode);

    parser.process(new Colon(0));

    final PunctuationNode punctuationNode = targetNode.getPunctuationNode();
    Assert.assertNotNull(punctuationNode);
    Assert.assertEquals(Delimiter.COLON, punctuationNode.getSign());
  }

  @Test(expected = ParserException.class)
  public void testProcessColonWithoutRuleNode() throws Exception {
    parser.process(new Colon(0));
  }

  @Test
  public void testProcessTargetRulePrerequisite() throws Exception {
    final RuleNode ruleNode = new RuleNode();
    final Field field = Parser.class.getDeclaredField("ruleNode");
    field.setAccessible(true);
    field.set(parser, ruleNode);

    fileNode.add(ruleNode);
    final TargetNode targetNode = new TargetNode();
    targetNode.addIdNode(new IdNode("all"));
    targetNode.setPunctuationNode(new PunctuationNode(":"));
    ruleNode.setTargetNode(targetNode);

    final String targetId = "hello";
    parser.process(new Identifier(0, targetId));

    final PrerequisitesNode prerequisitesNode = targetNode.getPrerequisitesNode();
    Assert.assertNotNull(prerequisitesNode);
    Assert.assertEquals(1, prerequisitesNode.size());
    final IdNode idNode = prerequisitesNode.get(0);
    Assert.assertEquals(targetId, idNode.getFilename().getId());
  }

  @Test
  public void testProcessTargetRulePrerequisites() throws Exception {
    final RuleNode ruleNode = new RuleNode();
    final Field field = Parser.class.getDeclaredField("ruleNode");
    field.setAccessible(true);
    field.set(parser, ruleNode);

    fileNode.add(ruleNode);
    final TargetNode targetNode = new TargetNode();
    targetNode.addIdNode(new IdNode("hello"));
    targetNode.setPunctuationNode(new PunctuationNode(":"));
    PrerequisitesNode prerequisitesNode = new PrerequisitesNode();
    final String firstTargetId = "main.o";
    prerequisitesNode.add(new IdNode(firstTargetId));
    targetNode.setPrerequisitesNode(prerequisitesNode);
    ruleNode.setTargetNode(targetNode);

    final String secondTargetId = "foo.o";
    parser.process(new Identifier(0, secondTargetId));

    Assert.assertEquals(2, prerequisitesNode.size());
    Assert.assertEquals(firstTargetId, prerequisitesNode.get(0).getFilename().getId());
    Assert.assertEquals(secondTargetId, prerequisitesNode.get(1).getFilename().getId());
  }

  @Test
  public void testProcessRecipe() throws Exception {
    final RuleNode ruleNode = new RuleNode();
    final Field field = Parser.class.getDeclaredField("ruleNode");
    field.setAccessible(true);
    field.set(parser, ruleNode);

    fileNode.add(ruleNode);
    final TargetNode targetNode = new TargetNode();
    targetNode.addIdNode(new IdNode("hello"));
    targetNode.setPunctuationNode(new PunctuationNode(":"));
    PrerequisitesNode prerequisitesNode = new PrerequisitesNode();
    prerequisitesNode.add(new IdNode("main.o"));
    targetNode.setPrerequisitesNode(prerequisitesNode);
    ruleNode.setTargetNode(targetNode);

    final String recipe = "g++ main.o -o hello";
    parser.process(new Recipe(0, recipe));

    final CommandNode commandNode = ruleNode.getCommandNode();
    Assert.assertNotNull(commandNode);
    Assert.assertEquals(recipe, commandNode.getRecipes().iterator().next());
  }

  @Test(expected = ParserException.class)
  public void testParseAstWithDuplicateTarget() throws Exception {
    final InputStream in = getClass().getResourceAsStream("/duplicate_target.mk");
    Parser parser = new Parser(new BufferedReader(new InputStreamReader(in)));
    parser.parseAst();
  }

  @Test(expected = ParserException.class)
  public void testParseAstWithDuplicateMultipleTarget() throws Exception {
    final InputStream in = getClass().getResourceAsStream("/duplicate_multiple_target.mk");
    Parser parser = new Parser(new BufferedReader(new InputStreamReader(in)));
    parser.parseAst();
  }

  @Test
  public void testMultilineRecipe() throws Exception {
    final InputStream in = getClass().getResourceAsStream("/multiline_recipe.mk");
    Parser parser = new Parser(new BufferedReader(new InputStreamReader(in)));

    final FileNode fileNode = parser.parseAst();
    Assert.assertNotNull(fileNode);

    final Iterable<RuleNode> ruleNodes = fileNode.getRuleNodes();
    Assert.assertNotNull(ruleNodes);
    Assert.assertEquals(1, fileNode.size());

    final RuleNode ruleNode = fileNode.get(0);
    Assert.assertNotNull(ruleNode);

    final CommandNode commandNode = ruleNode.getCommandNode();
    Assert.assertNotNull(commandNode);
    Assert.assertEquals(2, commandNode.size());

    Assert.assertEquals("g++ main.o -o hello", commandNode.get(0));
    Assert.assertEquals("@echo 'hello'", commandNode.get(1));
  }

  @Test
  public void testBlankRecipe() throws Exception {
    final InputStream in = getClass().getResourceAsStream("/blank_recipe.mk");
    Parser parser = new Parser(new BufferedReader(new InputStreamReader(in)));

    final FileNode fileNode = parser.parseAst();
    Assert.assertNotNull(fileNode);

    final Iterable<RuleNode> ruleNodes = fileNode.getRuleNodes();
    Assert.assertNotNull(ruleNodes);
    Assert.assertEquals(2, fileNode.size());

    final RuleNode ruleNode = fileNode.get(0);
    Assert.assertNotNull(ruleNode);

    final CommandNode commandNode = ruleNode.getCommandNode();
    Assert.assertNotNull(commandNode);
    Assert.assertEquals(1, commandNode.size());

    Assert.assertEquals("", commandNode.get(0));
  }

  @Test
  public void testDuplicatePrerequisite() throws Exception {
    final InputStream in = getClass().getResourceAsStream("/duplicate_prerequisite.mk");
    Parser parser = new Parser(new BufferedReader(new InputStreamReader(in)));

    final FileNode fileNode = parser.parseAst();
    Assert.assertNotNull(fileNode);
    Assert.assertEquals(5, fileNode.size());
  }

  @Test
  public void testSplitLines() throws Exception {
    final InputStream in = getClass().getResourceAsStream("/split_lines.mk");
    Parser parser = new Parser(new BufferedReader(new InputStreamReader(in)));

    final FileNode fileNode = parser.parseAst();
    Assert.assertNotNull(fileNode);
    Assert.assertEquals(1, fileNode.size());

    final RuleNode ruleNode = fileNode.get(0);
    Assert.assertNotNull(ruleNode);

    final TargetNode targetNode = ruleNode.getTargetNode();
    Assert.assertEquals("edit", targetNode.getIdNode(0).getFilename().getId());
    final PrerequisitesNode prerequisitesNode = targetNode.getPrerequisitesNode();
    Assert.assertEquals(8, prerequisitesNode.size());
    int idx = -1;
    Assert.assertEquals("main.o", prerequisitesNode.get(++idx).getFilename().getId());
    Assert.assertEquals("kbd.o", prerequisitesNode.get(++idx).getFilename().getId());
    Assert.assertEquals("command.o", prerequisitesNode.get(++idx).getFilename().getId());
    Assert.assertEquals("display.o", prerequisitesNode.get(++idx).getFilename().getId());
    Assert.assertEquals("insert.o", prerequisitesNode.get(++idx).getFilename().getId());
    Assert.assertEquals("search.o", prerequisitesNode.get(++idx).getFilename().getId());
    Assert.assertEquals("files.o", prerequisitesNode.get(++idx).getFilename().getId());
    Assert.assertEquals("utils.o", prerequisitesNode.get(++idx).getFilename().getId());
    Assert.assertEquals(
        "cc -o edit main.o kbd.o command.o display.o                    insert.o search.o files.o utils.o",
        ruleNode.getCommandNode().get(0));
  }

  @Test
  public void testSplitLinesWhithoutWhitespace() throws Exception {
    final InputStream in = getClass().getResourceAsStream("/split_lines_without_whitespace.mk");
    Parser parser = new Parser(new BufferedReader(new InputStreamReader(in)));

    final FileNode fileNode = parser.parseAst();
    Assert.assertNotNull(fileNode);
    Assert.assertEquals(1, fileNode.size());

    final RuleNode ruleNode = fileNode.get(0);
    Assert.assertNotNull(ruleNode);

    final TargetNode targetNode = ruleNode.getTargetNode();
    Assert.assertEquals("all", targetNode.getIdNode(0).getFilename().getId());
    final PrerequisitesNode prerequisitesNode = targetNode.getPrerequisitesNode();
    Assert.assertEquals(2, prerequisitesNode.size());
    int idx = -1;
    Assert.assertEquals("hello", prerequisitesNode.get(++idx).getFilename().getId());
    Assert.assertEquals("a", prerequisitesNode.get(++idx).getFilename().getId());
  }

  @Test
  public void testMultipleTargets() throws Exception {
    final InputStream in = getClass().getResourceAsStream("/multiple_targets.mk");
    Parser parser = new Parser(new BufferedReader(new InputStreamReader(in)));

    final FileNode fileNode = parser.parseAst();
    Assert.assertNotNull(fileNode);
    Assert.assertEquals(2, fileNode.size());

    RuleNode ruleNode = fileNode.get(0);
    Assert.assertNotNull(ruleNode);
    TargetNode targetNode = ruleNode.getTargetNode();
    Assert.assertEquals(3, targetNode.getIdNodesCount());
    int idx = -1;
    Assert.assertEquals("kbd.o", targetNode.getIdNode(++idx).getFilename().getId());
    Assert.assertEquals("command.o", targetNode.getIdNode(++idx).getFilename().getId());
    Assert.assertEquals("files.o", targetNode.getIdNode(++idx).getFilename().getId());
    PrerequisitesNode prerequisitesNode = targetNode.getPrerequisitesNode();
    Assert.assertEquals(1, prerequisitesNode.size());
    Assert.assertEquals("command.h", prerequisitesNode.get(0).getFilename().getId());
    Assert.assertNull(ruleNode.getCommandNode());

    ruleNode = fileNode.get(1);
    Assert.assertNotNull(ruleNode);
    targetNode = ruleNode.getTargetNode();
    Assert.assertEquals(2, targetNode.getIdNodesCount());
    idx = -1;
    Assert.assertEquals("bigoutput", targetNode.getIdNode(++idx).getFilename().getId());
    Assert.assertEquals("littleoutput", targetNode.getIdNode(++idx).getFilename().getId());
    prerequisitesNode = targetNode.getPrerequisitesNode();
    Assert.assertEquals(1, prerequisitesNode.size());
    Assert.assertEquals("text.g", prerequisitesNode.get(0).getFilename().getId());
    final CommandNode commandNode = ruleNode.getCommandNode();
    Assert.assertNotNull(commandNode);
    Assert.assertEquals(1, commandNode.size());
    Assert.assertEquals("generate text.g -$(subst output,,$@) > $@", commandNode.get(0));
  }

  @Test(expected = ParserException.class)
  public void testIllegalCharInTargetName() throws Exception {
    final InputStream in = getClass().getResourceAsStream("/illegal_char_in_target_name.mk");
    Parser parser = new Parser(new BufferedReader(new InputStreamReader(in)));

    try {
      parser.parseAst();
    } catch (ParserException e) {
      Assert.assertEquals("{1:3} Illegal character: '?'", e.getMessage());
      throw e;
    }
  }

  @Test(expected = ParserException.class)
  public void testIllegalCharInPrerequisiteName() throws Exception {
    final InputStream in = getClass().getResourceAsStream("/illegal_char_in_prerequisite_name.mk");
    Parser parser = new Parser(new BufferedReader(new InputStreamReader(in)));

    try {
      parser.parseAst();
    } catch (ParserException e) {
      Assert.assertEquals("{1:10} Illegal character: '&'", e.getMessage());
      throw e;
    }
  }

  @Test(expected = ParserException.class)
  public void testIllegalBackslashInPrerequisites() throws Exception {
    final InputStream in = getClass().getResourceAsStream("/illegal_backslash_in_prerequisites.mk");
    Parser parser = new Parser(new BufferedReader(new InputStreamReader(in)));

    try {
      parser.parseAst();
    } catch (ParserException e) {
      Assert.assertEquals("{1:11} Illegal escape sequence: '\\a'", e.getMessage());
      throw e;
    }
  }

  @Test
  public void testParseAstWithExtras() throws Exception {
    final InputStream in = getClass().getResourceAsStream("/simplest_with_extras.mk");
    Parser parser = new Parser(new BufferedReader(new InputStreamReader(in)));
    FileNode fileNode = parser.parseAst();

    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    AstPrinter astPrinter = new AstPrinter(new PrintStream(baos));
    astPrinter.print(fileNode);

    final String expected = StreamUtils
        .readStream(getClass().getResourceAsStream("/simplest_with_extras_ast.txt"));
    final String actual = baos.toString();
    System.out.println(actual);

    Assert.assertEquals(expected, actual);
  }

}
