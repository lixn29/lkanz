package lxn.se.map;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Map
 */
public class MapTest {

    /**
     * 遍历traversal
     */
    public static void testTraversal() {
        Map<String, String> hashMap = new HashMap<>();
        for (int i = 0; i < 5; i++) {
            hashMap.put("key" + i, "lxn" + i);
        }
        //循环遍历，可获取到key与value
        for (Map.Entry<String, String> entry : hashMap.entrySet()) {
            System.out.println("entrySet = " + entry.getKey() + "," + entry.getValue());
        }
        //循环遍历key
        for (String key : hashMap.keySet()) {
            System.out.println("key = " + key + "," + hashMap.get(key));
        }
        //循环遍历value
        for (String value : hashMap.values()) {
            System.out.println("value = " + value);
        }
        Iterator<Map.Entry<String, String>> entryItertor = hashMap.entrySet().iterator();
        while (entryItertor.hasNext()) {
            Map.Entry<String, String> entry = entryItertor.next();
            System.out.println("entryItertor = " + entry.getKey() + "," + entry.getValue());
        }
    }

    public static void test(){
        Map<String,String> hashMap = new HashMap<>();
    }

    public static void main(String[] args) {
        testTraversal();
    }
}
