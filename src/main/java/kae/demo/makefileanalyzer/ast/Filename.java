/*
 * 
 * 
 * Kapralov A.
 * 04.05.15
 */

package kae.demo.makefileanalyzer.ast;

/**
 * File or target name.
 *
 * @author A. Kapralov
 *         04.05.15 17:48
 */
public class Filename {

  /**
   * Unique name.
   */
  private String id;

  public Filename() {
  }

  public Filename(String id) {
    this.id = id;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Filename)) return false;

    Filename filename = (Filename) o;

    return !(id != null ? !id.equals(filename.id) : filename.id != null);
  }

  @Override
  public int hashCode() {
    return id != null ? id.hashCode() : 0;
  }

  @Override
  public String toString() {
    return id;
  }
}
