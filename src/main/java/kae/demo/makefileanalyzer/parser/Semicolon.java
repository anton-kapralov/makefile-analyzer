/*
 * 
 * 
 * Kapralov A.
 * 03.05.15
 */

package kae.demo.makefileanalyzer.parser;

/**
 * Delimiter "semicolon"
 *
 * @author A. Kapralov
 *         03.05.15 22:45
 */
class Semicolon extends Delimiter {

  protected Semicolon(long id) {
    super(id, SEMICOLON);
  }

  @Override
  public void process(Parser parser) throws ParserException {
    parser.process(this);
  }
}
