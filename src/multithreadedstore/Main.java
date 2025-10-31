package multithreadedstore;

import java.util.*;
import java.util.concurrent.*;

public class Main {

    private static final int ORDER_PROCESSOR_THREADS = 2;
    private static final int CUSTOMER_THREADS = 3;
    private static final int TOTAL_ORDERS = 50;

    public static void main(String[] args) throws InterruptedException {
        var products = List.of(
                new Product("Phone", 800),
                new Product("Laptop", 1500),
                new Product("Headphones", 100)
        );

        var warehouse = new Warehouse(products);
        var queue = new LinkedBlockingQueue<Order>();
        var processedOrders = Collections.synchronizedList(new ArrayList<Order>());

        var orderProcessor = new OrderProcessor(warehouse, queue, processedOrders, ORDER_PROCESSOR_THREADS);
        var orderGenerator = new OrderGenerator(products, queue, CUSTOMER_THREADS);

        orderProcessor.startWorkers(ORDER_PROCESSOR_THREADS);
        orderGenerator.startCustomers(TOTAL_ORDERS);

        orderGenerator.awaitCompletion(ORDER_PROCESSOR_THREADS);
        orderProcessor.awaitProcessing();

        Analytics.Report report = Analytics.generateReport(processedOrders);

        System.out.println("Total orders: " + report.getTotalOrders());
        System.out.println("Total profit: " + report.getTotalProfit());
        System.out.println("Top 3 selling products: " + report.getTop3Products());
    }
}
