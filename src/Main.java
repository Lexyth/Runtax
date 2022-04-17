import runtax.RuleMap;
import runtax.BasicRuleMap;
import runtax.ParserRuleMap;
import runtax.ParsedRuleMap;
import runtax.Parser;

import java.io.IOException;

class Main {
  public static void main(String[] args) {

    System.out.println("Defining source path.");
    String sourceFile = "src/runtax/rules/test.txt";

    System.out.println("Creating RuleMap.");
    RuleMap ruleMap = new RuleMap();

    System.out.println("Creating BasicRuleMap.");
    BasicRuleMap basicRuleMap = BasicRuleMap.instance();

    System.out.println("Creating ParserRuleMap.");
    ParserRuleMap parserRuleMap = ParserRuleMap.instance();


    ParsedRuleMap parsedRuleMap;
    try {
      System.out.println("Trying to create ParsedRuleMap.");
      parsedRuleMap = new ParsedRuleMap(sourceFile, parserRuleMap);
      System.out.println("Success.");
    } catch (IOException ioe) {
      //either FileNotFound from constructor or IOException from read method
      System.out.println("Failure.");
      ioe.printStackTrace();
      parsedRuleMap = null;
    }

    try {
      System.out.println("Trying to create AST.");
      Parser.parseFile(sourceFile, parserRuleMap);
      System.out.println("Success.");
    } catch (IOException ioe) {
      System.out.println("Failure.");
      ioe.printStackTrace();
    }
    
    System.out.println("RuleMap: " + ruleMap);
    System.out.println("BasicRuleMap: " + basicRuleMap);
    System.out.println("BasicRuleMap.letter: " + basicRuleMap.get("letter"));
    System.out.println("ParserRuleMap: " + parserRuleMap);
    if (parsedRuleMap != null)
      System.out.println("ParsedRuleMap: " + parsedRuleMap);
  }
}
