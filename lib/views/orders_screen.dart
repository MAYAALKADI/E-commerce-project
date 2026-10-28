import 'package:e_commerce_mobile_app/providers/AuthProvider.dart';
import 'package:e_commerce_mobile_app/providers/cart_provider.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../models/order_model.dart';

class OrdersScreen extends StatefulWidget {
  const OrdersScreen({Key? key}) : super(key: key);

  @override
  State<OrdersScreen> createState() => _OrdersScreenState();
}

class _OrdersScreenState extends State<OrdersScreen> {
 
  final List<Order> _dummyOrders = [
    Order(
      id: 101,
      customerName: 'أحمد محمد',
      totalPrice: 175.49,
      status: 'PENDING',
      createdAt: '2026-10-26',
    ),
    Order(
      id: 102,
      customerName: 'سارة أحمد',
      totalPrice: 120.00,
      status: 'SHIPPED',
      createdAt: '2026-10-25',
    ),
    Order(
      id: 103,
      customerName: 'أحمد محمد',
      totalPrice: 45.00,
      status: 'DELIVERED',
      createdAt: '2026-10-24',
    ),
    Order(
      id: 104,
      customerName: 'محمود علي',
      totalPrice: 210.00,
      status: 'PENDING',
      createdAt: '2026-10-23',
    ),
  ];

  @override
  Widget build(BuildContext context) {
    final authProvider = Provider.of<AuthProvider>(context);
    bool isAdmin = authProvider.userRole == 'ADMIN';

    final displayedOrders = isAdmin
        ? _dummyOrders
        : _dummyOrders
              .where((order) => order.customerName == 'أحمد محمد')
              .toList();

    return Scaffold(
      backgroundColor: const Color(0xFFF8F9FA),
      appBar: AppBar(
        title: Text(
          isAdmin
              ? 'إدارة طلبات العملاء (${displayedOrders.length})'
              : 'طلباتي السابقة (${displayedOrders.length})',
          style: const TextStyle(
            fontWeight: FontWeight.bold,
            color: Colors.white,
            fontSize: 20,
          ),
        ),
        backgroundColor: const Color(0xFF3F51B5),
        elevation: 0,
        centerTitle: true,
        iconTheme: const IconThemeData(color: Colors.white),
      ),

      floatingActionButton: !isAdmin
          ? FloatingActionButton.extended(
              backgroundColor: const Color(0xFF3F51B5),
              icon: const Icon(Icons.add_shopping_cart, color: Colors.white),
              label: const Text(
                'إنشاء طلب جديد',
                style: TextStyle(color: Colors.white),
              ),
              onPressed: () {
                _showCreateOrderDialog(context);
              },
            )
          : null,

      body: displayedOrders.isEmpty
          ? const Center(
              child: Text(
                'لا توجد طلبات لعرضها',
                style: TextStyle(color: Color(0xFF718096), fontSize: 16),
              ),
            )
          : ListView.builder(
              padding: const EdgeInsets.all(16),
              itemCount: displayedOrders.length,
              itemBuilder: (context, index) {
                final order = displayedOrders[index];
                return Container(
                  margin: const EdgeInsets.only(bottom: 16),
                  padding: const EdgeInsets.all(16),
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
                      Row(
                        mainAxisAlignment: MainAxisAlignment.spaceBetween,
                        children: [
                          Text(
                            'طلب #${order.id}',
                            style: const TextStyle(
                              fontWeight: FontWeight.bold,
                              fontSize: 16,
                              color: Color(0xFF2D3748),
                            ),
                          ),
                          // حالة الطلب مع لون مميز
                          Container(
                            padding: const EdgeInsets.symmetric(
                              horizontal: 10,
                              vertical: 4,
                            ),
                            decoration: BoxDecoration(
                              color: _getStatusColor(
                                order.status,
                              ).withOpacity(0.1),
                              borderRadius: BorderRadius.circular(8),
                            ),
                            child: Text(
                              _getStatusText(order.status),
                              style: TextStyle(
                                color: _getStatusColor(order.status),
                                fontWeight: FontWeight.bold,
                                fontSize: 12,
                              ),
                            ),
                          ),
                        ],
                      ),
                      const SizedBox(height: 8),
                      Text(
                        'العميل: ${order.customerName}',
                        style: const TextStyle(
                          color: Color(0xFF718096),
                          fontSize: 14,
                        ),
                      ),
                      const SizedBox(height: 4),
                      Text(
                        'التاريخ: ${order.createdAt}',
                        style: const TextStyle(
                          color: Color(0xFF718096),
                          fontSize: 13,
                        ),
                      ),
                      const Divider(height: 20),
                      Row(
                        mainAxisAlignment: MainAxisAlignment.spaceBetween,
                        children: [
                          Text(
                            'المجموع: \$${order.totalPrice.toStringAsFixed(2)}',
                            style: const TextStyle(
                              color: Color(0xFF3F51B5),
                              fontWeight: FontWeight.bold,
                              fontSize: 15,
                            ),
                          ),
                          
                          if (isAdmin)
                            OutlinedButton.icon(
                              onPressed: () {
                                _showChangeStatusDialog(context, order);
                              },
                              icon: const Icon(
                                Icons.edit_outlined,
                                size: 16,
                                color: Color(0xFF3F51B5),
                              ),
                              label: const Text(
                                'تغيير الحالة',
                                style: TextStyle(color: Color(0xFF3F51B5)),
                              ),
                              style: OutlinedButton.styleFrom(
                                side: const BorderSide(
                                  color: Color(0xFF3F51B5),
                                ),
                                shape: RoundedRectangleBorder(
                                  borderRadius: BorderRadius.circular(8),
                                ),
                              ),
                            ),
                        ],
                      ),
                    ],
                  ),
                );
              },
            ),
    );
  }

  Color _getStatusColor(String status) {
    switch (status) {
      case 'PENDING':
        return Colors.orange;
      case 'SHIPPED':
        return Colors.blue;
      case 'DELIVERED':
        return Colors.green;
      default:
        return Colors.grey;
    }
  }

  String _getStatusText(String status) {
    switch (status) {
      case 'PENDING':
        return 'قيد الانتظار';
      case 'SHIPPED':
        return 'قيد الشحن';
      case 'DELIVERED':
        return 'تم التوصيل';
      default:
        return status;
    }
  }

  void _showCreateOrderDialog(BuildContext context) {
    // جلب المنتجات المختارة من الـ CartProvider
    final cartProvider = Provider.of<CartProvider>(context, listen: false);

    if (cartProvider.cartItems.isEmpty) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          content: Text(
            'الرجاء اختيار منتجات أولاً بالضغط على زر (+) في صفحة المنتجات!',
          ),
          backgroundColor: Colors.orange,
        ),
      );
      return;
    }

    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('تأكيد إنشاء الطلب'),
        content: Text(
          'لديك ${cartProvider.cartItems.length} منتجات في السلة.\nالمجموع الكلي: \$${cartProvider.totalPrice.toStringAsFixed(2)}\n\nهل تريد تأكيد إرسال الطلب؟',
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('إلغاء', style: TextStyle(color: Colors.grey)),
          ),
          ElevatedButton(
            style: ElevatedButton.styleFrom(
              backgroundColor: const Color(0xFF3F51B5),
            ),
            onPressed: () {
              
              cartProvider.clearCart();

              Navigator.pop(context);
              ScaffoldMessenger.of(context).showSnackBar(
                const SnackBar(
                  content: Text('تم إنشاء الطلب بنجاح!'),
                  backgroundColor: Colors.green,
                ),
              );
            },
            child: const Text(
              'تأكيد الطلب',
              style: TextStyle(color: Colors.white),
            ),
          ),
        ],
      ),
    );
  }

  void _showChangeStatusDialog(BuildContext context, Order order) {
    String selectedStatus = order.status;
    showDialog(
      context: context,
      builder: (context) => StatefulBuilder(
        builder: (context, setDialogState) => AlertDialog(
          title: Text('تغيير حالة الطلب #${order.id}'),
          content: Column(
            mainAxisSize: MainAxisSize.min,
            children: ['PENDING', 'SHIPPED', 'DELIVERED'].map((status) {
              return RadioListTile<String>(
                title: Text(_getStatusText(status)),
                value: status,
                groupValue: selectedStatus,
                activeColor: const Color(0xFF3F51B5),
                onChanged: (val) {
                  setDialogState(() {
                    selectedStatus = val!;
                  });
                },
              );
            }).toList(),
          ),
          actions: [
            TextButton(
              onPressed: () => Navigator.pop(context),
              child: const Text('_إلغاء', style: TextStyle(color: Colors.grey)),
            ),
            ElevatedButton(
              style: ElevatedButton.styleFrom(
                backgroundColor: const Color(0xFF3F51B5),
              ),
              onPressed: () {
                setState(() {
                  order.status = selectedStatus;
                });
                Navigator.pop(context);
                ScaffoldMessenger.of(context).showSnackBar(
                  const SnackBar(
                    content: Text('تم تحديث حالة الطلب بنجاح!'),
                    backgroundColor: Colors.green,
                  ),
                );
              },
              child: const Text(
                'حفظ التعديل',
                style: TextStyle(color: Colors.white),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
