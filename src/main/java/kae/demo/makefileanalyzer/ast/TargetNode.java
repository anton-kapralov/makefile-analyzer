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
 * Target node
 *
 * @author A. Kapralov
 *         03.05.15 20:46
 */
public class TargetNode extends AstNode {

  private IdListNode targetsIdsNode = new IdListNode();

  private PunctuationNode punctuationNode;

  private PrerequisitesNode prerequisitesNode;

  private List<AstNode> children = new ArrayList<AstNode>(3);

  {
    children.add(targetsIdsNode);
    children.add(null);
    children.add(null);
  }

  public Iterable<IdNode> getIdNodes() {
    return targetsIdsNode.getIdNodes();
  }

  public int getIdNodesCount() {
    return targetsIdsNode.size();
  }

  public IdNode getIdNode(int i) {
    return targetsIdsNode.get(i);
  }

  public void addIdNode(IdNode node) {
    if (node != null) {
      targetsIdsNode.add(node);
      node.setParent(this);
    }
  }

  public IdListNode getTargetsIdsNode() {
    return targetsIdsNode;
  }

  public PunctuationNode getPunctuationNode() {
    return punctuationNode;
  }

  public void setPunctuationNode(PunctuationNode punctuationNode) {
    this.punctuationNode = punctuationNode;
    if (punctuationNode != null) {
      punctuationNode.setParent(this);
    }
    children.set(1, punctuationNode);
  }

  public PrerequisitesNode getPrerequisitesNode() {
    return prerequisitesNode;
  }

  public void setPrerequisitesNode(PrerequisitesNode prerequisitesNode) {
    this.prerequisitesNode = prerequisitesNode;
    if (prerequisitesNode != null) {
      prerequisitesNode.setParent(this);
    }
    children.set(2, prerequisitesNode);
  }

  @SuppressWarnings("SimplifiableIfStatement")
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof TargetNode)) return false;

    TargetNode that = (TargetNode) o;

    if (targetsIdsNode != null ? !targetsIdsNode.equals(that.targetsIdsNode) :
        that.targetsIdsNode != null) return false;
    if (punctuationNode != null ? !punctuationNode.equals(that.punctuationNode) :
        that.punctuationNode != null) return false;
    return !(prerequisitesNode != null ? !prerequisitesNode.equals(that.prerequisitesNode) :
        that.prerequisitesNode != null);

  }

  @Override
  public int hashCode() {
    int result = targetsIdsNode != null ? targetsIdsNode.hashCode() : 0;
    result = 31 * result + (punctuationNode != null ? punctuationNode.hashCode() : 0);
    result = 31 * result + (prerequisitesNode != null ? prerequisitesNode.hashCode() : 0);
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
