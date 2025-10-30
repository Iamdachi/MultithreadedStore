package multithreadedstore;

import java.util.*;
import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        var products = List.of(
                new Product("Phone", 800),
                new Product("Laptop", 1500),
                new Product("Headphones", 100)
        );
        var warehouse = new Warehouse(products);
        var queue = new LinkedBlockingQueue<Order>();
        var processed = Collections.synchronizedList(new ArrayList<Order>());

        var orderProcessor = new OrderProcessor(warehouse, queue, processed);
        var orderGenerator = new OrderGenerator(products, queue);

        orderProcessor.startWorkers(2);
        orderGenerator.startCustomers(3, 50);

        orderGenerator.awaitCompletion();
        orderProcessor.awaitProcessing();

        Analytics.report(processed);
    }
}
