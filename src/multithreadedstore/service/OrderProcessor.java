package multithreadedstore.service;

import multithreadedstore.model.Order;
import multithreadedstore.model.Warehouse;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Handles concurrent order processing using a pool of worker threads.
 * Each worker takes orders from a blocking queue and updates the shared
 * {@link Warehouse} accordingly. The processor supports normal, reservation,
 * checkout, and cancellation orders.
 */
public class OrderProcessor {

    private final Warehouse warehouse;
    private final BlockingQueue<Order> queue;
    private final List<Order> processedOrders;
    private final List<Order> reservedOrders;
    private final List<Order> cancelledOrders;
    private final ExecutorService workers;

    /**
     * Constructs a new {@code OrderProcessor}.
     *
     * @param warehouse        warehouse to update
     * @param orderQueue       queue supplying orders to process
     * @param processedOrders  list to collect successfully processed orders
     * @param reservedOrders   list to collect successfully reserved orders
     * @param cancelledOrders  list to collect successfully cancelled reservations
     * @param workerCount      number of worker threads to run
     */
    public OrderProcessor(Warehouse warehouse, BlockingQueue<Order> orderQueue,
                          List<Order> processedOrders, List<Order> reservedOrders,
                          List<Order> cancelledOrders, int workerCount) {
        this.warehouse = warehouse;
        this.queue = orderQueue;
        this.processedOrders = processedOrders;
        this.reservedOrders = reservedOrders;
        this.cancelledOrders = cancelledOrders;
        this.workers = Executors.newFixedThreadPool(workerCount);
    }

    /**
     * Starts worker threads that continuously consume and process orders
     * from the queue until a poison order is encountered.
     *
     * @param count number of worker threads to start
     */
    public void startWorkers(int count) {
        for (int i = 0; i < count; i++) {
            workers.submit(() -> {
                try {
                    while (true) {
                        Order order = queue.take();
                        if (order.isPoison()) break;

                        synchronized (warehouse) {
                            if (order.isReservationOrder() && warehouse.reserveProduct(order)) {
                                reservedOrders.add(order);
                            } else if (order.isReservationCancellationOrder() && warehouse.cancelReservation(order)) {
                                cancelledOrders.add(order);
                            } else if (order.isReservationCheckoutOrder() && warehouse.checkoutReservation(order)) {
                                processedOrders.add(order);
                            } else if (warehouse.process(order)) {
                                processedOrders.add(order);
                            }
                        }
                    }
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                }
            });
        }
    }

    /**
     * Waits for all workers to finish processing queued orders.
     *
     * @throws InterruptedException if interrupted while waiting
     */
    public void awaitProcessing() throws InterruptedException {
        workers.shutdown();
        workers.awaitTermination(1, TimeUnit.SECONDS);
    }
}

