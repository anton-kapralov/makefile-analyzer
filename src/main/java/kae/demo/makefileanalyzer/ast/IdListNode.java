/*
 * 
 * 
 * Kapralov A.
 * 05.05.15
 */

package kae.demo.makefileanalyzer.ast;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Node with list of file or target name nodes.
 *
 * @author A. Kapralov
 *         05.05.15 19:30
 */
public class IdListNode extends AstNode {

  private List<IdNode> idNodes = new LinkedList<IdNode>();

  public Iterable<IdNode> getIdNodes() {
    return idNodes;
  }

  public boolean add(IdNode node) {
    if (node != null) {
      node.setParent(this);
      return idNodes.add(node);
    } else {
      return false;
    }
  }

  public int size() {
    return idNodes.size();
  }

  public IdNode get(int index) {
    return idNodes.get(index);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof IdListNode)) return false;

    IdListNode that = (IdListNode) o;

    return !(idNodes != null ? !idNodes.equals(that.idNodes) :
        that.idNodes != null);

  }

  @Override
  public int hashCode() {
    return idNodes != null ? idNodes.hashCode() : 0;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("id{");
    for (Iterator<IdNode> it = idNodes.iterator(); it.hasNext(); ) {
      IdNode idNode = it.next();
      sb.append(idNode.getFilename().getId());
      if (it.hasNext()) {
        sb.append(", ");
      }
    }
    sb.append("}");

    return sb.toString();
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
