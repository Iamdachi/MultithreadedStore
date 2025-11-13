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
 * Generates random customer orders and submits them to a shared queue
 * for processing by worker threads.
 */
public class OrderGenerator {

    private final List<Product> products;
    private final BlockingQueue<Order> queue;
    private final ExecutorService customers;

    /**
     * Constructs a new {@code OrderGenerator}.
     *
     * @param products       list of products available for orders
     * @param queue          queue to submit generated orders
     * @param threadPoolSize number of customer threads generating orders
     */
    public OrderGenerator(List<Product> products, BlockingQueue<Order> queue, int threadPoolSize) {
        this.products = products;
        this.queue = queue;
        this.customers = Executors.newFixedThreadPool(threadPoolSize);
    }

    /**
     * Starts threads that generate random orders and submit them to the queue.
     * Orders can be normal purchases, reservations, checkouts, or cancellations
     * with randomized probabilities.
     *
     * @param totalOrders total number of orders to generate
     */
    public void startCustomers(int totalOrders) {
        for (int i = 0; i < totalOrders; i++) {
            int orderNumber = i;
            customers.submit(() -> {
                Order order;
                double random = Math.random();
                if (random < 0.2) {
                    order = new ReservationOrder();
                } else if (random < 0.8) {
                    order = new Order();
                } else if (random < 0.8 && orderNumber > 40) {
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
     * Waits for all customer threads to finish generating orders and
     * then inserts poison pills into the queue to signal workers to stop.
     *
     * @param workerCount number of worker threads that will consume orders
     * @throws InterruptedException if the current thread is interrupted while waiting
     */
    public void awaitCompletion(int workerCount) throws InterruptedException {
        customers.shutdown();
        customers.awaitTermination(1, TimeUnit.MINUTES);

        for (int i = 0; i < workerCount; i++) {
            queue.put(Order.POISON);
        }
    }
}

