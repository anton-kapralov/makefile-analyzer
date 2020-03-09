/*
 * 
 * 
 * Kapralov A.
 * 03.05.15
 */

package kae.demo.makefileanalyzer.parser;

/**
 * Delimiter "new line" (LF or CRLF)
 *
 * @author A. Kapralov
 *         03.05.15 22:48
 */
class NewLine extends Delimiter {

  protected NewLine(long id, String value) {
    super(id, value);
  }

  @Override
  public void process(Parser parser) throws ParserException {
    parser.process(this);
  }

}
