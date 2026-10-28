import 'package:e_commerce_mobile_app/services/ApiService.dart';
import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';


class AuthProvider with ChangeNotifier {
  String? _token;
  String? _userRole;
  bool _isLoading = false;

  String? get token => _token;
  String? get userRole => _userRole;
  bool get isLoading => _isLoading;
  bool get isAuthenticated => _token != null;

  Future<bool> login(String email, String password) async {
    _isLoading = true;
    notifyListeners();

    final result = await ApiService.login(email, password);

    _isLoading = false;
    if (result['success']) {
      _token = result['data']['token'];
      _userRole = result['data']['role'];

      final prefs = await SharedPreferences.getInstance();
      await prefs.setString('jwt_token', _token!);
      await prefs.setString('user_role', _userRole!);

      notifyListeners();
      return true;
    } else {
      notifyListeners();
      return false;
    }
  }



  Future<bool> register(String email, String password) async {
  _isLoading = true;
  notifyListeners();

  final result = await ApiService.register(email, password);

  _isLoading = false;
  if (result['success']) {
    _token = result['data']['token'];
    _userRole = result['data']['role'] ?? 'CUSTOMER';

    final prefs = await SharedPreferences.getInstance();
    await prefs.setString('jwt_token', _token!);
    await prefs.setString('user_role', _userRole!);

    notifyListeners();
    return true;
  } else {
    notifyListeners();
    return false;
  }
}

  void logout() async {
    _token = null;
    _userRole = null;
    final prefs = await SharedPreferences.getInstance();
    await prefs.clear();
    notifyListeners();
  }
}