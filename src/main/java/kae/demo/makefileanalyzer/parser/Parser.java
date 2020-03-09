/*
 * 
 * 
 * Kapralov A.
 * 01.05.15
 */

package kae.demo.makefileanalyzer.parser;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import kae.demo.makefileanalyzer.ast.Filename;
import kae.demo.makefileanalyzer.ast.PunctuationNode;
import kae.demo.makefileanalyzer.ast.AstNode;
import kae.demo.makefileanalyzer.ast.CommandNode;
import kae.demo.makefileanalyzer.ast.FileNode;
import kae.demo.makefileanalyzer.ast.IdNode;
import kae.demo.makefileanalyzer.ast.PrerequisitesNode;
import kae.demo.makefileanalyzer.ast.RuleNode;
import kae.demo.makefileanalyzer.ast.TargetNode;

/**
 * Makefile parser.
 *
 * @author A. Kapralov
 *         01.05.15 18:16
 */
public class Parser {

  private final Scanner scanner;
  private final FileNode fileNode;
  private RuleNode ruleNode;
  private AstNode lastLeafNode;
  private boolean firstLine = true;
  private boolean lineHasSplit = false;
  private boolean recipeLine = false;
  private boolean emptyRecipe = false;
  private final Map<String, Filename> filenameMap = new HashMap<>();
  private final Map<String, TargetNode> targetNodeMap = new HashMap<>();
  private final Map<String, List<IdNode>> prerequisitesIdNodeMap = new HashMap<>();
  private final List<Token> extras = new LinkedList<>();

  public Parser(Reader reader) {
    scanner = new Scanner(reader);
    fileNode = new FileNode();
  }

  public FileNode parseAst() throws IOException, ParserException {
    Token token;
    try {
      while (null != (token = scanner.nextToken())) {
        token.process(this);
      }

      if (!extras.isEmpty()) {
        if (fileNode.size() > 0) {
          final RuleNode ruleNode = fileNode.get(fileNode.size() - 1);
          ruleNode.addTrailingExtras(extras);
        } else {
          fileNode.addInternalExtras(extras);
        }
        extras.clear();
      }
    } catch (ScannerException e) {
      throw new ParserException(String
          .format("{%d:%d} %s", scanner.getStartRow(), scanner.getStartCol(), e.getMessage()));
    }

    return fileNode;
  }

  void process(Identifier identifier) throws ParserException {
    final String value = identifier.getValue();

    Filename filename = filenameMap.get(value);
    if (filename == null) {
      filename = new Filename(value);
      filenameMap.put(value, filename);
    }

    final IdNode idNode = new IdNode(filename);
    lastLeafNode = idNode;

    if (ruleNode == null || !firstLine) {
      ruleNode = new RuleNode();
      ruleNode.addPrecedingExtras(extras);
      extras.clear();
      firstLine = true;
      lineHasSplit = false;

      final TargetNode targetNode = new TargetNode();
      targetNode.addIdNode(idNode);
      ruleNode.setTargetNode(targetNode);

      if (targetNodeMap.get(value) != null) {
        throw new ParserException(String.format("{%d:%d} \"%s\" already defined",
            scanner.getStartRow(), scanner.getStartCol(), value));
      }
      updateTargetMaps(value, targetNode);

      fileNode.add(ruleNode);
    } else {
      final TargetNode targetNode = ruleNode.getTargetNode();

      final PunctuationNode punctuationNode = targetNode.getPunctuationNode();
      if (punctuationNode == null) {
        if (targetNodeMap.get(value) != null) {
          throw new ParserException(String.format("{%d:%d} \"%s\" already defined",
              scanner.getStartRow(), scanner.getStartCol(), value));
        }
        targetNode.addIdNode(idNode);
        updateTargetMaps(value, targetNode);
      } else {
        PrerequisitesNode prerequisitesNode = targetNode.getPrerequisitesNode();
        if (prerequisitesNode == null) {
          prerequisitesNode = new PrerequisitesNode();
          targetNode.setPrerequisitesNode(prerequisitesNode);
        }
        prerequisitesNode.add(idNode);
        List<IdNode> idNodes = prerequisitesIdNodeMap.get(value);
        if (idNodes == null) {
          idNodes = new LinkedList<>();
          prerequisitesIdNodeMap.put(value, idNodes);
        }
        idNodes.add(idNode);
      }
    }
  }

  private void updateTargetMaps(String value, TargetNode targetNode) {
    targetNodeMap.put(value, targetNode);
    final List<IdNode> prerequisitesIdNodes = prerequisitesIdNodeMap.get(value);
    if (prerequisitesIdNodes != null) {
      for (IdNode prerequisitesIdNode : prerequisitesIdNodes) {
        prerequisitesIdNode.setParentTargetNode(targetNode);
      }
    }
  }

  void process(Colon colon) throws ParserException {
    final String sign = colon.getValue();
    if (ruleNode == null) {
      final String message = String.format("Target name expected at {%d:%d}",
          scanner.getStartRow(), scanner.getStartCol());
      throw new ParserException(message);
    } else {
      final TargetNode targetNode = ruleNode.getTargetNode();
      PunctuationNode punctuationNode = targetNode.getPunctuationNode();
      final PrerequisitesNode prerequisitesNode = targetNode.getPrerequisitesNode();
      if (punctuationNode != null || prerequisitesNode != null) {
        final String message = String.format("Unexpected symbol ':' at {%d:%d}",
            scanner.getStartRow(), scanner.getStartCol());
        throw new ParserException(message);
      } else {
        punctuationNode = new PunctuationNode(sign);
        lastLeafNode = punctuationNode;
        targetNode.setPunctuationNode(punctuationNode);
      }
    }
  }

  @SuppressWarnings("UnusedParameters")
  public void process(Semicolon semicolon) throws ParserException {

  }

  @SuppressWarnings("UnusedParameters")
  public void process(RecipePrefix recipePrefix) {
    recipeLine = true;
    emptyRecipe = true;
  }

  @SuppressWarnings("UnusedParameters")
  public void process(NewLine newLine) {
    lastLeafNode = null;

    if (ruleNode != null) {
      if (firstLine) {
        firstLine = false;
      } else if (!recipeLine) {
        ruleNode = null;
        extras.add(newLine);
      } else if (emptyRecipe) {
        addRecipe("");
      }
    } else {
      extras.add(newLine);
    }

    recipeLine = false;
  }

  void process(Recipe recipe) throws ParserException {
    if (ruleNode == null) {
      final String message = String.format("Target name expected at {%d:%d}",
          scanner.getStartRow(), scanner.getStartCol());
      throw new ParserException(message);
    } else {
      addRecipe(recipe.getValue());
      emptyRecipe = false;
    }
  }

  private void addRecipe(String recipe) {
    CommandNode commandNode = ruleNode.getCommandNode();
    if (commandNode == null) {
      commandNode = new CommandNode();
      lastLeafNode = commandNode;
      ruleNode.setCommandNode(commandNode);
    }
    commandNode.add(recipe);
  }

  public void process(Comment comment) {
    if (lastLeafNode == null) {
      extras.add(comment);
    } else {
      lastLeafNode.addTrailingExtra(comment);
    }

    if (ruleNode != null) {
      if (firstLine && !lineHasSplit) {
        firstLine = false;
        lastLeafNode = null;
      }
    }
  }

  public void process(Whitespaces whitespaces) {
    processExtra(whitespaces);
  }

  public void process(LineSplit lineSplit) {
    lineHasSplit = true;
    processExtra(lineSplit);
  }

  private void processExtra(Token token) {
    if (lastLeafNode == null) {
      extras.add(token);
    } else {
      lastLeafNode.addTrailingExtra(token);
    }
  }

}
