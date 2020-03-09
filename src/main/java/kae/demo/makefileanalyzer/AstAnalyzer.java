/*
 * 
 * 
 * Kapralov A.
 * 04.05.15
 */

package kae.demo.makefileanalyzer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import kae.demo.makefileanalyzer.ast.FileNode;
import kae.demo.makefileanalyzer.ast.IdListNode;
import kae.demo.makefileanalyzer.ast.IdNode;
import kae.demo.makefileanalyzer.ast.RuleNode;
import kae.demo.makefileanalyzer.ast.TargetNode;

/**
 * Semantic AST analyzer.
 *
 * @author A. Kapralov
 *         04.05.15 17:43
 */
public class AstAnalyzer  {

  /**
   * Returns all targets names.
   */
  public ArrayList<String> getTargetIds(FileNode fileNode) {
    final Iterable<RuleNode> ruleNodes = fileNode.getRuleNodes();
    ArrayList<String> targetIds = new ArrayList<String>(fileNode.size());
    for (RuleNode ruleNode : ruleNodes) {
      final Iterable<IdNode> idNodes = ruleNode.getTargetNode().getIdNodes();
      for (IdNode idNode : idNodes) {
        targetIds.add(idNode.getFilename().getId());
      }
    }
    return targetIds;
  }

  /**
   * Builds dependencies graph.
   */
  public DependenciesGraph buildDependencyGraph(FileNode fileNode) {
    final ArrayList<String> targetIds = getTargetIds(fileNode);
    final DependenciesGraph dependenciesGraph = new DependenciesGraph(targetIds);

    final Iterable<RuleNode> ruleNodes = fileNode.getRuleNodes();
    for (RuleNode ruleNode : ruleNodes) {
      final TargetNode targetNode = ruleNode.getTargetNode();
      final Iterable<IdNode> targetIdNodes = targetNode.getIdNodes();
      final IdListNode prerequisitesNode = targetNode.getPrerequisitesNode();
      if (prerequisitesNode == null) {
        continue;
      }

      final Iterable<IdNode> prerequisitesIdNodes = prerequisitesNode.getIdNodes();
      for (IdNode targetIdNode : targetIdNodes) {
        final Integer targetIdx = dependenciesGraph.getIndex(targetIdNode.getFilename().getId());

        for (IdNode prerequisiteIdNode : prerequisitesIdNodes) {
          final Integer prerequisiteIdx = dependenciesGraph.getIndex(
              prerequisiteIdNode.getFilename().getId());
          if (prerequisiteIdx != null) {
            dependenciesGraph.setDependency(targetIdx, prerequisiteIdx, true);
          }
        }
      }
    }

    return dependenciesGraph;
  }

  /**
   * Returns root targets names.
   */
  public Set<String> getRootTargetIds(FileNode fileNode) {
    return getRootTargetIds(buildDependencyGraph(fileNode));
  }

  /**
   * Returns root targets names by dependencies graph.
   */
  public Set<String> getRootTargetIds(DependenciesGraph dependenciesGraph) {
    final int size = dependenciesGraph.size();
    Set<String> result = new HashSet<String>(size);
    for (int i = 0; i < size; i++) {
      boolean dependencyOfAnyTarget = false;
      for (int j = 0; j < size; j++) {
        if (dependenciesGraph.isDepend(j, i)) {
          dependencyOfAnyTarget = true;
          break;
        }
      }
      if (!dependencyOfAnyTarget) {
        result.add(dependenciesGraph.getId(i));
      }
    }
    return result;
  }

}
