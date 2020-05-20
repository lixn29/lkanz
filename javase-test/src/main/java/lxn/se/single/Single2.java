package lxn.se.single;

/**
 * 懒汉模式
 * 1、线程不安全
 *
 */
public class Single2 {
    private static Single2 single2 = null;

    public static synchronized Single2 getInstance() {
        if (single2 == null) {
            single2 = new Single2();
        }
        return single2;
    }
}
