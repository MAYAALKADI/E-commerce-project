# Performance results: my_orders (GET /api/v1/orders/my-orders)

Dataset: 100,000 orders | Load: 20 VUs, 30s

| Stage                    | avg     | p50     | p95     | p99     | max     |
|--------------------------|---------|---------|---------|---------|---------|
| Before                   | 1.19 s  | 1.10 s  | 2.00 s  | 3.22 s  | 3.69 s  |
| After Index              | 371 ms  | 327 ms  | 677 ms  | 882 ms  | 1.38 s  |
| After Index + BatchSize  | 67.6 ms | 45.6 ms | 204 ms  | 309 ms  | 483 ms  |

EXPLAIN: type ALL / 99,922 rows  ->  type ref / 93 rows
N+1: ~100 queries on order_product_ids  ->  1 query