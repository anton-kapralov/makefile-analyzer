/*
 * 
 * 
 * Kapralov A.
 * 04.05.15
 */

package kae.demo.makefileanalyzer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Dependencies graph
 *
 * @author A. Kapralov
 *         04.05.15 19:44
 */
public class DependenciesGraph {

  private final ArrayList<String> targetIds;

  private boolean[][] dependencies;
  private final Map<String, Integer> idToIdxMap;

  /**
   * @param targetIds targets names list
   */
  public DependenciesGraph(ArrayList<String> targetIds) {
    this.targetIds = targetIds;
    final int size = targetIds.size();
    dependencies = new boolean[size][size];
    idToIdxMap = new HashMap<>(size);
    for (int i = 0; i < targetIds.size(); i++) {
      String targetId = targetIds.get(i);
      idToIdxMap.put(targetId, i);
    }
  }

  /**
   * Returns graph nodes count.
   */
  public int size() {
    return targetIds.size();
  }

  /**
   * Returns target name by index.
   */
  public String getId(int i) {
    return targetIds.get(i);
  }

  /**
   * Returns target index in this graph by name.
   */
  public Integer getIndex(String id) {
    return idToIdxMap.get(id);
  }

  /**
   * Returns dependency relation of two nodes.
   * @param aIdx first node index.
   * @param bIdx second node index.
   * @return true - first node depends of second node, false - first node doesn't depend of second node.
   */
  public boolean isDepend(int aIdx, int bIdx) {
    return aIdx < dependencies.length && bIdx < dependencies.length && dependencies[aIdx][bIdx];
  }

  /**
   * Sets dependency relation of two nodes.
   * @param aIdx first node index.
   * @param bIdx second node index.
   * @param depends true - first node depends of second node, false - first node doesn't depend of second node.
   */
  public void setDependency(int aIdx, int bIdx, boolean depends) {
    if (aIdx < dependencies.length && bIdx < dependencies.length) {
      dependencies[aIdx][bIdx] = depends;
    }
  }

}
