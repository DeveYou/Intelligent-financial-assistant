/// Represents summary data for the home screen.
///
/// Contains essential account information displayed on the home screen.
class HomeModel {
  /// Name of the account holder.
  final String accountHolder;
  
  /// The account number (typically IBAN or masked number).
  final String accountNumber;
  
  /// Name of the bank.
  final String bankName;
  
  /// Current account balance.
  final double balance;
  
  /// Currency code (e.g., USD, MAD, EUR).
  final String currency;

  /// Creates a new [HomeModel] instance.
  HomeModel({
    required this.accountHolder,
    required this.accountNumber,
    required this.bankName,
    required this.balance,
    required this.currency,
  });

  /// Creates a [HomeModel] from JSON data with fallback defaults.
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