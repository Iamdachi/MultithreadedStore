package multithreadedstore.service;

import multithreadedstore.model.Order;
import multithreadedstore.model.Warehouse;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Processes orders from a queue using multiple worker threads and updates the warehouse stock.
 */
public class OrderProcessor {

    private final Warehouse warehouse;
    private final BlockingQueue<Order> queue;
    private final List<Order> processedOrders;
    private final ExecutorService workers;

    /**
     * Creates an OrderProcessor.
     *
     * @param warehouse the warehouse to update
     * @param orderQueue the orderQueue from which orders are taken
     * @param processedOrders the list where processed orders are stored
     * @param workerCount number of worker threads
     */
    public OrderProcessor(Warehouse warehouse, BlockingQueue<Order> orderQueue,
                          List<Order> processedOrders, int workerCount) {
        this.warehouse = warehouse;
        this.queue = orderQueue;
        this.processedOrders = processedOrders;
        this.workers = Executors.newFixedThreadPool(workerCount);
    }

    /**
     * Starts worker threads to process orders from the queue.
     * Stops when poisoned.
     */
    public void startWorkers(int count) {
        for (int i = 0; i < count; i++) {
            workers.submit(() -> {
                try {
                    while (true) {
                        Order order = queue.take();
                        if (order.isPoison()) {
                            break;
                        }
                        warehouse.process(order);
                        synchronized (processedOrders) {
                            processedOrders.add(order);
                        }
                    }
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                }
            });
        }
    }

    /**
     * Waits for all workers to finish processing.
     *
     * @throws InterruptedException if the current thread is interrupted
     */
    public void awaitProcessing() throws InterruptedException {
        workers.shutdown();
        workers.awaitTermination(1, TimeUnit.SECONDS);
    }
}
