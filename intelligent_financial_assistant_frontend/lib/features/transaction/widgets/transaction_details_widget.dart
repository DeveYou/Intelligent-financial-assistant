import 'package:flutter/material.dart';
import 'package:intelligent_financial_assistant_frontend/features/transaction/domains/models/transaction_model.dart';
import 'package:intelligent_financial_assistant_frontend/localization/language_constraints.dart';
import 'package:intl/intl.dart';

class TransactionDetailsWidget extends StatelessWidget {
  final TransactionModel transaction;

  const TransactionDetailsWidget({
    super.key,
    required this.transaction,
  });

  @override
  Widget build(BuildContext context) {
    final bool isCredit = transaction.type?.toLowerCase() == 'credit';
    final dateFormat = DateFormat('MMMM dd, yyyy');
    final timeFormat = DateFormat('hh:mm a');

    return Container(
      padding: const EdgeInsets.all(16),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // Header with amount
          Center(
            child: Column(
              children: [
                Container(
                  width: 60,
                  height: 60,
                  decoration: BoxDecoration(
                    color: isCredit ? Colors.green.shade100 : Colors.red.shade100,
                    borderRadius: BorderRadius.circular(30),
                  ),
                  child: Icon(
                    isCredit ? Icons.arrow_downward : Icons.arrow_upward,
                    color: isCredit ? Colors.green : Colors.red,
                    size: 30,
                  ),
                ),
                const SizedBox(height: 16),
                Text(
                  '${isCredit ? '+' : '-'} \$${transaction.amount?.toStringAsFixed(2) ?? '0.00'}',
                  style: TextStyle(
                    fontWeight: FontWeight.bold,
                    color: isCredit ? Colors.green : Colors.red,
                    fontSize: 24,
                  ),
                ),
                const SizedBox(height: 8),
                Text(
                  transaction.createdAt != null
                      ? '${dateFormat.format(transaction.createdAt!)} at ${timeFormat.format(transaction.createdAt!)}'
                      : getTranslated('unknown_date', context)!,
                  style: TextStyle(
                    color: Colors.grey.shade600,
                    fontSize: 14,
                  ),
                ),
              ],
            ),
          ),

          const SizedBox(height: 32),

          // Transaction details
          Text(
            getTranslated('transaction_details', context)!,
            style: TextStyle(
              fontWeight: FontWeight.bold,
              fontSize: 18,
            ),
          ),
          const SizedBox(height: 16),

          _buildDetailRow(getTranslated('type', context)!, transaction.type ?? getTranslated('unknown', context)!),
          _buildDetailRow(getTranslated('reference', context)!, transaction.reference ?? 'N/A'),
          _buildDetailRow(getTranslated('reason', context)!, transaction.reason ?? getTranslated('no_reason_provided', context)!),
          _buildDetailRow(getTranslated('charge_amount', context)!, '\$${transaction.chargeAmount?.toStringAsFixed(2) ?? '0.00'}'),

          const SizedBox(height: 32),

          // Beneficiary details if available
          if (transaction.beneficiary != null) ...[
            Text(
              getTranslated('beneficiary_details', context)!,
              style: TextStyle(
                fontWeight: FontWeight.bold,
                fontSize: 18,
              ),
            ),
            const SizedBox(height: 16),

            _buildDetailRow(getTranslated('name', context)!, transaction.beneficiary?.fullName ?? getTranslated('unknown', context)!),
            _buildDetailRow(getTranslated('iban', context)!, transaction.beneficiary?.iban ?? 'N/A'),
            _buildDetailRow(getTranslated('bank', context)!, transaction.beneficiary?.bank ?? 'N/A'),
          ],
        ],
      ),
    );
  }

  Widget _buildDetailRow(String label, String value) {
    return Padding(
      padding: const EdgeInsets.only(bottom: 12),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          SizedBox(
            width: 120,
            child: Text(
              label,
              style: TextStyle(
                color: Colors.grey.shade600,
                fontSize: 14,
              ),
            ),
          ),
          Expanded(
            child: Text(
              value,
              style: const TextStyle(
                fontWeight: FontWeight.w500,
                fontSize: 14,
              ),
            ),
          ),
        ],
      ),
    );
  }
}
