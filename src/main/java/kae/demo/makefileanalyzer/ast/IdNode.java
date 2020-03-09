/*
 * 
 * 
 * Kapralov A.
 * 03.05.15
 */

package kae.demo.makefileanalyzer.ast;

import java.util.List;

/**
 * File or target name node.
 *
 * @author A. Kapralov
 *         03.05.15 20:37
 */
public class IdNode extends AstNode {

  /**
   * Target node of rule for this target name. For files equals null.
   */
  private TargetNode parentTargetNode;

  private Filename filename;

  public IdNode() {
  }

  public IdNode(String id) {
    this.filename = new Filename(id);
  }

  public IdNode(Filename filename) {
    this.filename = filename;
  }

  /**
   * Target node of rule for this target name. For files equals null.
   */
  public TargetNode getParentTargetNode() {
    return parentTargetNode;
  }

  public void setParentTargetNode(TargetNode parentTargetNode) {
    this.parentTargetNode = parentTargetNode;
  }

  public Filename getFilename() {
    return filename;
  }

  public void setFilename(Filename filename) {
    this.filename = filename;
  }

  @Override
  public String toString() {
    return filename.getId();
  }

  @SuppressWarnings("SimplifiableIfStatement")
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof IdNode)) return false;

    IdNode idNode = (IdNode) o;

    if (parentTargetNode != null ? !parentTargetNode.equals(idNode.parentTargetNode) :
        idNode.parentTargetNode != null) return false;
    return !(filename != null ? !filename.equals(idNode.filename) : idNode.filename != null);

  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (parentTargetNode != null ? parentTargetNode.hashCode() : 0);
    result = 31 * result + (filename != null ? filename.hashCode() : 0);
    return result;
  }


  @Override
  public void accept(NodeVisitor visitor) {
    visitor.visit(this);
  }

  @Override
  public List<? extends AstNode> getChildren() {
    return null;
  }

}
