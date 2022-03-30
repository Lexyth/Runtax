package runtax;

import java.util.ArrayList;

/**
* Rule describing a syntax assembled from contained {@code Rule}s or given by the {@code Rule#regex} field.
* <p>Supports chained building directly and static creation of {@code Rule} objects via the {@code Rule.Builder} interface.
* @author Lexyth
* @see runtax.RuleSet
* @since 1.0.0
*/
public class Rule {

  //optionally add HashMap of all NAMED Rules to speed up lookup of subrules. Maybe irrelevant due to possibly requiring deep search...

  /**
* The rules this {@code Rule} instance is made of.
*/
  private ArrayList<RuleEntry> rules;

  /**
* The regex assembled from all the rules in {@code Rule#rules} via the {@code Rule#assemble()} method. 
*/
  private String regex;

  /**
* A constructor used to initialize the {@code Rule#rules}. 
* Only used by the static building methods of the inner {@code Rule.Builder} interface.
*/
  private Rule () {
    rules = new ArrayList<>();
  }

  /**
* A constructor used to set the {@code Rule#regex}.
* Only used by the static building methods of the inner {@code Rule.Builder} interface.
* @see Rule#regex
* @see Rule.Builder
* @see Rule.Builder#regex()
*/
  private Rule (String regex) {
    this.regex = regex;
  }

/**
* Get the regex associated with this {@code Rule}. If {@code Rule#regex} is null, call {@code Rule#assemble()} to generate it from the {@code Rule}s in {@code Rule#rules}.
* <p>Assembly may result in an empty String, but not null.
* @return The regex associated with this {@code Rule}, whether given at construction or assembled via {@code Rule#assemble()}
* @see Rule#assemble()
* @see Rule#regex
*/
  public String regex () {
    if (regex == null) {
      assemble();
    }
    return this.regex;
  }

  /*
  **
  **instance chained building methods
  **
  */

  //no need for sequence cause the chained calls adding to the rules arraylist already work like a sequence

  //need 2 of each, with and without name

  //Rule entry

/**
* Chained building method overloading {@code Rule#rule(String, Rule...)}.
* @see Rule#rule(String, Rule...)
* @param rules The rules to be collected in a single rule and appended to this rule
* @return This rule
*/
  public Rule rule (Rule... rules) {
    return rule("", rules);
  }

  /**
* Chained building method that collects the given {@code rules} into a single rule via the {@code Rule.Builder#sequence(Rule...)} method of the {@code Rule.Builder} interface and appends that to this rule as a {@code Rule.RuleEntry}, setting its name to {@code name}.
* @see Rule.Builder#sequence(Rule...)
* @see Rule.RuleEntry
* @see Rule#rule(Rule...)
* @param name The name of the rule to be appended
* @param rules The rules to be collected in a single rule and appended to this rule
* @return This rule
*/
  public Rule rule (String name, Rule... rules) {
    if (rules.length == 1)
      this.rules.add(new RuleEntry(name, rules[0]));
    else if (rules.length > 1)
      this.rules.add(new RuleEntry(name, Builder.sequence(rules)));
    return this;
  }

  /**
* Chained building method overloading {@code Rule#regex(String, String)}
* @see Rule#regex(String, String)
* @param regex The regex to be converted to a rule and appended to this rule
* @return This rule
*/
  public Rule regex (String regex) {
    return regex("", regex);
  }

  /**
* Chained building method that converts the given {@code regex} into a rule via the {@code Rule.Builder#regex(String)} method of the {@code Rule.Builder} interface and appends that to this rule as a {@code Rule.RuleEntry}, setting its name to {@code name}.
* @see Rule.RuleEntry
* @see Rule#rule(String, Rule...)
* @see Rule.Builder#regex(String)
* @see Rule#regex(String)
* @param name The name of the rule to be appended
* @param regex The regex to be converted to a rule and appended to this rule
* @return This rule
*/
  
  public Rule regex (String name, String regex) {
    return rule(name, Builder.regex(regex));
  }

  /**
* Chained building method overloading {@code Rule#text(String, String)}
* @see Rule#text(String, String)
* @param text The text to be converted to a rule and appended to this rule
* @return This rule
*/

  public Rule text (String text) {
    return text("", text);
  }

  /**
* Chained building method that converts the given {@code text} into a rule via the {@code Rule.Builder#text(String)} method of the {@code Rule.Builder} interface and appends that to this rule as a {@code Rule.RuleEntry}, setting its name to {@code name}.
* @see Rule.RuleEntry
* @see Rule#rule(String, Rule...)
* @see Rule.Builder#regex(String)
* @see Rule#text(String)
* @param name The name of the rule to be appended
* @param text The text to be converted to a rule and appended to this rule
* @return This rule
*/
  
  public Rule text (String name, String text) {
    return rule(name, Builder.text(text));
  }

  //oneOf entry
  
  public Rule oneOf (Rule... rules) {
    return oneOf("", rules);
  }

  public Rule oneOf (String name, Rule... rules) {
    /*
    Rule inner = new Rule();
    for (int i = 0; i < rules.length-1; i++) {
      inner.rule(rules[i]);
      inner.regex("|");
    }
    inner.rule(rules[rules.length-1]);
*/
    return rule(name, Builder.oneOf(rules));
  }
  
  //moreOrOne entry (greedy)
  //rework to support varargs
  
  public Rule moreOrOne (Rule rule) {
    return moreOrOne("", rule);
  }

  public Rule moreOrOne (String name, Rule rule) {
    //Rule inner = new Rule ();
    //inner.rule(rule).regex("+");
    return rule(name, Builder.moreOrOne(rule));
  }
  

  //oneOrMore entry (lazy)
  
  public Rule oneOrMore (Rule rule) {
    return oneOrMore("", rule);
  }

  public Rule oneOrMore (String name, Rule rule) {
    //Rule inner = new Rule ();
    //inner.rule(rule).regex("+?");
    return rule(name, Builder.oneOrMore(rule));
  }

  //moreOrZero entry (greedy)

  public Rule moreOrZero (Rule rule) {
    return moreOrZero("", rule);
  }

  public Rule moreOrZero (String name, Rule rule) {
    //Rule inner = new Rule ();
    //inner.rule(rule).regex("*");
    return rule(name, Builder.moreOrZero(rule));
  }

  //zeroOrMore entry (lazy)

  public Rule zeroOrMore (Rule rule) {
    return zeroOrMore("", rule);
  }

  public Rule zeroOrMore (String name, Rule rule) {
    //Rule inner = new Rule ();
    //inner.rule(rule).regex("*?");
    return rule(name, Builder.zeroOrMore(rule));
  }

  /*
  **
  ** method to assemble regex
  **
  */
  
  //assembling rules into regex
  //add paretheses around ALL inner rules! (probably?)

  public void assemble () {
    regex = "";
    for (int i = 0; i < rules.size(); i++) {
      regex += "(" + rules.get(i).rule().regex() + ")";
    }
  }

  /*
  **
  ** method to get a Rule by name
  **
  */

  public Rule get (String name) {
    for (int i = 0; i < rules.size(); i++) {
      if (rules.get(i).name().equals(name)) {
        return rules.get(i).rule();
      }
    }
    //TODO maybe look for name in subrules
    return null;
  }

  //toString
  public String toString () {
    return regex();
  }

  /**
* The {@code Builder} interface allows static creation of Rule objects.
  */
  public interface Builder {
    static Rule regex (String regex) {
      return new Rule (regex);
    }

    static Rule text (String text) {
      return regex ("\\Q" + text + "\\E");
    }

    static Rule sequence (Rule... rules) {
      Rule rule = null;
      if (rules.length == 1) 
          rule = rules[0];
      else if (rules.length > 1) {
        rule = new Rule();
        for (int i = 0; i < rules.length; i++)
          rule.rule(rules[i]);
      }
      return rule;
    }

    static Rule oneOf (Rule... rules) {
      Rule rule = new Rule();
      for (int i = 0; i < rules.length-1; i++) {
        rule.rule(rules[i]).regex("|");
      }
      rule.rule(rules[rules.length-1]);
      return rule;
    }

    static Rule moreOrOne (Rule... rules) {
      return new Rule().rule(rules).regex("+");
    }

    static Rule oneOrMore (Rule... rules) {
      return new Rule().rule(rules).regex("+?");
    }

    static Rule moreOrZero (Rule... rules) {
      return new Rule().rule(rules).regex("*");
    }

    static Rule zeroOrMore (Rule... rules) {
      return new Rule().rule(rules).regex("*?");
    }
  }
  
  /*
  **
  ** RuleEntry class
  **
  */

  class RuleEntry {
    private String name;
    private Rule rule;
    public RuleEntry (String name, Rule rule) {
      this.name = name;
      this.rule = rule;
    }

    public RuleEntry (Rule rule) {
      this("", rule);
    }

    public String name() {
      return name;
    }

    public Rule rule() {
      return rule;
    }
  }
}
