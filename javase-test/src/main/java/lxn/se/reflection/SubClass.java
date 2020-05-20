package lxn.se.reflection;

import lxn.se.reflection.Foo;

public class SubClass extends Foo {
    public static void main(String[] args) {

    }

    public void test() {
        printHelloByPublic("lxn");
        //同包下可调用，非此lxn.se.reflection路径的子类不可调用
        printHelloByPackage("lxn");
    }
}
