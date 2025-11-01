package multithreadedstore.service;

import multithreadedstore.model.Order;
import multithreadedstore.model.Product;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * Generates random orders and submits them to a queue for processing.
 */
public class OrderGenerator {

    private final List<Product> products;
    private final BlockingQueue<Order> queue;
    private final ExecutorService customers;

    /**
     * Creates an OrderGenerator.
     *
     * @param products the list of products to generate orders from
     * @param queue the queue to which orders will be submitted
     * @param threadPoolSize number of customer threads to generate orders
     */
    public OrderGenerator(List<Product> products, BlockingQueue<Order> queue, int threadPoolSize) {
        this.products = products;
        this.queue = queue;
        this.customers = Executors.newFixedThreadPool(threadPoolSize);
    }

    /**
     * Starts customer threads generating orders.
     *
     * @param totalOrders number of orders to generate
     */
    public void startCustomers(int totalOrders) {
        for (int i = 0; i < totalOrders; i++) {
            customers.submit(() -> {
                var order = new Order();
                var product = products.get(ThreadLocalRandom.current().nextInt(products.size()));
                order.add(product, 1);
                queue.add(order);
            });
        }
    }

    /**
     * Waits for all customer threads to finish and then adds poison pills to the queue.
     *
     * @param workerCount number of worker threads that will consume orders
     * @throws InterruptedException if the current thread is interrupted
     */
    public void awaitCompletion(int workerCount) throws InterruptedException {
        customers.shutdown();
        customers.awaitTermination(1, TimeUnit.MINUTES);

        for (int i = 0; i < workerCount; i++) {
            queue.put(Order.POISON);
        }
    }

}
