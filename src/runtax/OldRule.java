package runtax;

import java.util.List;
import java.util.ArrayList;


//TODO: Check if returning a new Rule instead of this Rule and applying changes from the Builder methods to that Rule is sufficient to make this class immutable.

/**
* Rule describing a syntax assembled from contained {@link Rule}s or given by the {@link Rule#regex} field.
* <p>Supports chained building directly and static creation of {@link Rule} objects via the {@link Rule.Builder} interface.
* @deprecated
* @author Lexyth
* @see runtax.RuleMap
* @since 1.0.0
*/
public class Rule {

  /**
* The rules this rule is made of.
*/
  private ArrayList<RuleEntry> rules;

  /**
* The regex assembled from all the rules in {@link Rule#rules} via {@link Rule#assemble()}.
*/
  private String regex;

  /**
* The named regex assembled from all the rules in {@link Rule#rules} via {@link Rule#assemble()}. {@code RuleEntry}s with {@code name}s that are neither null nor return true from {@link String.isEmpty()} are put into named groups in the resulting regex.
*/

  private String namedRegex;

  /**
* A constructor for an empty Rule.
* @see Rule.Builder#regex(String)
*/
  private Rule () {}

  /**
* A constructor used to initialize the {@link Rule#rules} with the RuleEntrys in the given {@code ruleEntryList}.
* @see Rule.Builder#sequence(Rule...)
*/
  private Rule (RuleEntry... ruleEntryList) {
    if (ruleEntryList == null)
      throw new NullPointerException();
    this.rules = new ArrayList<>(java.util.Arrays.asList(ruleEntryList));
    assemble();
  }

  /**
  * A copy-constructor.
  */
  private Rule (Rule rule) {
    this.regex = rule.regex;
    this.namedRegex = rule.namedRegex;
    if (rule.rules != null)
      this.rules = new ArrayList<>(rule.rules);
  }
  
/**
* Get the regex of this rule.
* @return the regex of this rule
*/
  public String regex () {
    return this.regex;
  }

  /**
* Get the named regex of this rule.
* @return the named regex of this rule
*/
  public String namedRegex() {
    return this.namedRegex;
  }

  /**
* Get the rules of this rule. 
* @return the rules of this rule. 
*/
  public List<RuleEntry> rules () {
    if (rules == null)
      return null;
    return java.util.Collections.unmodifiableList(rules);
  }

  /**
* Return a new Rule with the last added Rule associated to the given {@code name}.
* If there are no Rules the new Rule will place the contained regex in a new Rule and associate the {@code name} with that. 
*/
  public Rule name (String name) {

    Rule rule = new Rule (this);
    
    if (rule.rules == null) {
      rule.rules = new ArrayList<RuleEntry>();
      rule.rules.add(new RuleEntry(
        name,
        Builder.regex(rule.regex())
      ));
      rule.assemble();
      return rule;
    }

    int index = rule.rules.size()-1;
    RuleEntry entry = rule.rules.get(index);

    if (entry.name().equals(name))
      return this;
    
    rule.rules.set(
      index,
      new RuleEntry(
        name,
        entry.rule()
      )
    );

    return rule;
  }

  /*
  **
  **instance chained building methods
  **
  */

/**
* Chained building method overloading {@link Rule#rule(String, Rule...)}.
* @param rules The rules to be appended
* @return A copy of this Rule with the {@code rules} appended
* @see Rule#rule(String, Rule...)
*/
  public Rule rule (Rule... rules) {
    return rule("", rules);
  }

  /**
* Chained building method that groups the given {@code rules} in a new rule via the {@link Rule.Builder#sequence(Rule...)} method of the {@link Rule.Builder} interface and appends the result to a copy of this rule as a {@link Rule.RuleEntry}, setting its name to {@code name}.
* @param name The name of the rule to be appended
* @param rules The rules to be appended
* @return A copy of this Rule with the {@code rules} appended
* @see Rule.RuleEntry
* @see Rule.Builder#sequence(Rule...)
* @see Rule#rule(Rule...)
*/
  public Rule rule (String name, Rule... rules) {
    Rule rule = new Rule(this);
    if (rule.rules == null) {
      rule.rules = new ArrayList<>();
      //should never be null in this case... or maybe never at all, so maybe superfluous
      if (rule.regex() != null) {
        rule.rules.add(new RuleEntry("",Builder.regex(rule.regex())));
      }
    }
    rule.rules.add(new RuleEntry(name, Builder.sequence(rules)));
    rule.assemble();
    return rule;
  }

  /**
* Chained building method overloading {@link Rule#regex(String, String)}
* @param regex The regex to be appended
* @return A copy of this Rule with the {@code regex} appended
* @see Rule#regex(String, String)
*/
  public Rule regex (String regex) {
    return regex("", regex);
  }

  /**
* Chained building method that converts the given {@code regex} into a rule via the {@link Rule.Builder#regex(String)} method of the {@link Rule.Builder} interface and appends the result to a copy of this rule as a {@link Rule.RuleEntry}, setting its name to {@code name}.
* @param name The name of the rule to be appended
* @param regex The regex to be appended
* @return A copy of this Rule with the {@code regex} appended
* @see Rule#rule(String, Rule...)
* @see Rule.Builder#regex(String)
* @see Rule#regex(String)
*/
  public Rule regex (String name, String regex) {
    return rule(name, Builder.regex(regex));
  }

  /**
* Chained building method overloading {@link Rule#text(String, String)}
* @param text The text to be appended
* @return A copy of this Rule with the {@code text} appended
* @see Rule#text(String, String)
*/
  public Rule text (String text) {
    return text("", text);
  }

  /**
* Chained building method that converts the given {@code text} into a rule via the {@link Rule.Builder#text(String)} method of the {@link Rule.Builder} interface and appends the result to a copy of this rule as a {@link Rule.RuleEntry}, setting its name to {@code name}.
* @param name The name of the rule to be appended
* @param text The text to be appended
* @return A copy of this Rule with the {@code text} appended
* @see Rule#rule(String, Rule...)
* @see Rule.Builder#text(String)
* @see Rule#text(String)
*/
  public Rule text (String name, String text) {
    return rule(name, Builder.text(text));
  }

  /**
  * Chained building method overloading {@link Rule#oneOf(String, Rule...)}
  * @param rules The rules to be appended
  * @return A copy of this Rule with the {@code rules} appended
  * @see Rule#oneOf(String, Rule...)
  */
  public Rule oneOf (Rule... rules) {
    return oneOf("", rules);
  }

  /**
  * Chained building method that groups the given {@code rules} in a new rule via the {@link Rule.Builder#oneOf(Rule...)} method of the {@link Rule.Builder} interface and appends the result to a copy of this rule as a {@link Rule.RuleEntry}, setting its name to {@code name}.
  * Only one of the {@code rules} has to appear to satisfy this rule.
  * @param name The name of the rule to be appended
  * @param rules The rules to be appended
  * @return A copy of this Rule with the {@code rules} appended
  * @see Rule#rule(String, Rule...)
  * @see Rule.Builder#oneOf(Rule...)
  * @see Rule#oneOf(Rule...)
  */
  public Rule oneOf (String name, Rule... rules) {
    return rule(name, Builder.oneOf(rules));
  }

  /**
  * Chained building method overloading {@link Rule#oneOf(String, RuleEntry...)}
  * @param ruleEntrys The RuleEntrys to be appended
  * @return A copy of this Rule with the {@code ruleEntrys} appended
  * @see Rule#oneOf(String, RuleEntry...)
  */
  public Rule oneOf (RuleEntry... ruleEntrys) {
    return oneOf("", ruleEntrys);
  }

  /**
  * Chained building method that groups the given {@code ruleEntrys} in a new rule via the {@link Rule.Builder#oneOf(RuleEntry...)} method of the {@link Rule.Builder} interface and appends the result to a copy of this rule as a {@link Rule.RuleEntry}, setting its name to {@code name}.
  * Only one of the rules in the {@code ruleEntrys} has to appear to satisfy this rule.
  * @param name The name of the rule to be appended
  * @param ruleEntrys The ruleEntrys to be appended
  * @return A copy of this Rule with the {@code ruleEntrys} appended
  * @see Rule#rule(String, Rule...)
  * @see Rule.Builder#oneOf(RuleEntry...)
  * @see Rule#oneOf(RuleEntry...)
  */
  public Rule oneOf (String name, RuleEntry... ruleEntrys) {
    return rule(name, Builder.oneOf(ruleEntrys));
  }
  
  /**
  * Chained building method overloading {@link Rule#moreOrOne(String, Rule...)}
  * @param rules The rules to be appended
  * @return A copy of this Rule with the {@code rules} appended
  * @see Rule#moreOrOne(String, Rule...)
  * @see Rule#moreOrZero(Rule...)
  */
  public Rule moreOrOne (Rule... rules) {
    return moreOrOne("", rules);
  }

  /**
  * Chained building method that groups the given {@code rules} in a new rule via the {@link Rule.Builder#moreOrOne(Rule...)} method of the {@link Rule.Builder} interface and appends the result to a copy of this rule as a {@link Rule.RuleEntry}, setting its name to {@code name}.
  * The {@code rules} have to appear once or more to satisfy this rule. 
  * This rule takes precedence, so ALL consecutive appearances will be consumed by it. 
  * @param name The name of the rule to be appended
  * @param rules The rules to be appended
  * @return A copy of this Rule with the {@code rules} appended
  * @see Rule#rule(String, Rule...)
  * @see Rule.Builder#moreOrOne(Rule...)
  * @see Rule#moreOrOne(Rule...)
  * @see Rule#moreOrZero(String, Rule...)
  */
  public Rule moreOrOne (String name, Rule... rules) {
    return rule(name, Builder.moreOrOne(rules));
  }
  
  /**
  * Chained building method overloading {@link Rule#oneOrMore(String, Rule...)}
  * @param rules The rules to be appended
  * @return A copy of this Rule with the {@code rules} appended
  * @see Rule#oneOrMore(String, Rule...)
  * @see Rule#zeroOrMore(Rule...)
  */
  public Rule oneOrMore (Rule... rules) {
    return oneOrMore("", rules);
  }

  /**
  * Chained building method that groups the given {@code rules} in a new rule via the {@link Rule.Builder#oneOrMore(Rule...)} method of the {@link Rule.Builder} interface and appends the result to a copy of this rule as a {@link Rule.RuleEntry}, setting its name to {@code name}.
  * The {@code rules} have to appear once or more to satisfy this rule. 
  * Following rules take precedence, so consecutive appearences may be consumed by those instead, except for the first one. 
  * @param name The name of the rule to be appended
  * @param rules The rules to be appended
  * @return A copy of this Rule with the {@code rules} appended
  * @see Rule#rule(String, Rule...)
  * @see Rule.Builder#oneOrMore(Rule...)
  * @see Rule#oneOrMore(Rule...)
  * @see Rule#zeroOrMore(String, Rule...)
  */
  public Rule oneOrMore (String name, Rule... rules) {
    return rule(name, Builder.oneOrMore(rules));
  }

  /**
  * Chained building method overloading {@link Rule#moreOrZero(String, Rule...)}
  * @param rules The rules to be appended
  * @return A copy of this Rule with the {@code rules} appended
  * @see Rule#moreOrZero(String, Rule...)
  * @see Rule#moreOrOne(Rule...)
  */
  public Rule moreOrZero (Rule... rules) {
    return moreOrZero("", rules);
  }

  /**
  * Chained building method that groups the given {@code rules} in a new rule via the {@link Rule.Builder#moreOrZero(Rule...)} method of the {@link Rule.Builder} interface and appends the result to a copy of this rule as a {@link Rule.RuleEntry}, setting its name to {@code name}.
  * The {@code rules} have to appear zero or more time to satisfy this rule. 
  * This rule takes precedence, so ALL consecutive appearances will be consumed by it. 
  * @param name The name of the rule to be appended
  * @param rules The rules to be appended
  * @return A copy of this Rule with the {@code rules} appended
  * @see Rule#rule(String, Rule...)
  * @see Rule.Builder#moreOrZero(Rule...)
  * @see Rule#moreOrZero(Rule...)
  * @see Rule#moreOrOne(String, Rule...)
  */
  public Rule moreOrZero (String name, Rule... rules) {
    return rule(name, Builder.moreOrZero(rules));
  }

  /**
  * Chained building method overloading {@link Rule#zeroOrMore(String, Rule...)}
  * @param rules The rules to be appended
  * @return A copy of this Rule with the {@code rules} appended
  * @see Rule#zeroOrMore(String, Rule...)
  * @see Rule#oneOrMore(Rule...)
  */
  public Rule zeroOrMore (Rule... rules) {
    return zeroOrMore("", rules);
  }

  /**
  * Chained building method that groups the given {@code rules} in a new rule via the {@link Rule.Builder#zeroOrMore(Rule...)} method of the {@link Rule.Builder} interface and appends the result to a copy of this rule as a {@link Rule.RuleEntry}, setting its name to {@code name}.
  * The {@code rules} have to appear zero or more time to satisfy this rule. 
  * Following rules take precedence, so appearences may be consumed by those instead, INCLUDING the first one. 
  * @param name The name of the rule to be appended
  * @param rules The rules to be appended
  * @return A copy of this Rule with the {@code rules} appended
  * @see Rule#rule(String, Rule...)
  * @see Rule.Builder#zeroOrMore(Rule...)
  * @see Rule#zeroOrMore(Rule...)
  * @see Rule#oneOrMore(String, Rule...)
  */
  public Rule zeroOrMore (String name, Rule... rules) {
    return rule(name, Builder.zeroOrMore(rules));
  }

  /**
  * Assemble the {@code Rule#regex} of all Rules into this Rules {@code Rule#regex}.
  * The regex representations for every Rule will be surrounded by parentheses.
  */
  private void assemble () {
    this.regex = "";
    this.namedRegex = "";
    for (int i = 0; i < rules.size(); i++) {
      RuleEntry entry = rules.get(i);
      String name = entry.name();
      Rule rule = entry.rule();
      String regex = rule.regex();

      if (!(name == null || name.isEmpty())) {
        this.namedRegex += "(?<" + name + ">" + regex + ")";
        this.regex += "(" + regex + ")";
      } else if (regex.length() > 1) {
        this.regex += "(" + regex + ")";
        this.namedRegex += "(" + regex + ")";
      } else {
        this.regex += regex;
        this.namedRegex += regex;
      }
    }
  }

  /**
  * Get a Rule by its name.
  * @param name The name associated with a Rule
  * @return The Rule associated with the name
  */
  public Rule get (String name) {
    for (int i = 0; i < rules.size(); i++) {
      if (rules.get(i).name().equals(name)) {
        return rules.get(i).rule();
      }
    }
    //TODO maybe look for name in subrules... or better not
    return null;
  }

  @Override
  public String toString () {
    return regex();
  }

  /**
* The {@code Builder} interface allows static creation of Rule objects.
  */
  public interface Builder {

    /**
    * Constructs a new Rule from the given {@code regex}. 
    * @param regex The regex
    * @return the new Rule
    */
    static Rule regex (String regex) {
      Rule rule = new Rule();
      rule.regex = regex;
      rule.namedRegex = regex;
      return rule;
    }
    
    /**
    * Constructs a new Rule from the given {@code text}.
    * @param text The text
    * @return the new Rule
    */
    static Rule text (String text) {
      return regex ("\\Q" + text + "\\E");
    }
    
    /**
    * Constructs a new Rule from the given {@code rules} or returns the first Rule if only one was given. 
    * @param rules The Rules
    * @return the new Rule
    */
    static Rule sequence (Rule... rules) {
      Rule rule = null;
      if (rules.length == 1) 
          rule = rules[0];
      else if (rules.length > 1) {
        ArrayList<RuleEntry> ruleEntrys = new ArrayList<>();
        for (int i = 0; i < rules.length; i++) {
          ruleEntrys.add(new RuleEntry("",rules[i]));
        }
          rule = new Rule(ruleEntrys.toArray(new RuleEntry[0]));
      }
      return rule;
    }

    /**
    * Constructs a new Rule from the given {@code ruleEntrys} or returns the Rule of the first RuleEntry if only one was given. 
    * @param ruleEntrys The RuleEntrys
    * @return the new Rule
    */
    static Rule sequence (RuleEntry... ruleEntrys) {
      Rule rule = null;
      if (ruleEntrys.length == 1) 
          rule = ruleEntrys[0].rule();
      else if (ruleEntrys.length > 1) {
        rule = new Rule(ruleEntrys);
      }
      return rule;
    }

    /**
    * Constructs a new Rule from the given {@code rules}, requiring only one of them to be satisfied. 
    * @param rules The rules
    * @return the new Rule
    */
    static Rule oneOf (Rule... rules) {

      if (rules.length == 1) {
        return rules[0];
      }
      ArrayList<Rule> separatedRules = new ArrayList<>();
      for (int i = 0; i < rules.length-1; i++) {
        separatedRules.add(rules[i]);
        separatedRules.add(regex("|"));
      }
      separatedRules.add(rules[rules.length-1]);
      return sequence(separatedRules.toArray(new Rule[0]));
    }

        /**
    * Constructs a new Rule from the given {@code ruleEntrys}, requiring only one of them to be satisfied. 
    * @param ruleEntrys The ruleEntrys
    * @return the new Rule
    */
    static Rule oneOf (RuleEntry... ruleEntrys) {

      if (ruleEntrys.length == 1) {
        return ruleEntrys[0].rule();
      }
      ArrayList<RuleEntry> separatedRuleEntrys = new ArrayList<>();
      for (int i = 0; i < ruleEntrys.length-1; i++) {
        separatedRuleEntrys.add(ruleEntrys[i]);
        separatedRuleEntrys.add(new RuleEntry("", regex("|")));
      }
      separatedRuleEntrys.add(ruleEntrys[ruleEntrys.length-1]);
      return sequence(separatedRuleEntrys.toArray(new RuleEntry[0]));
    }

    /**
    * Constructs a new Rule from the given {@code rules}, requiring them to appear consecutively one or more times (greedy).
    * @param rules The rules
    * @return the new Rule
    */
    static Rule moreOrOne (Rule... rules) {
      return sequence(sequence(rules), regex("+"));
    }

    /**
    * Constructs a new Rule from the given {@code ruleEntrys}, requiring them to appear consecutively one or more times (greedy).
    * @param ruleEntrys The ruleEntrys
    * @return the new Rule
    */
    static Rule moreOrOne (RuleEntry... ruleEntrys) {
      return sequence(sequence(ruleEntrys), regex("+"));
    }

    /**
    * Constructs a new Rule from the given {@code rules}, requiring them to appear consecutively one or more times (lazy).
    * @param rules The rules
    * @return the new Rule
    */
    static Rule oneOrMore (Rule... rules) {
      return sequence(sequence(rules), regex("+"), regex("?"));
    }

    /**
    * Constructs a new Rule from the given {@code ruleEntrys}, requiring them to appear consecutively one or more times (lazy).
    * @param ruleEntrys The ruleEntrys
    * @return the new Rule
    */
    static Rule oneOrMore (RuleEntry... ruleEntrys) {
      return sequence(sequence(ruleEntrys), regex("+"), regex("?"));
    }

    /**
    * Constructs a new Rule from the given {@code rules}, requiring them to appear consecutively zero or more times (greedy).
    * @param rules The rules
    * @return the new Rule
    */
    static Rule moreOrZero (Rule... rules) {
      return sequence(sequence(rules), regex("*"));
    }

    /**
    * Constructs a new Rule from the given {@code rules}, requiring them to appear consecutively zero or more times (lazy).
    * @param rules The rules
    * @return the new Rule
    */
    static Rule zeroOrMore (Rule... rules) {
      return sequence(sequence(rules), regex("*"), regex("?"));
    }

    /**
    * Constructs a new Rule from the given {@code rules}, requiring them to appear consecutively zero or one times. 
    * @param rules The rules
    * @return the new Rule
    */
    static Rule optional (Rule... rules) {
      return sequence(sequence(rules), regex("?"));
    }
    
  }
  
  /**
  * An Entry associating a name to a Rule.
  */
  public static class RuleEntry {

    /**
    * The name of this Entry. 
    */
    private String name;

    /**
    * The Rule of this Entry. 
    */
    private Rule rule;

    /**
    * A constructor requiring a name and a rule. 
    * @param name The name associated with this Entry
    * @param rule The Rule associated with this Entry
    */
    public RuleEntry (String name, Rule rule) {
      this.name = name;
      this.rule = rule;
    }

    /**
    * A constructor requiring a rule. The name will be empty.
    * @param rule The Rule associated with this Entry. 
    */
    public RuleEntry (Rule rule) {
      this("", rule);
    }

    /**
    * Get the name of this Entry. 
    * @return the name of this Entry. 
    */
    public String name() {
      return name;
    }

    /**
    * Get the Rule of this Entry. 
    * @return the Rule of this Entry.
    */
    public Rule rule() {
      return rule;
    }
  }
}
