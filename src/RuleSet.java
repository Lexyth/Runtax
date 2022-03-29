package runtax;

import java.util.HashMap;

import runtax.Rule;
import static runtax.Rule.Builder.*;

public class RuleSet {
  private static RuleSet instance;
  private HashMap<String, Rule> rules;

  protected RuleSet () {
    rules = new HashMap<>();
    add(
      "anything",
      regex(".")
    );
  }

  public static RuleSet instance () {
    if (instance == null)
      instance = new RuleSet();
    return instance;
  }

  public HashMap<String, Rule> rules () {
    return rules;
  }

  public Rule get(String key) {
    return rules().get(key);
  }

  public void add(String key, Rule rule) {
    rules().put(key, rule);
  }

  @Override
  public String toString() {
    String result = "{\n";
    for (java.util.Map.Entry entry : rules().entrySet()) {
      result += "  '" + entry.getValue() + "'\n";
    }
    result += "}";
    return result;
  }
}
