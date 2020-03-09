/*
 * 
 * 
 * Kapralov A.
 * 03.05.15
 */

package kae.demo.makefileanalyzer.ast;

/**
 * Prerequisites (dependencies) node.
 *
 * @author A. Kapralov
 *         03.05.15 20:44
 */
public class PrerequisitesNode extends IdListNode {

  @Override
  public void accept(NodeVisitor visitor) {
    visitor.visit(this);
  }

}
