/*
 * 
 * 
 * Kapralov A.
 * 01.05.15
 */

package kae.demo.makefileanalyzer.ast;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import kae.demo.makefileanalyzer.parser.Token;

/**
 * Abstract AST-node.
 *
 * @author A. Kapralov
 *         01.05.15 18:47
 */
public abstract class AstNode {

  /**
   * Parent node.
   */
  private AstNode parent;

  private List<Token> precedingExtras = new LinkedList<>();
  private List<Token> internalExtras = new LinkedList<>();
  private List<Token> trailingExtras = new LinkedList<>();

  protected AstNode() {
  }

  /**
   * Parent node.
   */
  public AstNode getParent() {
    return parent;
  }

  public void setParent(AstNode parent) {
    this.parent = parent;
  }

  public abstract void accept(NodeVisitor visitor);

  /**
   * Returns child nodes of this node.
   * @return child nodes or null if this node doesn't have any child.
   */
  public abstract List<? extends AstNode> getChildren();

  public Iterable<Token> getPrecedingExtras() {
    return precedingExtras;
  }

  public void addPrecedingExtra(Token token) {
    precedingExtras.add(token);
  }

  public void addPrecedingExtras(Collection<Token> tokens) {
    precedingExtras.addAll(tokens);
  }

  public boolean hasPrecedingExtras() {
    return !precedingExtras.isEmpty();
  }

  public Iterable<Token> getInternalExtras() {
    return internalExtras;
  }

  public void addInternalExtra(Token token) {
    internalExtras.add(token);
  }

  public void addInternalExtras(Collection<Token> tokens) {
    internalExtras.addAll(tokens);
  }

  public boolean hasInternalExtras() {
    return !internalExtras.isEmpty();
  }

  public Iterable<Token> getTrailingExtras() {
    return trailingExtras;
  }

  public void addTrailingExtra(Token token) {
    trailingExtras.add(token);
  }

  public void addTrailingExtras(Collection<Token> tokens) {
    trailingExtras.addAll(tokens);
  }

  public boolean hasTrailingExtras() {
    return !trailingExtras.isEmpty();
  }

}
