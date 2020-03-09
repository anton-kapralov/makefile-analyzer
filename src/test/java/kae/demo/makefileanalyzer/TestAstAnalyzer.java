/*
 * 
 * 
 * Kapralov A.
 * 04.05.15
 */

package kae.demo.makefileanalyzer;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import kae.demo.makefileanalyzer.ast.FileNode;
import kae.demo.makefileanalyzer.parser.Parser;

/**
 * @author A. Kapralov
 *         04.05.15 17:44
 */
public class TestAstAnalyzer {

  private FileNode fileNode;
  private AstAnalyzer analyzer;

  @Before
  public void setUp() throws Exception {
    final InputStream in = getClass().getResourceAsStream("/simplest.mk");
    Parser parser = new Parser(new BufferedReader(new InputStreamReader(in)));
    fileNode = parser.parseAst();

    analyzer = new AstAnalyzer();
  }

  @Test
  public void testGetTargetIds() throws Exception {
    final ArrayList<String> targetIds = analyzer.getTargetIds(fileNode);
    Assert.assertNotNull(targetIds);
    Assert.assertEquals(4, targetIds.size());
    int idx = -1;
    Assert.assertEquals("all", targetIds.get(++idx));
    Assert.assertEquals("hello", targetIds.get(++idx));
    Assert.assertEquals("main.o", targetIds.get(++idx));
    Assert.assertEquals("clean", targetIds.get(++idx));
  }

  @Test
  public void testBuildDependencyGraph() throws Exception {
    final DependenciesGraph dependenciesGraph = analyzer.buildDependencyGraph(fileNode);
    Assert.assertNotNull(dependenciesGraph);

    boolean[][] expected = new boolean[][] {
        {false, true, false, false},
        {false, false, true, false},
        {false, false, false, false},
        {false, false, false, false}
    };

    for (int i = 0; i < expected.length; i++) {
      for (int j = 0; j < expected.length; j++) {
        Assert.assertEquals(String.format("[%d][%d]", i, j), expected[i][j],
            dependenciesGraph.isDepend(i, j));
      }
    }
  }

  @Test
  public void testGetRootTargetIds() throws Exception {
    ArrayList<String> targetIds = new ArrayList<String>(4);
    targetIds.add("all");
    targetIds.add("hello");
    targetIds.add("main.o");
    targetIds.add("clean");

    final DependenciesGraph dependenciesGraph = new DependenciesGraph(targetIds);
    boolean[][] dependencies = new boolean[][] {
        {false, true, false, false},
        {false, false, true, false},
        {false, false, false, false},
        {false, false, false, false}
    };
    for (int i = 0; i < dependencies.length; i++) {
      for (int j = 0; j < dependencies.length; j++) {
        dependenciesGraph.setDependency(i, j, dependencies[i][j]);
      }
    }

    final Set<String> rootTargetIds =
        analyzer.getRootTargetIds(dependenciesGraph);


    Assert.assertNotNull(rootTargetIds);
    Assert.assertEquals(2, rootTargetIds.size());
    Assert.assertTrue(rootTargetIds.contains("all"));
    Assert.assertTrue(rootTargetIds.contains("clean"));
  }

}
