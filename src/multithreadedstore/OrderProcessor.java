package multithreadedstore;

import java.util.*;
import java.util.concurrent.*;

class OrderProcessor {
    private final Warehouse warehouse;
    private final BlockingQueue<Order> queue;
    private final List<Order> processed;
    private final ExecutorService workers = Executors.newFixedThreadPool(2);

    OrderProcessor(Warehouse warehouse, BlockingQueue<Order> queue, List<Order> processed) {
        this.warehouse = warehouse;
        this.queue = queue;
        this.processed = processed;
    }

    void startWorkers(int count) {
        for (int i = 0; i < count; i++) {
            workers.submit(() -> {
                try {
                    while (true) {
                        Order o = queue.take();
                        warehouse.process(o);
                        processed.add(o);
                    }
                } catch (InterruptedException ignored) {}
            });
        }
    }

    void awaitProcessing() throws InterruptedException {
        while (!queue.isEmpty()) Thread.sleep(100);
        workers.shutdownNow();
        workers.awaitTermination(1, TimeUnit.SECONDS);
    }
}
