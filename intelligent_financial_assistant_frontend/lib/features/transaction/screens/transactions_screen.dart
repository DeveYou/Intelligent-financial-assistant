import 'package:flutter/material.dart';
import 'package:intelligent_financial_assistant_frontend/common/basewidgets/shimmer_skeleton.dart';
import 'package:intelligent_financial_assistant_frontend/features/notifications/screens/notifications_screen.dart';
import 'package:intelligent_financial_assistant_frontend/features/settings/screens/settings_screen.dart';
import 'package:intelligent_financial_assistant_frontend/features/transaction/controllers/transaction_controller.dart';
import 'package:intelligent_financial_assistant_frontend/features/transaction/domains/models/transaction_model.dart';
import 'package:intelligent_financial_assistant_frontend/features/transaction/widgets/transaction_details_widget.dart';
import 'package:intelligent_financial_assistant_frontend/features/transaction/widgets/transaction_item_widget.dart';
import 'package:intelligent_financial_assistant_frontend/localization/language_constraints.dart';
import 'package:provider/provider.dart';

class TransactionsScreen extends StatefulWidget {
  const TransactionsScreen({super.key});

  @override
  State<TransactionsScreen> createState() => _TransactionsScreenState();
}

class _TransactionsScreenState extends State<TransactionsScreen> {
  TransactionModel? _selectedTransaction;

  @override
  void initState() {
    super.initState();
    // Load transactions when screen initializes
    WidgetsBinding.instance.addPostFrameCallback((_) {
      Provider.of<TransactionController>(context, listen: false).getTransactions();
    });
  }

  void _showTransactionDetails(TransactionModel transaction) {
    setState(() {
      _selectedTransaction = transaction;
    });

    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      shape: const RoundedRectangleBorder(
        borderRadius: BorderRadius.vertical(top: Radius.circular(20)),
      ),
      builder: (context) {
        return DraggableScrollableSheet(
          initialChildSize: 0.7,
          maxChildSize: 0.9,
          minChildSize: 0.5,
          expand: false,
          builder: (context, scrollController) {
            return SingleChildScrollView(
              controller: scrollController,
              child: TransactionDetailsWidget(transaction: transaction),
            );
          },
        );
      },
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(
          getTranslated('transactions', context)!,
          style: const TextStyle(
            color: Colors.white
          )
        ),
        backgroundColor: Theme.of(context).primaryColor,
        elevation: 0,
        actions: [
          IconButton(
            icon: const Icon(Icons.notifications_none),
            onPressed: () {
              Navigator.of(context).push(
                  MaterialPageRoute(
                      builder: (context) => const NotificationsScreen()
                  )
              );
            },
          ),
          IconButton(
            icon: const Icon(Icons.settings),
            onPressed: () {
              Navigator.of(context).push(
                  MaterialPageRoute(
                      builder: (context) => const SettingsScreen()
                  )
              );
            },
          ),
        ],
      ),
      body: Consumer<TransactionController>(
        builder: (context, transactionController, child) {
          if (transactionController.isLoading) {
            return ListView.builder(
              itemCount: 8,
              padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
              itemBuilder: (context, index) {
                return Padding(
                  padding: const EdgeInsets.only(bottom: 12),
                  child: Row(
                    children: [
                      const ShimmerSkeleton.circular(height: 50, width: 50),
                      const SizedBox(width: 16),
                      Expanded(
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            const ShimmerSkeleton.rectangular(height: 16, width: 120),
                            const SizedBox(height: 8),
                            const ShimmerSkeleton.rectangular(height: 12, width: 80),
                          ],
                        ),
                      ),
                      const SizedBox(width: 16),
                      const ShimmerSkeleton.rectangular(height: 16, width: 60),
                    ],
                  ),
                );
              },
            );
          } else if (transactionController.transactions.isEmpty) {
            return Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Container(
                    padding: const EdgeInsets.all(24),
                    decoration: BoxDecoration(
                      color: Colors.grey[100],
                      shape: BoxShape.circle,
                    ),
                    child: Icon(Icons.receipt_long_outlined, size: 64, color: Colors.grey[400]),
                  ),
                  const SizedBox(height: 24),
                  Text(
                    getTranslated('no_transactions_found', context) ?? 'No transactions found',
                    style: const TextStyle(fontSize: 18, fontWeight: FontWeight.bold, color: Colors.black87),
                  ),
                  const SizedBox(height: 8),
                  Text(
                    getTranslated('no_transactions_subtitle', context) ?? 'Your recent transactions will appear here',
                    style: TextStyle(fontSize: 14, color: Colors.grey[600]),
                    textAlign: TextAlign.center,
                  ),
                ],
              ),
            );
          }

          return ListView.builder(
            itemCount: transactionController.transactions.length,
            itemBuilder: (context, index) {
              final transaction = transactionController.transactions[index];
              return TransactionItemWidget(
                transaction: transaction,
                onTap: () => _showTransactionDetails(transaction),
              );
            },
          );
        },
      ),
    );
  }
}
