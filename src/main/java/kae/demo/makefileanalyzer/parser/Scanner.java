/*
 * 
 * 
 * Kapralov A.
 * 01.05.15
 */

package kae.demo.makefileanalyzer.parser;

import java.io.IOException;
import java.io.Reader;

/**
 * Tokens scanner.
 *
 * @author A. Kapralov
 *         01.05.15 18:16
 */
class Scanner {

  private static final char EOF = '\uffff';
  private Reader reader;

  /**
   * Last read character.
   */
  private char ch;

  private Token token;

  /**
   * Current cursor position - row.
   */
  private int currentRow;
  /**
   * Current cursor position - column.
   */
  private int currentCol;
  /**
   * Start cursor position of current token - row.
   */
  private int startRow;
  /**
   * Start cursor position of current token - column.
   */
  private int startCol;

  /**
   * Tokens counter for ID generation.
   */
  private int tokensCounter;

  public Scanner(Reader reader) {
    this.reader = reader;
  }

  /**
   * Reads and returns next token or null if end of file reached.
   */
  public Token nextToken() throws IOException, ScannerException {
    if (ch == '\0') {
      read();
    }

    startRow = currentRow;
    startCol = currentCol - 1;

    final Token extra = readExtra();
    if (extra != null) {
      return extra;
    }

    if (ch == EOF) {
      this.token = null;
    } else {
      Delimiter delimiter = scanDelimiter();
      if (delimiter == null) {
        if (this.token != null && this.token instanceof Delimiter && isRecipePrefix((Delimiter) this.token)) {
          this.token = scanRecipe();
        } else if (isCommentStart()) {
          this.token = scanComment();
        } else if (isFilenameCharacter()) {
          this.token = scanFilename();
        } else if (ch == EOF) {
          this.token = null;
        } else {
          throw new ScannerException(String.format("Illegal character: '%c'", ch));
        }
      } else {
        this.token = delimiter;
      }
    }

    return this.token;
  }

  private long nextId() {
    return ++tokensCounter;
  }

  /**
   * Reads next token and recalculates cursor position.
   */
  private void read() throws IOException {
    ch = (char) reader.read();
    recalculateCurrentPosition();
  }

  /**
   * Recalculates cursor position.
   */
  private void recalculateCurrentPosition() {
    switch (ch) {
      case '\r':
        currentCol = 1;
        break;
      case '\n':
        ++currentRow;
        currentCol = 1;
        break;
      default:
        ++currentCol;
        break;
    }
  }

  /**
   * Read spaces or lines split.
   */
  private Token readExtra() throws IOException, ScannerException {
    if (ch == ' ') {
     StringBuilder sb = new StringBuilder();
      while (ch == ' ') {
        sb.append(ch);
        read();
      }

      return new Whitespaces(nextId(), sb.toString());
    } else if (ch == '\\') {
      StringBuilder sb = new StringBuilder();

      while (ch == '\\') {
        sb.append(ch);
        read();
        if (ch == '\r') {
          sb.append(ch);
          read();
          if (ch == '\n') {
            sb.append(ch);
            read();
          } else {
            throw new ScannerException("Illegal escape sequence: '\\\\r'");
          }
        } else if (ch == '\n') {
          sb.append(ch);
          read();
        } else {
          throw new ScannerException(String.format("Illegal escape sequence: '\\%c'", ch));
        }
      }

      return new LineSplit(nextId(), sb.toString());
    } else {
      return null;
    }
  }

  /**
   * Skips insignificant characters, e.g. spaces and comments.
   */
  private void skipInsignificantChars() throws IOException, ScannerException {

  }

//  /**
//   * Skips all characters to next line.
//   */
//  private void skipToNextLine() throws IOException {
//    while (ch != '\r' && ch != '\n' && ch != EOF) {
//      read();
//    }
//
//    skipNewLine();
//  }

  /**
   * Read all characters to next line.
   */
  private String readToNextLine() throws IOException {
    StringBuilder sb = new StringBuilder();
    while (ch != '\r' && ch != '\n' && ch != EOF) {
      sb.append(ch);
      read();
    }

    readNewLine(sb);

    return sb.toString();
  }

  /**
   * Skips LF or CRLF.
   */
  private void skipNewLine() throws IOException {
    if (ch == '\r') {
      read();
    }

    if (ch == '\n') {
      read();
    }
  }

  /**
   * Skips LF or CRLF.
   */
  private void readNewLine(StringBuilder sb) throws IOException {
    if (ch == '\r') {
      sb.append(ch);
      read();
    }

    if (ch == '\n') {
      sb.append(ch);
      read();
    }
  }

  /**
   * Returns one-based index of row contains current token
   */
  public int getStartRow() {
    return startRow + 1;
  }

  /**
   * Returns one-based index of column where starts current token
   */
  public int getStartCol() {
    return startCol;
  }

  /**
   * Attempts to read delimiter.
   * @return scanned delimiter or null, if current token is not delimiter.
   */
  private Delimiter scanDelimiter() throws IOException {
    Delimiter delimiter = null;
    switch (ch) {
      case ':':
        delimiter = new Colon(nextId());
        break;
      case ';':
        delimiter = new Semicolon(nextId());
        break;
      case '\t':
        delimiter = new RecipePrefix(nextId(), Delimiter.TAB);
        break;
      case '\r':
        read();
        if (ch == '\n') {
          delimiter = new NewLine(nextId(), Delimiter.CRLF);
        }
        break;
      case '\n':
        delimiter = new NewLine(nextId(), Delimiter.LF);
        break;
    }

    if (delimiter != null) {
      read();
    }

    return delimiter;
  }

  /**
   * Check current character for being character of comment start.
   */
  private boolean isCommentStart() {
    return ch == '#';
  }

  /**
   * Read comment.
   */
  private Token scanComment() throws IOException {
    final String value = readToNextLine();
    return new Comment(nextId(), value);
  }

  /**
   * Check current character for being character of file or target name.
   */
  private boolean isFilenameCharacter() {
    return ch >= 'A' && ch <= 'Z' || ch >= 'a' && ch <= 'z' || ch >= '0' && ch <= '9' ||
        ch == '_' || ch == '.' || ch == '$' || ch == '-';
  }

  /**
   * Read file or target name.
   */
  private Identifier scanFilename() throws IOException {
    StringBuilder sb = new StringBuilder();
    while (isFilenameCharacter()) {
      sb.append(ch);
      read();
    }

    return new Identifier(nextId(), sb.toString());
  }

  /**
   * Check read delimiter for being recipe prefix.
   */
  private boolean isRecipePrefix(Delimiter delimiter) {
    return delimiter.getValue().equals(Delimiter.TAB);
  }

  /**
   * Read recipe (command).
   */
  private Recipe scanRecipe() throws IOException {
    StringBuilder sb = new StringBuilder();
    while (ch != '\r' && ch != '\n' && ch != EOF) {
      if (ch != '\\') {
        sb.append(ch);
        read();
      } else {
        read();
        skipNewLine();
      }
    }

    return new Recipe(nextId(), sb.toString());
  }

}
