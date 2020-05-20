package lxn.se.reflection;

public class Foo implements FooInterface {
    private String name;
    public String age;

    public Foo() {

    }

    public Foo(String name) {
        this.name = name;
    }

    /**
     * 私有方法
     *
     * @return
     */
    private String printHelloByPrivate(String name) {
        System.out.println(name + "：Hello World by private");
        return "lxn";
    }

    /**
     * 公有方法
     *
     * @param name
     */
    public void printHelloByPublic(String name) {
        System.out.println(name + "：Hello World by public");
    }

    /**
     * 保护权限方法
     *
     * @param name
     */
    protected void printHelloByProtected(String name) {
        System.out.println(name + "：Hello World by protected");
    }

    /**
     * 默认权限方法
     *
     * @param name
     */
    void printHelloByPackage(String name) {
        System.out.println(name + "：Hello World");
    }

    @Override
    public void print() {
        System.out.println(name + ":print Hello");
    }

    @Override
    public void printHello(String name) {
        System.out.println(name + ":print Hello");
    }
}
