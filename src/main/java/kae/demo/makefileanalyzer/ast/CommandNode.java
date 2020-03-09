/*
 * 
 * 
 * Kapralov A.
 * 03.05.15
 */

package kae.demo.makefileanalyzer.ast;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * Command node.
 *
 * @author A. Kapralov
 *         03.05.15 20:42
 */
public class CommandNode extends AstNode {

  /**
   * List of recipes. E. g. "g++ main.o -o hello".
   */
  private List<String> recipes = new LinkedList<String>();

  public CommandNode() {
  }

  public CommandNode(String recipe) {
    this.recipes.add(recipe);
  }

  public Iterable<String> getRecipes() {
    return recipes;
  }

  /**
   * @return recipes count.
   */
  public int size() {
    return recipes.size();
  }

  /**
   * Returns recipe by index.
   */
  public String get(int index) {
    return recipes.get(index);
  }

  /**
   * Добавляет команду.
   */
  public void add(String recipe) {
    recipes.add(recipe);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof CommandNode)) return false;

    CommandNode that = (CommandNode) o;

    return !(recipes != null ? !recipes.equals(that.recipes) : that.recipes != null);

  }

  @Override
  public int hashCode() {
    return recipes != null ? recipes.hashCode() : 0;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("command{");
    for (Iterator<String> it = recipes.iterator(); it.hasNext(); ) {
      String recipe = it.next();
      sb.append(recipe);
      if (it.hasNext()) {
        sb.append(", ");
      }
    }
    sb.append("}");

    return sb.toString();
  }

  @Override
  public void accept(NodeVisitor visitor) {
    visitor.visit(this);
  }

  @Override
  public List<? extends AstNode> getChildren() {
    return null;
  }

  /**
   * Renames in all recipes source to destination.
   */
  public void rename(String source, String destination) {
    for (ListIterator<String> it = recipes.listIterator(); it.hasNext(); ) {
      String recipe = it.next();
      if (recipe.contains(source)) {
        it.set(recipe.replace(source, destination));
      }
    }
  }
}
