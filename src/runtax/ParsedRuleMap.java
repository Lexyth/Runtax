package runtax;

import java.util.Map;
import java.util.HashMap;

import java.util.ArrayList;

import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
* {inheritDoc}
* <p>The ParsedRuleMap allows for RuleSets to be assembles from text files following the format specified by the given ParserRuleMap.
* <p>Note: The {@code name} of a Rule will ALWAYS be looked for first, so every format is required to define the {@code name} in an unambiguous manner.
* <p>Example:
<pre>
{@code
myName = myValue myValue2 myValue3
name{myName}, value{myValue, myValue2}
value=myValue3 name=myName
myName {myValue1:myValue2}
#some of these formats introduce various limitations, but those are unrelated to the RuleSet parsing process.
}
</pre>
*/
public class ParsedRuleMap extends RuleMap {

  public ParsedRuleMap (String[] lines, RuleMap ruleMap) {
    super(parse(lines, ruleMap));
  }

  public ParsedRuleMap (File file, RuleMap ruleMap) throws IOException {
    this(read(file), ruleMap);
  }

  public ParsedRuleMap (String filePath, RuleMap ruleMap) throws IOException {
    this(new File(filePath), ruleMap);
  }
  
  private static String[] read (File file) throws IOException {
    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
      String line = null;
      ArrayList<String> lines = new ArrayList<>();
      while ((line = br.readLine()) != null) {
        lines.add(line);
      }
    return lines.toArray(new String[0]);
    }
  }
  
  private static Map<String, Rule> parse (String[] lines, RuleMap ruleMap) {
    String source = "";
    for (int i = 0; i < lines.length; i++) {
      source += lines[i] + "\n";
    }
    return parse(source, ruleMap);
  }

  private static Map<String, Rule> parse (String source, RuleMap ruleMap) {

    Map<String, Rule> rules = new HashMap<>();

    Pattern linePattern = Pattern.compile(ruleMap.get("line").namedRegex());

    Pattern ruleEntryPattern = Pattern.compile(ruleMap.get("ruleEntry").namedRegex());

    Pattern commentPattern = Pattern.compile(ruleMap.get("comment").namedRegex());

    Matcher sourceMatcher = linePattern.matcher(source);

    while (sourceMatcher.find()) {
      String ruleEntry = null;
      String comment = null;
      if ((ruleEntry = sourceMatcher.group("ruleEntry")) != null) {
        System.out.println("RuleEntry:");
        Matcher ruleEntryMatcher = ruleEntryPattern.matcher(ruleEntry);
        if (ruleEntryMatcher.find()) {
          System.out.println("Name: " + ruleEntryMatcher.group("name"));
          System.out.println("Rule: " + ruleEntryMatcher.group("value"));
        }
        System.out.println();
      } else if ((comment = sourceMatcher.group("comment")) != null) {
        System.out.println("Comment:");
        Matcher commentMatcher = commentPattern.matcher(comment);
        if (commentMatcher.find()) {
          System.out.println("Content: " + commentMatcher.group("content"));
        }
        System.out.println();
      }
    }
    
    /*Pattern ruleEntryPattern = Pattern.compile(ruleMap.get("ruleEntry").namedRegex());

    Matcher sourceMatcher = linePattern.matcher(source);
    
    while (sourceMatcher.find()) {
      String line = sourceMatcher.group();
      Matcher ruleEntryMatcher = ruleEntryPattern.matcher(line);
      if (ruleEntryMatcher.find()) {
        String name = ruleEntryMatcher.group("name");
        String value = ruleEntryMatcher.group("value");
        rules.put(name, Rule.Builder.text(value));
        System.out.println("Rule: " + line);
      } else {
        System.out.println("Comment: " + line);
      }
    }*/
    
    return rules;
  }
}