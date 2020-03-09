/*
 * 
 * 
 * Kapralov A.
 * 03.05.15
 */

package kae.demo.makefileanalyzer.ast;

import java.util.LinkedList;
import java.util.List;

/**
 * Root AST node.
 *
 * @author A. Kapralov
 *         03.05.15 21:06
 */
public class FileNode extends AstNode {

  private List<RuleNode> ruleNodes = new LinkedList<RuleNode>();

  public Iterable<RuleNode> getRuleNodes() {
    return ruleNodes;
  }

  /**
   * @return Rule nodes count.
   */
  public int size() {
    return ruleNodes.size();
  }

  /**
   * Adds rule node
   */
  public boolean add(RuleNode ruleNode) {
    if (ruleNode != null) {
      ruleNode.setParent(this);
      return ruleNodes.add(ruleNode);
    } else {
      return false;
    }
  }

  /**
   * Returns rule node by index.
   */
  public RuleNode get(int index) {
    return ruleNodes.get(index);
  }

  @Override
  public void accept(NodeVisitor visitor) {
    visitor.visit(this);
  }

  @Override
  public List<? extends AstNode> getChildren() {
    return ruleNodes;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof FileNode)) return false;

    FileNode fileNode = (FileNode) o;

    return !(ruleNodes != null ? !ruleNodes.equals(fileNode.ruleNodes) :
        fileNode.ruleNodes != null);

  }

  @Override
  public int hashCode() {
    return ruleNodes != null ? ruleNodes.hashCode() : 0;
  }
}
