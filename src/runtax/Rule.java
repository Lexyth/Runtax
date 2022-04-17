package runtax;

import java.util.ArrayList;

import java.util.Arrays;

/**
* 
*/

public class Rule {
  private String regex;
  private String namedRegex;
  private Type type;
  private ArrayList<Entry> rules;

  private enum Type {
    UNKNOWN ("", ""),
    REGEX ("", ""),
    TEXT ("\\Q", "\\E"),
    SEQUENCE ("", ""),
    ONE_OF ("", ""),
    OPTIONAL ("", "?"),
    ZERO_OR_MORE ("", "*?"),
    ONE_OR_MORE ("", "+?"),
    MORE_OR_ZERO ("", "*"),
    MORE_OR_ONE ("", "+");

    private String start;
    private String end;

    private Type (String start, String end) {
      this.start = start;
      this.end = end;
    }

    public String start () {
      return start;
    }

    public String end () {
      return end;
    }
  }

  private Rule (String regex, Type type) {
    if (type == Type.TEXT)
      regex = "\\Q" + regex + "\\E";
    this.regex = regex;
    this.namedRegex = regex;
    this.type = type;
  }

  private Rule (Type type, Entry... entries) throws NullPointerException {
    if (entries == null)
      throw new NullPointerException();
    this.rules = new ArrayList<>(java.util.Arrays.asList(entries));
    this.type = type;
    assemble();
  }
  
  private Rule (Rule rule) {
    regex = rule.regex;
    namedRegex = rule.namedRegex;
    type = rule.type;
    rules = new ArrayList<>(rule.rules);
  }

  public String regex () {
    return regex;
  }

  public String namedRegex () {
    return namedRegex;
  }

  public Type type () {
    return type;
  }

  public java.util.List<Entry> rules () {
    return java.util.Collections.unmodifiableList(rules);
  }

  private void assemble () {
    this.regex = "(" + type.start();
    this.namedRegex = "(" + type.start();
    
    int length = rules.size();
    for (int i = 0; i < length; i++) {
      Entry entry = rules.get(i);
      String name = entry.name();
      Rule rule = entry.rule();
      String regex = rule.regex();
      
      this.regex += regex;
      if (name != null && !name.isEmpty())
        this.namedRegex += "(?<" + name + ">" + regex + ")";
      else
        this.namedRegex += regex;
      
      if (type == Type.ONE_OF && i < length-1) {
        this.regex += "|";
        this.namedRegex += "|";
      }
    }

    this.regex += ")" + type.end();
    this.namedRegex += ")" + type.end();
  }

  @Override
  public String toString () {
    return namedRegex;
  }

  public interface Builder {

    static Entry entry (String name, Rule rule) {
      return new Entry (name, rule);
    }
    
    static Rule regex (String regex) {
      return new Rule (regex, Type.REGEX);
    }

    static Rule text (String text) {
      return new Rule (text, Type.TEXT);
    }

    static Rule sequence (Rule... rules) {
      //probably not perfect...
      return sequence (
        (Entry[])
         Arrays
        .stream(rules)
        .map(elem -> entry ("", elem))
        .toArray(Entry[]::new)
      );
    }

    static Rule sequence (Entry... entries) {
      return new Rule (Type.SEQUENCE, entries);
    }

    static Rule oneOf (Rule... rules) {
      //probably not perfect...
      return oneOf (
        (Entry[])
         Arrays
        .stream(rules)
        .map(elem -> entry ("", elem))
        .toArray(Entry[]::new)
      );
    }

    static Rule oneOf (Entry... entries) {
      return new Rule (Type.ONE_OF, entries);
    }

    static Rule optional (Rule... rules) {
      //probably not perfect...
      return optional (
        (Entry[])
         Arrays
        .stream(rules)
        .map(elem -> entry ("", elem))
        .toArray(Entry[]::new)
      );
    }

    static Rule optional (Entry... entries) {
      return new Rule (Type.OPTIONAL, entries);
    }

    static Rule zeroOrMore (Rule... rules) {
      //probably not perfect...
      return zeroOrMore (
        (Entry[])
         Arrays
        .stream(rules)
        .map(elem -> entry ("", elem))
        .toArray(Entry[]::new)
      );
    }

    static Rule zeroOrMore (Entry... entries) {
      return new Rule (Type.ZERO_OR_MORE, entries);
    }

    static Rule oneOrMore (Rule... rules) {
      //probably not perfect...
      return oneOrMore (
        (Entry[])
         Arrays
        .stream(rules)
        .map(elem -> entry ("", elem))
        .toArray(Entry[]::new)
      );
    }

    static Rule oneOrMore (Entry... entries) {
      return new Rule (Type.ONE_OR_MORE, entries);
    }

    static Rule moreOrZero (Rule... rules) {
      //probably not perfect...
      return moreOrZero (
        (Entry[])
         Arrays
        .stream(rules)
        .map(elem -> entry ("", elem))
        .toArray(Entry[]::new)
      );
    }

    static Rule moreOrZero (Entry... entries) {
      return new Rule (Type.MORE_OR_ZERO, entries);
    }

    static Rule moreOrOne (Rule... rules) {
      //probably not perfect...
      return moreOrOne (
        (Entry[])
         Arrays
        .stream(rules)
        .map(elem -> entry ("", elem))
        .toArray(Entry[]::new)
      );
    }

    static Rule moreOrOne (Entry... entries) {
      return new Rule (Type.MORE_OR_ONE, entries);
    }
  }

  public static class Entry {
    private String name;
    private Rule rule;

    public Entry (String name, Rule rule) {
      this.name = name;
      this.rule = rule;
    }

    public String name() {
      return name;
    }

    public Rule rule() {
      return rule;
    }
  }

}