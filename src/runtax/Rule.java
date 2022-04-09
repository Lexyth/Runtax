package runtax;

import java.util.ArrayList;


//TODO: Check if returning a new Rule instead of this Rule and applying changes from the Builder methods to that Rule is sufficient to make this class immutable.

/**
* Rule describing a syntax assembled from contained {@link Rule}s or given by the {@link Rule#regex} field.
* <p>Supports chained building directly and static creation of {@link Rule} objects via the {@link Rule.Builder} interface.
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
* A constructor used to initialize the {@link Rule#rules} with the RuleEntrys in the given {@code ruleEntryList}.
* @see Rule.Builder#sequence(Rule...)
*/
  private Rule (ArrayList<RuleEntry> ruleEntryList) {
    this.rules = ruleEntryList;
    assemble();
  }

  /**
* A constructor used to set the {@link Rule#regex}.
* @see Rule.Builder#regex(String)
*/
  private Rule (String regex) {
    this.regex = regex;
  }

  /**
  * A copy-constructor.
  */
  private Rule (Rule rule) {
    this.regex = rule.regex;
    this.rules = rule.rules;
  }

/**
* Get the regex associated with this rule.
* @return The regex associated with this rule
*/
  public String regex () {
    return this.regex;
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
  public void assemble () {
    regex = "";
    for (int i = 0; i < rules.size(); i++) {
      regex += "(" + rules.get(i).rule().regex() + ")";
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
      return new Rule (regex);
    }

    /**
    * Constructs a new Rule from the given {@code text}.
    * @param text The text
    * @return the new Rule
    */
    static Rule text (String text) {
      return new Rule ("\\Q" + text + "\\E");
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
      return sequence((Rule[])separatedRules.toArray());
    }

    /**
    * Constructs a new Rule from the given {@code rules}, requiring them to appear consecutively one or more times (greedy).
    * @param rules The rules
    * @return the new Rule
    */
    static Rule moreOrOne (Rule... rules) {
      return sequence(sequence(rules),regex("+"));
    }

    /**
    * Constructs a new Rule from the given {@code rules}, requiring them to appear consecutively one or more times (lazy).
    * @param rules The rules
    * @return the new Rule
    */
    static Rule oneOrMore (Rule... rules) {
      return sequence(sequence(rules), regex("+?"));
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
      return sequence(sequence(rules), regex("*?"));
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
