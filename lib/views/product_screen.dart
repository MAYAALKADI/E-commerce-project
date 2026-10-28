import 'package:e_commerce_mobile_app/providers/AuthProvider.dart';
import 'package:e_commerce_mobile_app/providers/cart_provider.dart';
import 'package:e_commerce_mobile_app/views/add_product_screen.dart';
import 'package:e_commerce_mobile_app/views/orders_screen.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../models/product_model.dart';

class ProductScreen extends StatefulWidget {
  const ProductScreen({Key? key}) : super(key: key);

  @override
  State<ProductScreen> createState() => _ProductScreenState();
}

class _ProductScreenState extends State<ProductScreen> {
  
  final List<Product> _allDummyProducts = [
    Product(
      id: 1,
      name: 'ساعة ذكية عصرية',
      description: 'ساعة ذكية بشاشة أموليد.',
      price: 99.99,
      imageUrl:
          'https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=500',
      category: 'إلكترونيات',
    ),
    Product(
      id: 2,
      name: 'حذاء رياضي مريح',
      description: 'حذاء مصمم للجري.',
      price: 75.50,
      imageUrl:
          'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=500',
      category: 'أزياء',
    ),
    Product(
      id: 3,
      name: 'سماعات بلوتوث',
      description: 'سماعات عازلة للضوضاء.',
      price: 120.00,
      imageUrl:
          'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=500',
      category: 'إلكترونيات',
    ),
    Product(
      id: 4,
      name: 'حقيبة ظهر أنيقة',
      description: 'حقيبة مقاومة للماء.',
      price: 45.00,
      imageUrl:
          'https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=500',
      category: 'إكسسوارات',
    ),
    Product(
      id: 5,
      name: 'نظارة شمسية',
      description: 'نظارة بتصميم عصري.',
      price: 30.00,
      imageUrl:
          'https://images.unsplash.com/photo-1572635196237-14b3f281503f?w=500',
      category: 'إكسسوارات',
    ),
    Product(
      id: 6,
      name: 'هاتف ذكي حديث',
      description: 'هاتف بمواصفات عالية.',
      price: 699.99,
      imageUrl:
          'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=500',
      category: 'إلكترونيات',
    ),
  ];

  @override
  Widget build(BuildContext context) {
    final authProvider = Provider.of<AuthProvider>(context);

    return Scaffold(
      backgroundColor: const Color(0xFFF8F9FA),
      appBar: AppBar(
        title: const Text(
          'قائمة المنتجات',
          style: TextStyle(
            fontWeight: FontWeight.bold,
            color: Colors.white,
            fontSize: 20,
          ),
        ),
        backgroundColor: const Color(0xFF3F51B5),
        elevation: 0,
        centerTitle: true,
        actions: [
          IconButton(
            icon: const Icon(Icons.receipt_long_rounded, color: Colors.white),
            tooltip: 'الطلبات',
            onPressed: () {
              Navigator.push(
                context,
                MaterialPageRoute(builder: (context) => const OrdersScreen()),
              );
            },
          ),
        ],
      ),
      floatingActionButton: authProvider.userRole == 'ADMIN'
          ? FloatingActionButton(
              backgroundColor: const Color(0xFF3F51B5),
              child: const Icon(Icons.add, color: Colors.white),
              onPressed: () {
                Navigator.push(
                  context,
                  MaterialPageRoute(
                    builder: (context) => const AddProductScreen(),
                  ),
                );
              },
            )
          : null,
      body: Column(
        crossAxisAlignment: CrossAxisAlignment.end,
        children: [
          Padding(
            padding: const EdgeInsets.fromLTRB(20, 20, 20, 10),
            child: const Text(
              'اكتشف أحدث المنتجات المتوفرة',
              style: TextStyle(
                fontSize: 16,
                fontWeight: FontWeight.bold,
                color: Color(0xFF718096),
              ),
              textAlign: TextAlign.right,
            ),
          ),
          const SizedBox(height: 10),

          Expanded(
            child: _allDummyProducts.isEmpty
                ? const Center(
                    child: Text(
                      'لا توجد منتجات حالياً',
                      style: TextStyle(color: Color(0xFF718096), fontSize: 16),
                    ),
                  )
                : GridView.builder(
                    padding: const EdgeInsets.symmetric(
                      horizontal: 16,
                      vertical: 8,
                    ),
                    gridDelegate:
                        const SliverGridDelegateWithFixedCrossAxisCount(
                      crossAxisCount: 2,
                      childAspectRatio: 0.72,
                      crossAxisSpacing: 16,
                      mainAxisSpacing: 16,
                    ),
                    itemCount: _allDummyProducts.length,
                    itemBuilder: (context, index) {
                      final product = _allDummyProducts[index];
                      return Container(
                        decoration: BoxDecoration(
                          color: Colors.white,
                          borderRadius: BorderRadius.circular(16),
                          boxShadow: [
                            BoxShadow(
                              color: Colors.grey.withOpacity(0.06),
                              spreadRadius: 2,
                              blurRadius: 10,
                              offset: const Offset(0, 4),
                            ),
                          ],
                        ),
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Expanded(
                              child: ClipRRect(
                                borderRadius: const BorderRadius.vertical(
                                  top: Radius.circular(16),
                                ),
                                child: Image.network(
                                  product.imageUrl,
                                  fit: BoxFit.cover,
                                  width: double.infinity,
                                  errorBuilder: (context, error, stackTrace) =>
                                      Container(
                                    color: Colors.grey[200],
                                    child: const Icon(
                                      Icons.image_not_supported,
                                      color: Colors.grey,
                                    ),
                                  ),
                                ),
                              ),
                            ),
                            Padding(
                              padding: const EdgeInsets.all(12.0),
                              child: Column(
                                crossAxisAlignment: CrossAxisAlignment.start,
                                children: [
                                  Text(
                                    product.name,
                                    maxLines: 1,
                                    overflow: TextOverflow.ellipsis,
                                    style: const TextStyle(
                                      fontWeight: FontWeight.bold,
                                      fontSize: 15,
                                      color: Color(0xFF2D3748),
                                    ),
                                  ),
                                  const SizedBox(height: 6),
                                  Row(
                                    mainAxisAlignment:
                                        MainAxisAlignment.spaceBetween,
                                    children: [
                                      Text(
                                        '\$${product.price.toStringAsFixed(2)}',
                                        style: const TextStyle(
                                          color: Color(0xFF3F51B5),
                                          fontWeight: FontWeight.bold,
                                          fontSize: 14,
                                        ),
                                      ),
                                      GestureDetector(
                                        onTap: () {
                                          final cartProvider =
                                              Provider.of<CartProvider>(
                                            context,
                                            listen: false,
                                          );
                                          cartProvider.addProductToCart(
                                            product,
                                          );

                                          ScaffoldMessenger.of(
                                            context,
                                          ).showSnackBar(
                                            SnackBar(
                                              content: Text(
                                                'تمت إضافة "${product.name}" إلى الطلب',
                                              ),
                                              backgroundColor: Colors.green,
                                              duration: const Duration(
                                                seconds: 1,
                                              ),
                                            ),
                                          );
                                        },
                                        child: Container(
                                          padding: const EdgeInsets.all(6),
                                          decoration: BoxDecoration(
                                            color: const Color(
                                              0xFF3F51B5,
                                            ).withOpacity(0.1),
                                            borderRadius: BorderRadius.circular(
                                              8,
                                            ),
                                          ),
                                          child: const Icon(
                                            Icons.add,
                                            size: 16,
                                            color: Color(0xFF3F51B5),
                                          ),
                                        ),
                                      ),
                                    ],
                                  ),
                                ],
                              ),
                            ),
                          ],
                        ),
                      );
                    },
                  ),
          ),
        ],
      ),
    );
  }
}