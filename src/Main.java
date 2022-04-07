import runtax.RuleMap;
import runtax.BasicRuleMap;

class Main {
  public static void main(String[] args) {
    System.out.println("Hello world!");
    BasicRuleMap basic = BasicRuleMap.instance();
    RuleMap main = new RuleMap();
    System.out.println(main.toString());
    System.out.println(basic.toString());

    System.out.println(basic.get("letter"));
  }
}
