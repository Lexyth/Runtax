package runtax;

import static runtax.Rule.Builder.*;

import java.util.Map;
import java.util.HashMap;

/**
* The most basic map of Rules immaginable. 
* Only contains rules for a letter and a digit. 
* Note: Singleton because there's no meaning in having multiple instances of it. 
*/
public class BasicRuleMap extends RuleMap {
  
  private static BasicRuleMap instance;
  
  protected BasicRuleMap () {
    Map<String, Rule> rules = new HashMap<>();
    rules.put(
      "letter",
      regex("\\w")
    );
    rules.put(
      "digit",
      regex("\\d")
    );
  }

  public static BasicRuleMap instance () {
    if (instance == null) 
      instance = new BasicRuleMap();
    return instance;
  }
}
