package multithreadedstore;

import multithreadedstore.model.Order;
import multithreadedstore.model.Product;
import multithreadedstore.model.Report;
import multithreadedstore.model.Warehouse;
import multithreadedstore.service.Analytics;
import multithreadedstore.service.OrderGenerator;
import multithreadedstore.service.OrderProcessor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class Main {

    private static final int ORDER_PROCESSOR_THREADS = 2;
    private static final int CUSTOMER_THREADS = 3;
    private static final int TOTAL_ORDERS = 50;

    /**
     * Entry point for the multithreaded store simulation.
     * Initializes products, warehouse, order generator, and order processor,
     * runs the simulation, and prints an analytics report.
     */
    public static void main(String[] args) throws InterruptedException {
        var products = List.of(
                new Product("Phone", 800),
                new Product("Laptop", 1500),
                new Product("Headphones", 100)
        );

        var warehouse = new Warehouse(products);
        var queue = new LinkedBlockingQueue<Order>();
        var processedOrders = Collections.synchronizedList(new ArrayList<Order>());
        var reservedOrders = Collections.synchronizedList(new ArrayList<Order>());
        var cancelledOrders = Collections.synchronizedList(new ArrayList<Order>());

        var orderProcessor = new OrderProcessor(warehouse, queue, processedOrders, reservedOrders, cancelledOrders, ORDER_PROCESSOR_THREADS);
        var orderGenerator = new OrderGenerator(products, queue, CUSTOMER_THREADS);

        orderProcessor.startWorkers(ORDER_PROCESSOR_THREADS);
        orderGenerator.startCustomers(TOTAL_ORDERS);

        orderGenerator.awaitCompletion(ORDER_PROCESSOR_THREADS);
        orderProcessor.awaitProcessing();

        Report report = Analytics.generateReport(processedOrders, reservedOrders,cancelledOrders, warehouse.getMaxReservedByProduct());

        System.out.println("Total orders: " + report.totalOrders());
        System.out.println("Total reservations: " + report.totalReservations());
        System.out.println("Total cancellations: " + report.totalCancellations());
        System.out.println("Total profit: " + report.totalProfit());
        System.out.println("Top 3 selling products: " + report.top3Products());
        System.out.println("Top 3 selling products: " + report.maxReservedByProduct());
    }
}
