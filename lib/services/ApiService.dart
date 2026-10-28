import 'dart:convert';
import 'package:e_commerce_mobile_app/models/order_model.dart';
import 'package:e_commerce_mobile_app/models/product_model.dart';
import 'package:http/http.dart' as http;

class ApiService {
 
  static const String baseUrl = 'http://10.0.2.2:8080/api/v1';

  static Future<Map<String, dynamic>> login(String email, String password) async {
    final url = Uri.parse('$baseUrl/auth/login');
    
    try {
      final response = await http.post(
        url,
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({
          'email': email,
          'password': password,
        }),
      );

      if (response.statusCode == 200) {
        return {'success': true, 'data': jsonDecode(response.body)};
      } else {
        final errorData = jsonDecode(response.body);
        return {
          'success': false, 
          'message': errorData['message'] ?? 'فشل تسجيل الدخول'
        };
      }
    } catch (e) {
      return {'success': false, 'message': 'تعذر الاتصال بالسيرفر'};
    }
  }



  static Future<Map<String, dynamic>> register(String email, String password) async {
  final url = Uri.parse('$baseUrl/auth/register');
  
  try {
    final response = await http.post(
      url,
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode({
        'email': email,
        'password': password,
      }),
    );

    if (response.statusCode == 200 || response.statusCode == 201) {
      return {'success': true, 'data': jsonDecode(response.body)};
    } else {
      final errorData = jsonDecode(response.body);
      return {
        'success': false, 
        'message': errorData['message'] ?? 'فشل إنشاء الحساب'
      };
    }
  } catch (e) {
    return {'success': false, 'message': 'تعذر الاتصال بالسيرفر'};
  }
}



static Future<List<Product>> getProducts(String token) async {
  final url = Uri.parse('$baseUrl/products');
  
  try {
    final response = await http.get(
      url,
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer $token', 
      },
    );

    if (response.statusCode == 200) {
      Iterable data = jsonDecode(response.body);
      return data.map((json) => Product.fromJson(json)).toList();
    } else {
      throw Exception('فشل في جلب المنتجات');
    }
  } catch (e) {
    throw Exception('تعذر الاتصال بالسيرفر: $e');
  }
}


static Future<Map<String, dynamic>> addProduct(
    String token, String name, String description, double price, String imageUrl) async {
  final url = Uri.parse('$baseUrl/products');

  try {
    final response = await http.post(
      url,
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer $token', 
      },
      body: jsonEncode({
        'name': name,
        'description': description,
        'price': price,
        'imageUrl': imageUrl,
      }),
    );

    if (response.statusCode == 200 || response.statusCode == 201) {
      return {'success': true, 'message': 'تم إضافة المنتج بنجاح'};
    } else {
      final errorData = jsonDecode(response.body);
      return {
        'success': false,
        'message': errorData['message'] ?? 'فشل إضافة المنتج'
      };
    }
  } catch (e) {
    return {'success': false, 'message': 'تعذر الاتصال بالسيرفر: $e'};
  }
}


static Future<List<Order>> getOrders(String token) async {
  final url = Uri.parse('$baseUrl/orders');
  try {
    final response = await http.get(
      url,
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer $token',
      },
    );

    if (response.statusCode == 200) {
      Iterable data = jsonDecode(response.body);
      return data.map((json) => Order.fromJson(json)).toList();
    } else {
      throw Exception('فشل في جلب الطلبات');
    }
  } catch (e) {
    throw Exception('تعذر الاتصال بالسيرفر: $e');
  }
}

static Future<bool> createOrder(String token, List<Map<String, dynamic>> items) async {
  final url = Uri.parse('$baseUrl/orders');
  try {
    final response = await http.post(
      url,
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer $token',
      },
      body: jsonEncode({'items': items}),
    );
    return response.statusCode == 200 || response.statusCode == 201;
  } catch (e) {
    return false;
  }
}

static Future<bool> updateOrderStatus(String token, int orderId, String newStatus) async {
  final url = Uri.parse('$baseUrl/orders/$orderId/status');
  try {
    final response = await http.patch(
      url,
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer $token',
      },
      body: jsonEncode({'status': newStatus}),
    );
    return response.statusCode == 200;
  } catch (e) {
    return false;
  }
}
}