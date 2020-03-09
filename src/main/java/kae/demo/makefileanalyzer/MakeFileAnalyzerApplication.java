/*
 *
 *
 * Kapralov A.
 * 06.05.15
 */

package kae.demo.makefileanalyzer;

import kae.demo.makefileanalyzer.ast.FileNode;
import kae.demo.makefileanalyzer.parser.Parser;
import kae.demo.makefileanalyzer.parser.ParserException;
import org.apache.commons.cli.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Reader;
import java.util.Set;

/** @author A. Kapralov 06.05.15 20:21 */
public class MakeFileAnalyzerApplication {

  private static final Options options = new Options();
  private static final CommandLineParser cmdLineParser = new DefaultParser();
  private static final HelpFormatter formatter = new HelpFormatter();
  private static final String CMD_LINE_SYNTAX = "kae-test";

  private static final String OPTION_F = "f";

  private static final String OPTION_A = "a";

  private static final String OPTION_R = "r";

  private static final String OPTION_S = "s";

  private static final String OPTION_D = "d";

  private static final String OPTION_O = "o";

  private static final String OPTION_N = "n";

  static {
    options.addOption(OPTION_F, "file", true, "Makefile for analyze");
    options.addOption(OPTION_A, "ast", false, "Print AST of makefile");
    options.addOption(OPTION_R, "root_targets", false, "Print root targets of makefile");
    options.addOption(OPTION_S, "renamesrc", true, "Source name for rename");
    options.addOption(OPTION_D, "renamedst", true, "Destination name for rename");
    options.addOption(OPTION_O, "out", false, "Print result AST");
    options.addOption(OPTION_N, "new", false, "Output file for changed makefile");
  }

  public static void main(String[] args) {
    try {
      final CommandLine cmdLine = cmdLineParser.parse(options, args);

      if (cmdLine.hasOption(OPTION_F)) {
        final String filename = cmdLine.getOptionValue(OPTION_F);
        try (final Reader reader = new BufferedReader(new FileReader(filename))) {
          Parser parser = new Parser(reader);
          final FileNode fileNode = parser.parseAst();
          System.out.println("PARSING SUCCESSFUL\n");

          if (cmdLine.hasOption(OPTION_A)) {
            printAst(fileNode);
          }

          if (cmdLine.hasOption(OPTION_R)) {
            printRootTargets(fileNode);
          }

          if (cmdLine.hasOption(OPTION_S) && cmdLine.hasOption(OPTION_D)) {
            final String src = cmdLine.getOptionValue(OPTION_S);
            final String dst = cmdLine.getOptionValue(OPTION_D);

            rename(fileNode, src, dst);
          }

          if (cmdLine.hasOption(OPTION_O)) {
            printAst(fileNode);
          }

          if (cmdLine.hasOption(OPTION_N)) {
            printCode(fileNode);
          }
        }
      } else {
        System.out.println("Please, specify makefile for analyze using -f or --file option");
        printHelp();
      }
    } catch (ParseException e) {
      printHelp();
    } catch (ParserException e) {
      System.out.printf("PARSER ERROR\n%s\n", e.getMessage());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static void printRootTargets(FileNode fileNode) {
    AstAnalyzer analyzer = new AstAnalyzer();
    final Set<String> rootTargetIds = analyzer.getRootTargetIds(fileNode);
    for (String targetId : rootTargetIds) {
      System.out.print(targetId);
      System.out.print(" ");
    }
    System.out.println();
  }

  private static void rename(FileNode fileNode, String src, String dst) {
    Refactorer refactorer = new Refactorer();
    refactorer.rename(fileNode, src, dst);
  }

  private static void printAst(FileNode fileNode) {
    AstPrinter printer = new AstPrinter(System.out);
    printer.print(fileNode);
    System.out.println();
  }

  private static void printCode(FileNode fileNode) {
    AstCodeGenerator codeGenerator = new AstCodeGenerator(System.out);
    codeGenerator.printMakefile(fileNode);
    System.out.println();
  }

  private static void printHelp() {
    formatter.printHelp(CMD_LINE_SYNTAX, options);
  }
}
