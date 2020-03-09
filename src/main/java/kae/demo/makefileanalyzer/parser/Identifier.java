/*
 * 
 * 
 * Kapralov A.
 * 01.05.15
 */

package kae.demo.makefileanalyzer.parser;

/**
 * Identifier (e.g., file or target name)
 *
 * @author A. Kapralov
 *         01.05.15 20:33
 */
class Identifier extends Token {

  public Identifier(long id, String value) {
    super(id, value);
  }

  @Override
  public void process(Parser parser) throws ParserException {
    parser.process(this);
  }

}
