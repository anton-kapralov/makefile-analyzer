/*
 * 
 * 
 * Kapralov A.
 * 03.05.15
 */

package kae.demo.makefileanalyzer.ast;

/**
 * Node visitor for iteration over AST.
 *
 * @author A. Kapralov
 *         03.05.15 23:08
 */
public interface NodeVisitor {

  void visit(FileNode node);

  void visit(RuleNode node);

  void visit(TargetNode node);

  void visit(IdListNode node);

  void visit(IdNode node);

  void visit(PunctuationNode node);

  void visit(PrerequisitesNode node);

  void visit(CommandNode node);
}
