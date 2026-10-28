class Order {
  final int id;
  final String customerName;
  final double totalPrice;
  String status; 
  final String createdAt;

  Order({
    required this.id,
    required this.customerName,
    required this.totalPrice,
    required this.status,
    required this.createdAt,
  });

  factory Order.fromJson(Map<String, dynamic> json) {
    return Order(
      id: json['id'] ?? 0,
      customerName: json['customerName'] ?? 'مستخدم',
      totalPrice: (json['totalPrice'] ?? 0.0).toDouble(),
      status: json['status'] ?? 'PENDING',
      createdAt: json['createdAt'] ?? '',
    );
  }
}