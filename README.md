# Multithreaded Store
There is a product catalog – a list of objects with price and quantity in stock.
Several customers create orders at the same time using multithreading (Runnable or ExecutorService).
The warehouse is a shared resource (ConcurrentHashMap<Product, Integer>).
Several warehouse workers process orders taken from a BlockingQueue<Order>.
After all orders are processed, run analytics in parallel (parallelStream) to show:
Total number of orders; Total profit; Top 3 best selling products;

## Project Structure
```bash
multithreadedstore/
├── src/
│   └── multithreadedstore/
│       ├── model/
│       │   ├── Product.java
│       │   ├── Order.java
│       │   └── Report.java
│       │
│       ├── service/
│       │   ├── Warehouse.java
│       │   ├── OrderGenerator.java
│       │   ├── OrderProcessor.java
│       │   └── Analytics.java
│       │
│       └── Main.java
└── out/
```

## How to Run

1. Open a terminal in the project root directory.
2. Create an output directory for compiled classes:

```bash
mkdir -p out
```

3. Compile all Java files into that directory:
```bash
javac -d out $(find src -name "*.java")
```

4. Run the program using the out directory as the classpath:
```bash
java -cp out multithreadedstore.Main
```

You should see a summary of total orders, total profit, and the top 3 selling products printed to the console.