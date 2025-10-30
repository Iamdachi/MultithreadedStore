package multithreadedstore;
import java.util.*;

class Order {
    private final Map<Product, Integer> items = new HashMap<>();
    void add(Product p, int qty) { items.put(p, qty); }
    Map<Product, Integer> items() { return items; }
}