import 'package:intelligent_financial_assistant_frontend/features/recipient/domains/models/recipient_model.dart';

class TransactionModel {
  String? type;
  double? amount;
  String? reference;
  String? reason;
  double? chargeAmount;
  RecipientModel? beneficiary;
  DateTime? createdAt;

  TransactionModel({
    this.type,
    this.amount,
    this.reference,
    this.reason,
    this.chargeAmount,
    this.beneficiary,
    this.createdAt,
  });


  TransactionModel.fromJson(Map<String, dynamic> json) {
      type = json['type'];
      amount = (json['amount'] as num).toDouble();
      reference = json['reference'];
      reason = json['reason'];
      chargeAmount = (json['chargeAmount'] as num).toDouble();
      beneficiary = json['beneficiary'] != null
          ? RecipientModel.fromJson(json['beneficiary'])
          : null;
      createdAt = DateTime.parse(json['createdAt']);
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = <String, dynamic>{};
    data['type'] = type;
    data['amount'] = amount;
    data['reference'] = reference;
    data['reason'] = reason;
    data['chargeAmount'] = chargeAmount;
    if (beneficiary != null) {
      data['beneficiary'] = beneficiary!.toJson();
    }
    data['createdAt'] = createdAt?.toIso8601String();
    return data;
  }


}