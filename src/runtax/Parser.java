package runtax;

import java.util.Map;
import java.util.HashMap;

import java.util.List;
import java.util.ArrayList;

import java.io.File;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Parser {

  private Parser() {}

  private static String readFile (Path path) throws IOException {
    return Files.readString(path);
  }

  public static AST parseFile (String filePath, RuleMap ruleMap) throws IOException {
    return parseFile(Paths.get(filePath), ruleMap);
  }

  public static AST parseFile (File file, RuleMap ruleMap) throws IOException {
    return parseFile(file.toPath(), ruleMap);
  }
  
  public static AST parseFile (Path path, RuleMap ruleMap) throws IOException {
    return parseSource(readFile(path), ruleMap);
  }
  
  public static AST parseSource (String source, RuleMap ruleMap) throws IOException {
    return toAST(source, ruleMap.get("main"));
  }

  private static AST toAST (String text, Rule rule) {
      System.out.println("Converting to AST: " + rule.namedRegex());
    
    Pattern pattern = Pattern.compile(rule.namedRegex());
    Matcher matcher = pattern.matcher(text);
    List<Rule.RuleEntry> ruleEntrys = new ArrayList<Rule.RuleEntry>(rule.rules());

    System.out.println("Looking for matches.");
    while (matcher.find()) {
      System.out.println("Found a match.");
      for (int i = 0; i < ruleEntrys.size(); i++) {
        System.out.println("Testing index: " + i);

        Rule.RuleEntry entry = ruleEntrys.get(i);

        if (entry.rule().namedRegex() != null)
          System.out.println("With regex: " + entry.rule().namedRegex());

        if (entry.rule().rules() != null)
          System.out.println("With number of Rules: " + entry.rule().rules().size());

        System.out.println("With name: " + entry.name());
        
        String name = entry.name();
        if (name == null || name.isEmpty())
          continue;

        System.out.println("Testing group: " + name);
        String match = null;
        if ((match = matcher.group(name)) != null) {
          //add to AST
          System.out.println("Success.");
          toAST(match, entry.rule());
          System.out.println(match);
        } else {
          System.out.println("Failure.");
        }
      }
    }
    System.out.println("Completed conversion to AST.");
    return new AST();
  }

  //just a placeholder to get rid of errors
  static class AST {}
}