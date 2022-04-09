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

  /**
* The only instance of this RuleMap. 
  */
  private static BasicRuleMap instance;


  /**
* Constructs this BasicRuleMap from the hard-coded rules of {@code #init(Map)}
  */
  protected BasicRuleMap () {
    super(init(null));
  }
  
  /**
* Constructs this BasicRuleMap from the hard-coded rules of {@code #init(Map)} and the given {@code map}. The Rules in the {@code map} have priority and will override hard-coded Rules with the same key. 
  * @param map A map of additional Rules
  */
  protected BasicRuleMap (Map<String, Rule> map) {
    super(init(map));
  }

  /**
  * Utility method to define the hard-coded Rules and add Rules from the given {@code map}. 
  * @param map A map of additional Rules
  * @return A map of hard-coded and additional Rules
  */
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

  /**
* Get the single instance this class can have. Creates a new one if none exists yet.
* @return the single instance
  */
  public static BasicRuleMap instance () {
    if (instance == null) 
      instance = new BasicRuleMap();
    return instance;
  }
}
