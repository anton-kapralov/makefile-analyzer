/*
 * 
 * 
 * Kapralov A.
 * 01.05.15
 */

package kae.demo.makefileanalyzer.parser;

/**
 * Abstract makefile token.
 *
 * @author A. Kapralov
 *         01.05.15 18:48
 */
public abstract class Token {

  private long id;

  private String value;

  /**
   * @param id unique token identifier.
   * @param value text representation of this token.
   */
  protected Token(long id, String value) {
    this.id = id;
    this.value = value;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return value;
  }

  public abstract void process(Parser parser) throws ParserException;
}
