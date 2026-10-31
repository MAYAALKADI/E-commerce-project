class Product {
  final int id;
  final String name;
  final String description;
  final double price;
  final String imageUrl;
  final String category; 
  Product({
    required this.id,
    required this.name,
    required this.description,
    required this.price,
    required this.imageUrl,
    required this.category,
  });

  factory Product.fromJson(Map<String, dynamic> json) {
    return Product(
      id: json['id'] ?? 0,
      name: json['name'] ?? '',
      description: json['description'] ?? '',
      price: (json['price'] ?? 0.0).toDouble(),
      imageUrl: json['imageUrl'] ?? 'https://via.placeholder.com/150',
      category: json['category'] ?? 'الكل', 
    );
  }
}