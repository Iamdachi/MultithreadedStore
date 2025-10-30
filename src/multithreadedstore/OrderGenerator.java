package multithreadedstore;

import java.util.List;
import java.util.concurrent.*;

class OrderGenerator {
    private final List<Product> products;
    private final BlockingQueue<Order> queue;
    private final ExecutorService customers = Executors.newFixedThreadPool(3);

    OrderGenerator(List<Product> products, BlockingQueue<Order> queue) {
        this.products = products;
        this.queue = queue;
    }

    void startCustomers(int count, int totalOrders) {
        for (int i = 0; i < totalOrders; i++) {
            customers.submit(() -> {
                var order = new Order();
                var p = products.get(ThreadLocalRandom.current().nextInt(products.size()));
                order.add(p, 1);
                queue.add(order);
            });
        }
    }

    void awaitCompletion() throws InterruptedException {
        customers.shutdown();
        customers.awaitTermination(1, TimeUnit.MINUTES);
    }
}
