package lxn.sqltest;

import java.sql.*;

/**
 * oracle jdbc连接
 */
public class JdbcTest {
    /**
     * 注意地址写法
     * 1、jdbc表示使用jdbc规范方式连接
     * 2、oracle表示使用oracle
     * 3、thin表示连接方式，引入jar包即可（还有一种为oci，需要安装oracle客户端，因为此种方式是先连接本地地客户端，然后客户端去连接oracle，优点是速度快）
     * 4、@127.0.0.1:1521:lxn表示数据库地址，IP:PORT:dataBaseName
     */
    private static String oracleUrl = "jdbc:oracle:thin:@127.0.0.1:1521:lxn";
    /**
     * 注意地址写法
     * 1、mysql地址为//IP:PORT/dataBaseName，注意mysql使用"/"连接数据库名称
     */
    private static String mysqlUrl = "jdbc:mysql://123.56.87.185:3306/lxn";

    /**
     * Oracle连接
     */
    public static void jdbcTest(String url) {

        String name = "root";
        String pwd = "123456";
        String sql = "select name,pwd from user where name = ?";
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            Class.forName("oracle.jdbc.OracleDriver");
            //获取连接
            connection = DriverManager.getConnection(url, name, pwd);
            //获取预执行sql语句,注意：Statement不能预防sql注入,preparedStatement可以
            preparedStatement = connection.prepareStatement(sql);
            //为占位符填充数据,注意，下标从1开始，这条语句意思是给第一个?赋值a
            preparedStatement.setString(1, "a");
            //执行查询sql语句
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                //以列位置获取
                System.out.println("name = " + resultSet.getString(1));
                //以列的名称获取
                System.out.println("pwd = " + resultSet.getString("pwd"));
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    public static void main(String[] args) {
        jdbcTest(mysqlUrl);
    }
}
