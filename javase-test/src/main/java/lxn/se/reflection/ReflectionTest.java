package lxn.se.reflection;

import java.lang.reflect.*;

/**
 * java反射
 */
public class ReflectionTest {
    /**
     * Class:
     * 1、3种获取方式有什么区别(底层实现方式),什么场景使用
     * 2、Class到底是干嘛的，承担着什么样的角色，做什么用的?
     */
    public static void classInstance() {
        Foo foo = new Foo();
        /**
         * 直接获取
         */
        Class c1 = Foo.class;

        /**
         * 声明一个对象(Foo foo = new Foo())之后才可使用
         * 使用场景：形参是一个对象的时候，public void getClassMsg(Object obj){obj.getClass();}
         */
        Class c2 = foo.getClass();

        System.out.println("c1==c2:" + (c1 == c2));
        Class c3 = null;
        try {
            /**
             * 通过Class内部的方法调取
             * 使用场景：形参是一个类名完整字符串的时候public void getClassMsg(String className){Class.forName("lxn.se.reflection.Foo");}
             */
            c3 = Class.forName("lxn.se.reflection.Foo");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println("c1==c3:" + (c1 == c3));

    }

    /**
     * 通过反射获取方法信息
     */
    public static void printMethodMessage() {
        Foo foo = new Foo();
        Class c = foo.getClass();
//        Method[] methods = String.class.getMethods();
        /**
         * 当前类声明的所有方法(包含公有，私有，保护，默认包访问)
         */
        Method[] declaredMethods = Foo.class.getDeclaredMethods();
        for (Method m : declaredMethods) {
            System.out.println("getDeclaredMethods-----" + m.getModifiers() + "," + m.getReturnType() + "," + m.getName());
            Parameter[] parameters = m.getParameters();
            for (Parameter p : parameters) {
                System.out.println("========" + p.getName());
            }
        }

        /**
         * 获取所有公有方法（包含继承，实现）
         */
        Method[] methods = Foo.class.getMethods();
        for (Method m : methods) {
            System.out.println("methods-----" + m.getReturnType() + "," + m.getName());
        }
    }

    /**
     * 通过反射使用方法
     */
    public static void useMethodByReflection() {
        Class c = null;
        try {
            c = Class.forName("lxn.se.reflection.Foo");
        } catch (ClassNotFoundException e) {
            //需抛出异常，可能会有该类不存在情况
            e.printStackTrace();
        }

        //获取某个方法(无返回参数的)
        Method method = null;
        try {
            method = c.getDeclaredMethod("printHelloByPublic", String.class);
        } catch (NoSuchMethodException e) {
            //需抛出异常，可能会有该方法不存在的情况
            e.printStackTrace();
        }
        try {
            method.invoke(c.newInstance(), "lxn");
        } catch (IllegalAccessException e) {
            //非法参数
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            //当被调用的方法的内部抛出了异常而没有被捕获时，将由此异常接收
            e.printStackTrace();
        } catch (InstantiationException e) {
            //1、当实例化的对象是接口，抽象类的时候
            //2、实例化的对象没有无参构造方法的时候
            e.printStackTrace();
        }
    }

    /**
     * 获取成员变量
     */
    public static void printField() {
        Class c = Foo.class;
//        Field[] fields = c.getFields();
        Field[] fields = c.getDeclaredFields();
        for (Field f : fields) {
            //成员变量的class type
            Class fClass = f.getType();
            /**
             * 成员变量访问修饰符的和
             * 解释：什么都不加 是0 ， public  是1 ，private 是 2 ，protected 是 4，static 是 8 ，final 是 16。
             * 如果是   public  static final  三个修饰的 就是3 个的加和 为 25 。
             */
            f.getModifiers();
            System.out.println("printField-----" + fClass.getName() + "," + f.getName());

        }
    }


    /**
     * 反射与数组
     */
    public static void pirntArray() {
        Class c = Foo[].class;
        System.out.println("c.isArray() = " + c.isArray());

    }

    public static void main(String[] args) {
//        classInstance();//Class获取方式
//        printMethodMessage();//反射获取方法信息
//        useMethodByReflection();//反射使用方法
//        printField();//反射获取成员变量
//        pirntArray();//反射与数组
        printConstructor();
    }

    /**
     * 反射与构造
     */
    public static void printConstructor() {
        Foo foo = new Foo();
        Class c = foo.getClass();
        Constructor[] constructors = c.getConstructors();
        for (Constructor constructor : constructors) {
            System.out.println(constructor.getName());
            Class[] constructorParameterType = constructor.getParameterTypes();
            for(Class cc : constructorParameterType){
                System.out.println(cc.getName());
            }
        }
        try {
            Constructor constructorFoo = c.getConstructor(String.class);
            Foo foo1 = (Foo) constructorFoo.newInstance("lxn");
            foo1.print();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

    }
}
