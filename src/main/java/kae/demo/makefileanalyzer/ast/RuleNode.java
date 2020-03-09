/*
 * 
 * 
 * Kapralov A.
 * 03.05.15
 */

package kae.demo.makefileanalyzer.ast;

import java.util.ArrayList;
import java.util.List;

/**
 * Rule Node
 *
 * @author A. Kapralov
 *         03.05.15 20:48
 */
public class RuleNode extends AstNode {

  private TargetNode targetNode;

  private CommandNode commandNode;

  private List<AstNode> children = new ArrayList<AstNode>(2);

  {
    children.add(null);
    children.add(null);
  }

  public TargetNode getTargetNode() {
    return targetNode;
  }

  public void setTargetNode(TargetNode targetNode) {
    this.targetNode = targetNode;
    if (targetNode != null) {
      targetNode.setParent(this);
    }
    children.set(0, targetNode);
  }

  public CommandNode getCommandNode() {
    return commandNode;
  }

  public void setCommandNode(CommandNode commandNode) {
    this.commandNode = commandNode;
    if (commandNode != null) {
      commandNode.setParent(this);
    }
    children.set(1, commandNode);
  }

  @SuppressWarnings("SimplifiableIfStatement")
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof RuleNode)) return false;

    RuleNode ruleNode = (RuleNode) o;

    if (targetNode != null ? !targetNode.equals(ruleNode.targetNode) : ruleNode.targetNode != null)
      return false;
    return !(commandNode != null ? !commandNode.equals(ruleNode.commandNode) :
        ruleNode.commandNode != null);

  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (targetNode != null ? targetNode.hashCode() : 0);
    result = 31 * result + (commandNode != null ? commandNode.hashCode() : 0);
    return result;
  }

  @Override
  public void accept(NodeVisitor visitor) {
    visitor.visit(this);
  }

  @Override
  public List<? extends AstNode> getChildren() {
    return children;
  }

}
