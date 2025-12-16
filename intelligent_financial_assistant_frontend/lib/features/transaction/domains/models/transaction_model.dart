import 'package:intelligent_financial_assistant_frontend/features/recipient/domains/models/recipient_model.dart';

class TransactionModel {
  int? id;
  String? type;
  double? amount;
  String? reference;
  String? reason;
  double? chargeAmount;
  RecipientModel? beneficiary;

  int? userId;
  int? bankAccountId;
  int? recipientId;
  String? recipientIban;
  String? status; // TransactionStatus: PENDING, EXECUTED, REJECTED
  DateTime? createdAt;

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