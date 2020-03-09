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
import kae.demo.makefileanalyzer.ast.NodeVisitor;
import kae.demo.makefileanalyzer.ast.PrerequisitesNode;
import kae.demo.makefileanalyzer.ast.PunctuationNode;
import kae.demo.makefileanalyzer.ast.RuleNode;
import kae.demo.makefileanalyzer.ast.IdNode;
import kae.demo.makefileanalyzer.ast.TargetNode;

/**
 * AST text printer.
 *
 * @author A. Kapralov
 *         03.05.15 23:11
 */
public class AstPrinter implements NodeVisitor {

  private final PrintStream out;

  private static final String indent = "  ";
  private int indentCounter;

  public AstPrinter(PrintStream out) {
    this.out = out;
  }

  /**
   * Prints AST into specified at constructor PrintStream.
   * @param node AST root.
   */
  public void print(FileNode node) {
    recursivePrint(node);
  }

  private void recursivePrint(AstNode node) {
    if (node == null) {
      return;
    }

    printIndents();
    node.accept(this);

    ++indentCounter;
    final List<? extends AstNode> children = node.getChildren();
    if (children != null && !children.isEmpty()) {
      for (AstNode child : children) {
        if (child != null) {
          recursivePrint(child);
        }
      }
    }
    --indentCounter;
  }

  private void printIndents() {
    for (int i = 0; i < indentCounter; i++) {
      out.print(indent);
    }
  }

  public void visit(FileNode node) {
    out.println("File");
  }

  public void visit(RuleNode node) {
    out.println("rule");
  }

  public void visit(TargetNode node) {
    out.println("target");
  }

  public void visit(IdListNode node) {
    final Iterable<IdNode> idNodes = node.getIdNodes();
    out.print("id{");
    for (Iterator<IdNode> it = idNodes.iterator(); it.hasNext(); ) {
      IdNode idNode = it.next();
      out.print(idNode.getFilename().getId());
      if (it.hasNext()) {
        out.print(", ");
      }
    }
    out.println("}");
  }

  public void visit(IdNode node) {

  }

  public void visit(PunctuationNode node) {
    out.printf("punctuation{%s}", node.getSign());
    out.println();
  }

  public void visit(PrerequisitesNode node) {
    out.println("dep_list");
    ++indentCounter;
    printIndents();
    visit((IdListNode) node);
    --indentCounter;
  }

  public void visit(CommandNode node) {
    out.print("command{");
    final Iterable<String> recipes = node.getRecipes();
    for (Iterator<String> it = recipes.iterator(); it.hasNext(); ) {
      String recipe = it.next();
      out.print(recipe);
      if (it.hasNext()) {
        out.println();
        printIndents();
        out.print("        "); // align for command{
      }
    }
    out.print("}");
    out.println();
  }

}
