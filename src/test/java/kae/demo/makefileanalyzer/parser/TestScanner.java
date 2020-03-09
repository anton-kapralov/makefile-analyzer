/*
 * 
 * 
 * Kapralov A.
 * 01.05.15
 */

package kae.demo.makefileanalyzer.parser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author A. Kapralov
 *         01.05.15 18:41
 */
public class TestScanner {

  @Test
  public void testNextToken() throws Exception {
    List<String> tokenValues = new ArrayList<>(30);
    tokenValues.add("# Comment start\n");
    tokenValues.add("# Comment continue\n");
    tokenValues.add("all");
    tokenValues.add(":");
    tokenValues.add(" ");
    tokenValues.add("hello");
    tokenValues.add("\n");
    tokenValues.add("hello");
    tokenValues.add(":");
    tokenValues.add(" ");
    tokenValues.add("main.o");
    tokenValues.add("\n");
    tokenValues.add("\t");
    tokenValues.add("g++ main.o -o hello");
    tokenValues.add("\n");
    tokenValues.add("main.o");
    tokenValues.add(":");
    tokenValues.add(" ");
    tokenValues.add("main.cpp");
    tokenValues.add("\n");
    tokenValues.add("\t");
    tokenValues.add("g++ -c main.cpp");
    tokenValues.add("\n");
    tokenValues.add("clean");
    tokenValues.add(":");
    tokenValues.add("\n");
    tokenValues.add("\t");
    tokenValues.add("rm *o hello");
    tokenValues.add("\n");
    tokenValues.add("# Comment end\n");


    final InputStream in = getClass().getResourceAsStream("/simplest.mk");
    Scanner scanner = new Scanner(new BufferedReader(new InputStreamReader(in)));
    Token token;
    int i = 0;
    while (null != (token = scanner.nextToken())) {
      final String tokenValue = tokenValues.get(i);
      Assert.assertEquals(String.valueOf(i), tokenValue, token.getValue());

      i++;
    }
    Assert.assertEquals(tokenValues.size(), i);
  }

}
