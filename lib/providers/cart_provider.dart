import 'package:flutter/foundation.dart';
import '../models/product_model.dart';

class CartProvider with ChangeNotifier {
  
  final List<Product> _cartItems = [];

  List<Product> get cartItems => _cartItems;


  void addProductToCart(Product product) {
    _cartItems.add(product);
    notifyListeners();
  }

  void clearCart() {
    _cartItems.clear();
    notifyListeners();
  }

  double get totalPrice {
    return _cartItems.fold(0.0, (sum, item) => sum + item.price);
  }
}