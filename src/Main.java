import runtax.RuleSet;
import runtax.BasicRuleSet;

class Main {
  public static void main(String[] args) {
    System.out.println("Hello world!");
    BasicRuleSet basic = BasicRuleSet.instance();
    RuleSet main = RuleSet.instance();
    System.out.println(main.toString());
    System.out.println(basic.toString());

    System.out.println(basic.get("letter"));
  }
}
