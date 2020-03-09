/*
 * 
 * 
 * Kapralov A.
 * 03.05.15
 */

package kae.demo.makefileanalyzer.parser;

/**
 * Recipe (command) prefix (For defaults tab character)
 *
 * @author A. Kapralov
 *         03.05.15 22:47
 */
class RecipePrefix extends Delimiter {

  protected RecipePrefix(long id, String value) {
    super(id, value);
  }

  @Override
  public void process(Parser parser) throws ParserException {
    parser.process(this);
  }

}
