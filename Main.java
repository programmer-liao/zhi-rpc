import java.util.HashMap;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        Map<String, Method> map = new HashMap<>();
        map.put("1", new Method1());
        map.put("2", new Method2());
        map.get("1").print();
        map.get("2").print();
    }

}
