/*
 * 
 * 
 * Kapralov A.
 * 15.05.15
 */

package kae.demo.makefileanalyzer.parser;

/**
 * @author A. Kapralov
 *         15.05.15 12:59
 */
class Whitespaces extends Token {

  /**
   * @param id    unique token identifier.
   * @param value text representation of this token.
   */
  protected Whitespaces(long id, String value) {
    super(id, value);
  }

  @Override
  public void process(Parser parser) throws ParserException {
    parser.process(this);
  }

}
