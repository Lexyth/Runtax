package runtax;

import static runtax.Rule.Builder.*;

import java.util.Map;
import java.util.HashMap;

//TODO: Allow subclasses to be made. All subclasses are required to add, or rather override, Rules for most, if not all, already specified Rules. 

/**
* The ParserRuleMap defines the Rules for parsing a Rule from a String according to the specified Rules.
* Only single line Rule definitions are allowed at the moment.
* <p>Example:
<pre>
{@code
#this is a
# comment
#double quotes are evaluated as text
rule1Name= "text"
#single quotes are evaluated as regex
rule2Name ='regex'
#unnecessary spaces are just ignored – start, end and surrounding the first equals sign
 rule3Name = rule1Name rule2Name
#multiple spaces are counted as one space
  rule4Name  =   rule2Name  rule3Name 
#unknown char sequences are evaluated as text, as long as they don't contain a space or true newline
#evaluated as the text 'text'
rule5Name = text
#evaluated as the separate texts "first" and "second", effectively reassembled as "firstsecond", so without space
rule6Name = first second
#evaluated as the text "first second"
rule7Name = "first second"

#empty lines are ignored

#special characters

#immediately following the target without space
# optional : ?
# zeroOrMore : *
# oneOrMore : +

#might not appear or only once
optional = "This text is optional."?
#might not appear or as often as it wants
zeroOrMore = "a"*
#might appear once or more times
oneOrMore = "b"+
#TODO: gotta figure out how lazy and greedy work out...

#surrounded by spaces
# oneOf : |

#TODO: decide between the following two options
# 1: could be "a" or "b" or "cd"
# 2: could be "ad" or "bd" or "cd"
oneOf = "a" | "b" | "c" "d"

#needs space or special character on the outer side
# grouping : ()

#the regex of a variable is surrounded by parentheses when it is referenced
#equivalent to the regex '((((\Qa\E)*)((\QThis text is optional.\E)?) ((\Qb\E)+))|((\QThis text is optional.\E)?(\QThis text is optional.\E)?)+)?'
group = ((zeroOrMore optional oneOrMore) | (optional optional)+)?

#order matters!
#evaluated as the optional text "doIExist"
tooEarly = doIExist?
#variables only exist in lines below the declaration
doIExist = "Only after this line"
}
</pre>
* <p>Note: Singleton because there's no meaning in having multiple instances of it. 
*/
public final class ParserRuleMap extends RuleMap {

  /**
* The only instance of this RuleMap. 
  */
  private static ParserRuleMap instance;


  /**
* Constructs this ParserRuleMap from the hard-coded rules of {@code #init(Map)}
  */
  private ParserRuleMap () {
    super(init());
  }

  /**
  * Utility method to define the hard-coded Rules. 
  * @return A map of hard-coded Rules
  */
  private static Map<String, Rule> init () {
    Map<String, Rule> rules;
    rules = new HashMap<>();

    Rule letter = regex("[a-zA-Z]");
    Rule digit = regex("[0-9]");
    Rule underscore = text("_");
    
    Rule whitespace = regex("\\s");
    Rule space = moreOrOne(whitespace);
    Rule optspace = moreOrZero(whitespace);

    Rule commentSymbol = text("#");

    rules.put(
      "comment",
      optspace
      .rule(commentSymbol)
      .rule(
        "content",
        zeroOrMore(regex("."))
      ).regex("\\R")
    );

    rules.put(
      "openGroup",
      text("(")
    );

    rules.put(
      "closeGroup",
      text(")")
    );

    rules.put(
      "optional",
      text("?")
    );

    rules.put(
      "zeroOrMore",
      text("*")
    );

    rules.put(
      "oneOrMore",
      text("+")
    );

    rules.put(
      "escape",
      text("\\")
    );

    //maybe change in such a way as to allow surrounding definitions for *+? like [oneOrMore]
    rules.put(
      "special",
      oneOf(
        rules.get("openGroup"),
        rules.get("closeGroup"),
        rules.get("optional"),
        rules.get("zeroOrMore"),
        rules.get("oneOrMore"),
        rules.get("escape")
      )
    );

    rules.put(
      "specialAfter",
      oneOf(
        rules.get("optional"),
        rules.get("zeroOrMore"),
        rules.get("oneOrMore")
      )
    );

    rules.put(
      "name",
      sequence(
        oneOf(
          letter,
          underscore
        ),
        moreOrZero(oneOf(
          letter,
          digit,
          underscore
        ))
      )
    );

    Rule equals = text("=");

    rules.put(
      "literal",
      oneOf(
        new Rule.RuleEntry(
          "unquoted",
          moreOrOne(regex("\\S"))
        ),
        new Rule.RuleEntry(
          "quoted",
          text("\"")
          .oneOrMore(
            "content",
            regex(".")
          )
          .regex("[^\\\\]")
          .text("\"")
        )
      )
    );

    rules.put(
      "group",
      sequence(
        rules.get("openGroup"),
        oneOrMore(
          oneOf(
            rules.get("literal")
            //rules.get("name")
          ),
          zeroOrMore(rules.get("specialAfter"))
        ),
        rules.get("closeGroup")
      )
    );
    //optionally change it to text("(").moreOrOne("content", regex("\\S")).text(")") to give the inner Rule a name to be accessed via get("group").get("content")
    
    Rule token = sequence(
      oneOf(
        //rules.get("name"), //already handeled by literal... Had to check if name was just a literal, now I have to check if literal is a valid name. 
        rules.get("literal"),
        rules.get("group")
      ),
      zeroOrMore(rules.get("specialAfter"))
    );

    rules.put(
      "value",
      sequence(
        token,
        zeroOrMore(
          space,
          token
        )
      )
    );

    /*
    rules.put(
      "ruleEntry",
      sequence(
        oneOf(
          regex("^"),
          regex("\\R")
        ),
        optspace,
        rules.get("name"),
        optspace,
        equals,
        optspace,
        rules.get("value"),
        optspace,
        regex("\\R")
      )
    );
    //*/

    rules.put(
      "ruleEntry",
      sequence(optspace)
      .rule("name", rules.get("name"))
      .rule(optspace, equals, optspace)
      .rule("value", rules.get("value"))
      .rule(
        space,
        text(";")
      )
    );

    rules.put(
      "line",
      oneOf(
        new Rule.RuleEntry("comment", rules.get("comment")),
        new Rule.RuleEntry("ruleEntry", rules.get("ruleEntry"))
      )
    );

    //gotta add a way to name the rules in these too...
    rules.put(
      "main",
      moreOrOne(
        rules.get("line")//.name("line") nope, only works with chained instance builders and last element in sequence and oneOf for now...
      )
    );
    
    return rules;
  }

  /**
* Get the single instance this class can have. Creates a new one if none exists yet.
* @return the single instance
  */
  public static ParserRuleMap instance () {
    if (instance == null) 
      instance = new ParserRuleMap();
    return instance;
  }
}
