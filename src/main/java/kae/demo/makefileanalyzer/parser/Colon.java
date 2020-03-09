/*
 * 
 * 
 * Kapralov A.
 * 03.05.15
 */

package kae.demo.makefileanalyzer.parser;

/**
 * Delimiter "colon"
 *
 * @author A. Kapralov
 *         03.05.15 22:43
 */
class Colon extends Delimiter {

  public Colon(long id) {
    super(id, COLON);
  }

  @Override
  public void process(Parser parser) throws ParserException {
    parser.process(this);
  }

}
