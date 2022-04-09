package runtax;

import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

/**
* A map of Rules.
* <p>Subclasses should follow this format: 
<pre>{@code
package runtax;

import static runtax.Rule.Builder.*;

import java.util.Map;
import java.util.HashMap;

public class BasicRuleMap extends RuleMap {

  private static BasicRuleMap instance;

  protected BasicRuleMap () {
    super(init(null));
  }
  
  protected BasicRuleMap (Map<String, Rule> map) {
    super(init(map));
  }

  private static Map<String, Rule> init (Map<String, Rule> map) {
    Map<String, Rule> rules;
    if (map != null)
      rules = new HashMap<>(map);
    else
      rules = new HashMap<>();

    rules.put(
      "letter",
      regex("[a-zA-Z]")
    );

    rules.put(
      "digit",
      regex("[0-9]")
    );
    
    return rules;
  }

  public static BasicRuleMap instance () {
    if (instance == null) 
      instance = new BasicRuleMap();
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

  /**
* Constructs a new RuleMap using the Rules from the given {@code ruleMaps}. Duplicate keys will be overwritten by the last entry without warning and in no guaranteed order. May be reworked.
  * @param ruleMaps The RuleMaps with the Rules
* @see java.util.Map#putAll(Map)
  */
  public RuleMap (RuleMap... ruleMaps) {
    rules = new HashMap<>();
    for (int i = 0; i < ruleMaps.length; i++) {
      rules.putAll(ruleMaps[i].rules());
    }
  }
  
  /**
* Constructs a new RuleMap from the Rules in the given {@code map}. Duplicate keys will be overwritten by the last entry without warning and in no guaranteed order. May be reworked.
  * @param map The Map with the Rules
* @see java.util.Map#putAll(Map)
  */
  public RuleMap (Map<String, Rule> map) {
    rules = new HashMap<>(map);
  }

  /**
* Returns an unmodifiable view of the Rules contained in this RuleMap.
* @return a view of the Rules
* @see java.util.Collections#unmodifiableMap(Map)
  */
  
  public Map<String, Rule> rules () {
    return Collections.unmodifiableMap(rules);
  }

  /**
* Returns a Rule by its associated name. 
* @param name The name of the Rule
* @return the Rule with this name
  */
  public Rule get(String name) {
    return rules.get(name);
  }

  /**
* {inheritDoc}
  */
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
