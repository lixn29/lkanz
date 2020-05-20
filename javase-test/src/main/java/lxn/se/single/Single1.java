package lxn.se.single;

/**
 * 饿汉模式
 */
public class Single1 {
    private static Single1 single1 = new Single1();

    public static Single1 getInstance() {
        return single1;
    }
}
