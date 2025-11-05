package multithreadedstore.service;

import multithreadedstore.model.Order;
import multithreadedstore.model.Product;
import multithreadedstore.model.ReservationCancellationOrder;
import multithreadedstore.model.ReservationCheckoutOrder;
import multithreadedstore.model.ReservationOrder;

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
            int orderNumber = i;
            customers.submit(() -> {
                // like 90 % chance this is gonna be order
                Order order;
                var random = Math.random();
                if (random < 0.1) {
                    order = new ReservationOrder();
                } else if(random >= 0.1 && random < 0.7) {
                    order = new Order();
                } else if (random >= 0.7 && orderNumber > 30) {
                    order = new ReservationCheckoutOrder();
                } else {
                    order = new ReservationCancellationOrder();
                }
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
