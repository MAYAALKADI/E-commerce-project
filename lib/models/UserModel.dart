class UserModel {
  final String email;
  final String? token;
  final String role; 

  UserModel({
    required this.email,
    this.token,
    required this.role,
  });

  factory UserModel.fromJson(Map<String, dynamic> json) {
    return UserModel(
      email: json['email'] ?? '',
      token: json['token'],
      role: json['role'] ?? 'CUSTOMER',
    );
  }
}