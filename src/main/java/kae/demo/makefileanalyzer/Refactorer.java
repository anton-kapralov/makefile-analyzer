/*
 * 
 * 
 * Kapralov A.
 * 05.05.15
 */

package kae.demo.makefileanalyzer;

import java.util.List;

import kae.demo.makefileanalyzer.ast.AstNode;
import kae.demo.makefileanalyzer.ast.CommandNode;
import kae.demo.makefileanalyzer.ast.FileNode;
import kae.demo.makefileanalyzer.ast.Filename;
import kae.demo.makefileanalyzer.ast.IdListNode;
import kae.demo.makefileanalyzer.ast.IdNode;
import kae.demo.makefileanalyzer.ast.NodeVisitor;
import kae.demo.makefileanalyzer.ast.PrerequisitesNode;
import kae.demo.makefileanalyzer.ast.PunctuationNode;
import kae.demo.makefileanalyzer.ast.RuleNode;
import kae.demo.makefileanalyzer.ast.TargetNode;
import kae.util.FileUtils;

/**
 * Utility class for refactoring purposes.
 *
 * @author A. Kapralov
 *         05.05.15 21:41
 */
public class Refactorer {

  private static class Renamer implements NodeVisitor {

    private final String source;
    private final String destination;
    private IdNode found;

    public Renamer(String source, String destination) {
      this.source = source;
      this.destination = destination;
    }

    public void visit(FileNode node) {

    }

    public void visit(RuleNode node) {

    }

    public void visit(TargetNode node) {

    }

    public void visit(IdListNode node) {
      if (found != null) {
        return;
      }

      final Iterable<IdNode> idNodes = node.getIdNodes();
      for (IdNode idNode : idNodes) {
        visit(idNode);
        if (found != null) {
          return;
        }
      }
    }

    public void visit(IdNode node) {
      if (found != null) {
        return;
      }

      final Filename filename = node.getFilename();
      if (filename.getId().equals(source)) {
        filename.setId(destination);
        found = node;
      }
    }

    public void visit(PunctuationNode node) {

    }

    public void visit(PrerequisitesNode node) {
      visit((IdListNode) node);
    }

    public void visit(CommandNode node) {
      node.rename(source, destination);
    }
  }

  /**
   * Renames in AST all source file or target names to destination value.
   * If source is file name, target of rule for *.o file will be renamed too.
   */
  public void rename(FileNode fileNode, String source, String destination) {
    Renamer renamer = new Renamer(source, destination);
    recursiveRename(fileNode, renamer);

    if (renamer.found != null) {
      IdNode node = renamer.found;
      if (node.getParentTargetNode() == null && node.getParent() instanceof PrerequisitesNode) {
        final String sourceTargetName = FileUtils.getNameWithoutExtension(source) + ".o";
        final String destinationTargetName = FileUtils.getNameWithoutExtension(destination) + ".o";
        renamer = new Renamer(sourceTargetName, destinationTargetName);
        recursiveRename(fileNode, renamer);
      }
    }
  }

  private void recursiveRename(AstNode node, Renamer renamer) {
    if (node == null) {
      return;
    }

    node.accept(renamer);

    final List<? extends AstNode> children = node.getChildren();
    if (children != null && !children.isEmpty()) {
      for (AstNode child : children) {
        if (child != null) {
          recursiveRename(child, renamer);
        }
      }
    }
  }

}
