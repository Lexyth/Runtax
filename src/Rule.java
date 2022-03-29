package runtax;

import java.util.ArrayList;

public class Rule {

  //optionally add HashMap of all NAMED Rules to speed up lookup of subrules. Maybe irrelevant due to possibly requiring deep search...

  private ArrayList<RuleEntry> rules;
  private String regex;

  public Rule () {
    rules = new ArrayList<>();
  }

  public Rule (String regex) {
    this.regex = regex;
  }

  //may be renamed to 'regex' if setRegex is removed
  public String getRegex () {
    if (regex == null) {
      assemble();
    }
    return this.regex;
  }

  //just in case... may be removed
  public void setRegex (String regex) {
    this.regex = regex;
  }

  /*
  **
  **instance chained builder methods
  **
  */

  //may be reworked to call the static construction methods where possible

  //no need for sequence cause the chained calls adding to the rules arraylist already work like a sequence

  //need 2 of each, one named the other not

  //Rule entry

  //rework to support varargs
  public Rule rule (Rule... rules) {
    return rule("", rules);
  }

  public Rule rule (String name, Rule... rules) {
    Rule inner;
    if (rules.length == 1) {
      inner = rules[0];
    } else {
      inner = new Rule();
      for (int i = 0; i < rules.length; i++) {
        inner.rule(rules[i]);
      }
    }
    this.rules.add(new RuleEntry(name, inner));
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
      regex += "(" + rules.get(i).rule().getRegex() + ")";
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
    return getRegex();
  }

  /*
  **
  ** static Builder class
  **
  */

  public interface Builder {
    static Rule regex (String regex) {
      return new Rule (regex);
    }

    static Rule text (String text) {
      return regex ("\\Q" + text + "\\E");
    }

    static Rule sequence (Rule... rules) {
      return new Rule().rule(rules);
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
