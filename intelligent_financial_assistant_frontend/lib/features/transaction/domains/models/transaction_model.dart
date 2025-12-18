import 'package:intelligent_financial_assistant_frontend/features/recipient/domains/models/recipient_model.dart';

/// Represents a financial transaction in the application.
///
/// A transaction can be a transfer, deposit, or withdrawal operation.
/// It contains all relevant information including amount, recipient, reason, and status.
class TransactionModel {
  /// Unique identifier for the transaction.
  int? id;
  
  /// Type of transaction (TRANSFER, DEPOSIT, WITHDRAWAL).
  String? type;
  
  /// The transaction amount.
  double? amount;
  
  /// Unique reference code for the transaction.
  String? reference;
  
  /// Reason or description for the transaction.
  String? reason;
  
  /// Transaction fee/charge amount.
  double? chargeAmount;
  
  /// The recipient/beneficiary of the transaction.
  RecipientModel? beneficiary;

  /// ID of the user who initiated the transaction.
  int? userId;
  
  /// ID of the bank account used for the transaction.
  int? bankAccountId;
  
  /// ID of the recipient.
  int? recipientId;
  
  /// IBAN of the recipient.
  String? recipientIban;
  
  /// Current status of the transaction (PENDING, EXECUTED, REJECTED).
  String? status;
  
  /// Timestamp when the transaction was created.
  DateTime? createdAt;

  /// Creates a new [TransactionModel] instance.
  TransactionModel({
    this.id,
    this.type,
    this.amount,
    this.reference,
    this.reason,
    this.chargeAmount,
    this.beneficiary,
    this.userId,
    this.bankAccountId,
    this.recipientId,
    this.recipientIban,
    this.status,
    this.createdAt,
  });

  /// Creates a [TransactionModel] from JSON data.
  ///
  /// Parses the JSON response from the API and handles multiple formats
  /// for recipient data and date fields.
  TransactionModel.fromJson(Map<String, dynamic> json) {
      id = json['id'];
      type = json['type'];
      amount = json['amount'] != null ? (json['amount'] as num).toDouble() : 0.0;
      reference = json['reference'];
      reason = json['reason'];
      status = json['status'];
      userId = json['userId'];
      bankAccountId = json['bankAccountId'];
      recipientId = json['recipientId'];
      recipientIban = json['recipientIban'];
      if (json['date'] != null) {
          createdAt = DateTime.tryParse(json['date'].toString());
      } else if (json['createdAt'] != null) {
          createdAt = DateTime.tryParse(json['createdAt'].toString());
      }

      if (json['recipientName'] != null) {
         beneficiary = RecipientModel(
           fullName: json['recipientName'],
           iban: json['recipientIban'],
         );
      } else if (json['recipientIban'] != null) {
          beneficiary = RecipientModel(
           iban: json['recipientIban'],
         );
      } else if (json['recipient'] != null && json['recipient'] is Map) {
         beneficiary = RecipientModel.fromJson(json['recipient']);
      } else if (json['beneficiary'] != null && json['beneficiary'] is Map) {
         beneficiary = RecipientModel.fromJson(json['beneficiary']);
      }
  }

  /// Converts this [TransactionModel] to a JSON map for API requests.
  ///
  /// Returns a map containing only the fields required for creating/updating transactions.
  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = <String, dynamic>{};
    data['bankAccountId'] = bankAccountId;
    data['amount'] = amount;
    data['type'] = type?.toUpperCase();
    data['reason'] = reason;
    
    if (beneficiary != null) {
      if (beneficiary!.id != null) {
         data['recipientId'] = beneficiary!.id;
      }
      if (beneficiary!.iban != null) {
         data['recipientIban'] = beneficiary!.iban;
      }
    } else {
       data['recipientId'] = recipientId;
       data['recipientIban'] = recipientIban;
    }
    
    return data;
  }
}