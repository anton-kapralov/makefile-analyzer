/*
 * 
 * 
 * Kapralov A.
 * 01.05.15
 */

package kae.demo.makefileanalyzer.parser;

/**
 * Abstract delimiter.
 *
 * @author A. Kapralov
 *         01.05.15 20:34
 */
abstract class Delimiter extends Token {

  public static final String COLON = ":";
  public static final String SEMICOLON = ";";
  public static final String TAB = "\t";
  public static final String CRLF = "\r\n";
  public static final String LF = "\n";

  protected Delimiter(long id, String value) {
    super(id, value);
  }

}
