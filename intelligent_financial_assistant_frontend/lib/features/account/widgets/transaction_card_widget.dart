import 'package:flutter/material.dart';
import 'package:intelligent_financial_assistant_frontend/features/transaction/domains/models/transaction_model.dart';
import 'package:intl/intl.dart';

class TransactionCardWidget extends StatelessWidget {
  final TransactionModel transaction;

  const TransactionCardWidget({super.key, required this.transaction});

  @override
  Widget build(BuildContext context) {
    // Basic formatting
    final isCredit = transaction.type == 'credit' || transaction.type == 'deposit';
    final color = isCredit ? Colors.green : Colors.red;
    final formattedDate = transaction.createdAt != null ? DateFormat('MMM dd, yyyy').format(transaction.createdAt!) : '';

    return Card(
      elevation: 0,
      color: Colors.grey[50],
      margin: const EdgeInsets.symmetric(vertical: 6),
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
      child: ListTile(
        leading: CircleAvatar(
          backgroundColor: color.withOpacity(0.1),
          child: Icon(
            isCredit ? Icons.arrow_downward : Icons.arrow_upward,
            color: color,
            size: 20,
          ),
        ),
        title: Text(
          transaction.reason ?? transaction.type!.toUpperCase(),
          style: const TextStyle(fontWeight: FontWeight.w600, fontSize: 14),
        ),
        subtitle: Text(formattedDate, style: TextStyle(fontSize: 12, color: Colors.grey[600])),
        trailing: Text(
          "${isCredit ? '+' : '-'} ${transaction.amount}",
          style: TextStyle(
            color: color,
            fontWeight: FontWeight.bold,
            fontSize: 16,
          ),
        ),
      ),
    );
  }
}