/*
 * 
 * 
 * Kapralov A.
 * 03.05.15
 */

package kae.demo.makefileanalyzer;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;

import kae.demo.makefileanalyzer.ast.AstNode;
import kae.demo.makefileanalyzer.ast.CommandNode;
import kae.demo.makefileanalyzer.ast.FileNode;
import kae.demo.makefileanalyzer.ast.IdListNode;
import kae.demo.makefileanalyzer.ast.IdNode;
import kae.demo.makefileanalyzer.ast.NodeVisitor;
import kae.demo.makefileanalyzer.ast.PrerequisitesNode;
import kae.demo.makefileanalyzer.ast.PunctuationNode;
import kae.demo.makefileanalyzer.ast.RuleNode;
import kae.demo.makefileanalyzer.ast.TargetNode;
import kae.demo.makefileanalyzer.parser.Token;
import kae.util.StringUtils;

/**
 * AST code generator.
 *
 * @author A. Kapralov
 *         03.05.15 23:11
 */
public class AstCodeGenerator implements NodeVisitor {

  private final PrintStream out;

  private String recipePrefix = "\t";

  private boolean firstLine;

  public AstCodeGenerator(PrintStream out) {
    this.out = out;
  }

  public void setRecipePrefix(String recipePrefix) {
    this.recipePrefix = recipePrefix;
  }

  /**
   * Prints makefile code by AST into specified at constructor PrintStream.
   * @param node AST root.
   */
  public void printMakefile(FileNode node) {
    recursivePrint(node);
  }

  private void recursivePrint(AstNode node) {
    if (node == null) {
      return;
    }

    printTokens(node.getPrecedingExtras());

    node.accept(this);

    final List<? extends AstNode> children = node.getChildren();
    if (children != null && !children.isEmpty()) {
      for (AstNode child : children) {
        if (child != null) {
          recursivePrint(child);
        }
      }
    }

    printTokens(node.getTrailingExtras());
  }

  private void printTokens(Iterable<Token> tokens) {
    for (Token token : tokens) {
      out.print(token.getValue());
    }
  }

  public void visit(FileNode node) {
    printTokens(node.getInternalExtras());
  }

  public void visit(RuleNode node) {
    firstLine = true;
  }

  public void visit(TargetNode node) {

  }

  public void visit(IdListNode node) {
    final Iterable<IdNode> idNodes = node.getIdNodes();
    for (Iterator<IdNode> it = idNodes.iterator(); it.hasNext(); ) {
      IdNode idNode = it.next();
      printTokens(idNode.getPrecedingExtras());
      out.print(idNode.getFilename().getId());
      if (idNode.hasTrailingExtras()) {
        printTokens(idNode.getTrailingExtras());
      } else if (it.hasNext()) {
        out.print(" ");
      }
    }
  }

  public void visit(IdNode node) {

  }

  public void visit(PunctuationNode node) {
    out.print(node.getSign());

    if (!node.hasTrailingExtras()) {
      if (node.getParent() instanceof TargetNode) {
        TargetNode parent = (TargetNode) node.getParent();
        if (parent.getPrerequisitesNode() != null) {
          out.print(" ");
        }
      }
    }
  }

  public void visit(PrerequisitesNode node) {
    visit((IdListNode) node);
    if (!node.hasTrailingExtras()) {
      final int size = node.size();
      if (size == 0 || !node.get(size - 1).hasTrailingExtras()) {
        out.println();
      }
    }
    firstLine = false;
  }

  public void visit(CommandNode node) {
    if (firstLine) {
      out.println();
    }

    final Iterable<String> recipes = node.getRecipes();
    for (String recipe : recipes) {
      if (StringUtils.isEmpty(recipe)) {
        continue;
      }

      out.print(recipePrefix);
      out.print(recipe);
      out.println();
    }
  }

}
