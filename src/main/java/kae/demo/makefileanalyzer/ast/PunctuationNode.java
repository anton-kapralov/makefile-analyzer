/*
 * 
 * 
 * Kapralov A.
 * 03.05.15
 */

package kae.demo.makefileanalyzer.ast;

import java.util.List;

/**
 * Punctuation node.
 *
 * @author A. Kapralov
 *         03.05.15 20:39
 */
public class PunctuationNode extends AstNode {

  private String sign;

  public PunctuationNode() {
  }

  public PunctuationNode(String sign) {
    this.sign = sign;
  }

  public String getSign() {
    return sign;
  }

  public void setSign(String sign) {
    this.sign = sign;
  }

  @Override
  public String toString() {
    return sign;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof PunctuationNode)) return false;

    PunctuationNode that = (PunctuationNode) o;

    return !(sign != null ? !sign.equals(that.sign) : that.sign != null);

  }

  @Override
  public int hashCode() {
    return sign != null ? sign.hashCode() : 0;
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
