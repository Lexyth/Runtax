package runtax;

import runtax.Rule;
import static runtax.Rule.Builder.*;

public class BasicRuleSet extends RuleSet {
  
  private static BasicRuleSet instance;
  
  protected BasicRuleSet () {
    add(
      "letter",
      regex("\\w")
    );
    add(
      "digit",
      regex("\\d")
    );
  }

  public static BasicRuleSet instance () {
    if (instance == null) 
      instance = new BasicRuleSet();
    return instance;
  }
}
