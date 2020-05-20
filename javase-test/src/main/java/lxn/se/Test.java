package lxn.se;

import lxn.se.reflection.FinalLxn;
import lxn.se.reflection.Foo;

public class Test {
    public static void main(String[] args) {
        testString();
    }

    public static void testString() {
        String str1 = "lxn";
        //当“+”连接符的字符串可知时,依旧是在常量池中生成，故相等
        String str2 = "lx" + "n";
        System.out.println(str1 == str2);

        String str3 = "lx";
        //当“+”连接符的字符串有未知str3这种时，此种方str4式在堆中分配，故不相等
        String str4 = str3 + "n";
        System.out.println(str1 == str4);

        String str5 = new String("lxn");
        String str6 = str5;
        System.out.println(str5 == str6);
        //此时str6指向了常量池中的lxn6，并没有如普通对象那样修改了str5堆内存中的值，故无法改变str5的值
        str6 = "lxn6";
        System.out.println(str5);


        FinalLxn finalLxn = new FinalLxn();
        finalLxn.name = "lxn";
        FinalLxn finalLxn1 = finalLxn;
        finalLxn1.name = "lxn1";
        System.out.println(finalLxn.name);
    }

    public static void test() {
        String str = "lxn";
        test(str);
        System.out.println(str);

        String nStr = new String("lxn");
        test(nStr);
        System.out.println(nStr);

        int i = 200;
        test(i);
        System.out.println(i);

        Foo foo = new Foo();
        foo.age = "200";
        test(foo);
        System.out.println(foo.age);
    }

    public static void test(String name) {
        name = "1";
    }

    public static void test(int name) {
        name = 1;
    }

    public static void test(Foo foo) {
        foo.age = "1";
    }
}
