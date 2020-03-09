/*
 * 
 * 
 * Kapralov A.
 * 01.05.15
 */

package kae.demo.makefileanalyzer.parser;

/**
 * Recipe (command).
 *
 * @author A. Kapralov
 *         01.05.15 20:34
 */
class Recipe extends Token {

  public Recipe(long id, String value) {
    super(id, value);
  }

  @Override
  public void process(Parser parser) throws ParserException {
    parser.process(this);
  }

}
