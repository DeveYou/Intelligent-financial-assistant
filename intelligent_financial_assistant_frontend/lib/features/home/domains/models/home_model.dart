class HomeModel {
  final String accountHolder;
  final String accountNumber;
  final String bankName;
  final double balance;
  final String currency;

  HomeModel({
    required this.accountHolder,
    required this.accountNumber,
    required this.bankName,
    required this.balance,
    required this.currency,
  });

  factory HomeModel.fromJson(Map<String, dynamic> json) {
    return HomeModel(
      accountHolder: json['account_holder'] ?? 'Unknown User',
      accountNumber: json['account_number'] ?? '**** **** **** 0000',
      bankName: json['bank_name'] ?? 'VoxBank',
      balance: (json['balance'] is String)
          ? double.tryParse(json['balance']) ?? 0.0
          : (json['balance'] as num?)?.toDouble() ?? 0.0,
      currency: json['currency'] ?? 'USD',
    );
  }
}