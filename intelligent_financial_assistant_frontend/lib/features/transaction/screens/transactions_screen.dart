import 'package:flutter/material.dart';
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
          style: TextStyle(
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
            return const Center(child: CircularProgressIndicator());
          } else if (transactionController.transactions.isEmpty) {
            return Center(
              child: Text(
                getTranslated('no_transactions_found', context) ?? 'No transactions found',
                style: const TextStyle(fontSize: 16),
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
