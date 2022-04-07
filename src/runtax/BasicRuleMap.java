package runtax;

import static runtax.Rule.Builder.*;

import java.util.Map;
import static java.util.Map.entry;

/**
* The most basic map of Rules immaginable. 
* Only contains rules for a letter and a digit. 
* Note: Singleton because there's no meaning in having multiple instances of it. 
*/
public class BasicRuleMap extends RuleMap {
  
  private static BasicRuleMap instance;
  
  protected BasicRuleMap () {
    super(Map.ofEntries(
      entry(
        "letter",
        regex("[a-zA-Z]")
      ),
      entry(
        "digit",
        regex("[0-9]")
      )
    ));
  }

  public static BasicRuleMap instance () {
    if (instance == null) 
      instance = new BasicRuleMap();
    return instance;
  }
}
