import http from 'k6/http';
import { check, sleep } from 'k6';

const BASE = 'http://localhost:8080';
const CATEGORIES = ['electronics', 'computers', 'clothes', 'books', 'home', 'sports', 'toys', 'beauty'];

export const options = {
  scenarios: {
    products_list: {
      executor: 'constant-vus',
      exec: 'productsList',
      vus: 20,
      duration: '30s',
      startTime: '0s',
    },
    product_detail: {
      executor: 'constant-vus',
      exec: 'productDetail',
      vus: 20,
      duration: '30s',
      startTime: '35s',
    },
    my_orders: {
      executor: 'constant-vus',
      exec: 'myOrders',
      vus: 20,
      duration: '30s',
      startTime: '70s',
    },
  },
  thresholds: {
    'http_req_duration{endpoint:products_list}': ['max>=0'],
    'http_req_duration{endpoint:product_detail}': ['max>=0'],
    'http_req_duration{endpoint:my_orders}': ['max>=0'],
  },
  summaryTrendStats: ['avg', 'min', 'med', 'p(90)', 'p(95)', 'p(99)', 'max'],
};

// بيشتغل مرة وحدة: تسجيل دخول 20 مستخدم وهمي وجمع التوكنات
export function setup() {
  const tokens = [];
  for (let i = 1; i <= 20; i++) {
    const res = http.post(
      `${BASE}/api/v1/users/login`,
      JSON.stringify({ email: `seeduser${i}@test.com`, password: 'Seed@123' }),
      { headers: { 'Content-Type': 'application/json' } }
    );
    if (res.status === 200) {
      tokens.push(res.json().token);
    }
  }
  if (tokens.length === 0) {
    throw new Error('Login failed for all users');
  }
  return { tokens };
}

export function productsList() {
  const cat = CATEGORIES[Math.floor(Math.random() * CATEGORIES.length)];
  const res = http.get(`${BASE}/api/v1/products?category=${cat}`, {
    tags: { endpoint: 'products_list' },
  });
  check(res, { 'products list 200': (r) => r.status === 200 });
  sleep(0.1);
}

export function productDetail() {
  const id = 3 + Math.floor(Math.random() * 50);
  const res = http.get(`${BASE}/api/v1/products/${id}`, {
    tags: { endpoint: 'product_detail' },
  });
  check(res, { 'product detail 200': (r) => r.status === 200 });
  sleep(0.1);
}

export function myOrders(data) {
  const token = data.tokens[Math.floor(Math.random() * data.tokens.length)];
  const res = http.get(`${BASE}/api/v1/orders/my-orders`, {
    headers: { Authorization: `Bearer ${token}` },
    tags: { endpoint: 'my_orders' },
  });
  check(res, { 'my orders 200': (r) => r.status === 200 });
  sleep(0.1);
}