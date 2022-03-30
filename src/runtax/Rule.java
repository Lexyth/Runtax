package runtax;

import java.util.ArrayList;

/**
* Rule describing a syntax assembled from contained {@code Rule}s or given by the {@code regex} field.
* <p>Supports chained building and static creation of {@code Rule} objects. 
* @author Lexyth
* @see RuleSet
* @since 1.0.0
*/

public class Rule {

  //optionally add HashMap of all NAMED Rules to speed up lookup of subrules. Maybe irrelevant due to possibly requiring deep search...

  /**
* The rules this {@code Rule} instance is made of.
*/
  private ArrayList<RuleEntry> rules;

  /**
* The regex assembled from all the rules in {@code rules} via the {@code assemble} method. 
*/
  private String regex;

  /**
* A constructor used to initialize the {@code rules}. 
* Only used by the static building methods of the inner {@code Builder} interface.
*/
  private Rule () {
    rules = new ArrayList<>();
  }

  /**
* A constructor used to set the {@code regex}.
* Only used by the static building methods of the inner {@code Builder} interface.
*/
  private Rule (String regex) {
    this.regex = regex;
  }

  //may be renamed to 'regex' if setRegex is removed
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

  public Rule rule (Rule... rules) {
    return rule("", rules);
  }

  public Rule rule (String name, Rule... rules) {
    if (rules.length == 1)
      this.rules.add(new RuleEntry(name, rules[0]));
    else if (rules.length > 1)
      this.rules.add(new RuleEntry(name, Builder.sequence(rules)));
    return this;
  }

  //regex entry
  public Rule regex (String regex) {
    return regex("", regex);
  }
  
  public Rule regex (String name, String regex) {
    return rule(name, Builder.regex(regex));
  }

  //text entry

  public Rule text (String text) {
    return text("", text);
  }
  
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
