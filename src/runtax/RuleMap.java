package runtax;

import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

import static runtax.Rule.Builder.*;

/**
* A map of Rules.
* <p>Subclasses should follow this format: 
<pre>{@code
import static java.util.Map.entry;
import static runtax.Rule.Builder.regex;

public class Subclass extends RuleMap {

  private Subclass instance;

  protected Subclass () {
    super(Map.ofEntries(
      entry("letter", regex("[a-zA-Z]")),
      entry("digit", regex("[0-9]"))
    ));
  }

  public static Subclass instance () {
    if (instance == null) instance = new Subclass();
    return instance;
  }
}
}
</pre>
* @see runtax.BasicRuleMap
* @see runtax.Rule.Builder
* @see runtax.Rule.Builder#regex(String)
* @author Lexyth
*/
public class RuleMap {
  
  private Map<String, Rule> rules;

  protected RuleMap () {
    rules = new HashMap<>();
  }

  /**
* Constructs a new RuleSet using the Rules from the given {@code ruleSets}. Duplicate keys will be overwritten by the last entry without warning and in no guaranteed order. May be reworked. 
  */
  public RuleMap (RuleMap... ruleMaps) {
    this();
    for (int i = 0; i < ruleMaps.length; i++) {
      for (Map.Entry<String, Rule> entry : ruleMaps[i].rules.entrySet()) {
        add(
          entry.getKey(),
          entry.getValue()
        );
      }
    }
  }
  
  /**
* Constructs a new RuleSet from the given Rules. Duplicate keys will be overwritten by the last entry without warning and in no guaranteed order. May be reworked. Also gotta solve the heap pollution caused by the varargs... later...
  */
  public RuleMap (Map<String, Rule> ruleMap) {
    this();
    for (Map.Entry<String, Rule> entry : ruleMap.entrySet()) {
      add(
        entry.getKey(),
        entry.getValue()
      );
    }
  }

  public Map<String, Rule> rules () {
    return Collections.unmodifiableMap(rules);
  }

  public Rule get(String key) {
    return rules.get(key);
  }

  private void add(String key, Rule rule) {
    rules.put(key, rule);
  }

  @Override
  public String toString() {
    String result = "{\n";
    for (Map.Entry<String, Rule> entry : rules.entrySet()) {
      result += "  '" + entry.getValue() + "'\n";
    }
    result += "}";
    return result;
  }
}
