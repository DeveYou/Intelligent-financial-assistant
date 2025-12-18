/// Represents a bank account in the financial assistant application.
///
/// An account contains information about the user's bank account including
/// balance, IBAN, account type, and various card permissions.
class AccountModel {
  /// Unique identifier for the account.
  int? id;
  
  /// ID of the user who owns this account.
  int? userId;
  
  /// Type of account (e.g., CHECKING, SAVINGS).
  String? type;
  
  /// International Bank Account Number.
  String? iban;
  
  /// Current account balance.
  double? balance;
  
  /// Card expiration date.
  String? expirationDate;
  
  /// Whether the account is currently active.
  bool? isActive;
  
  /// Permission for payment by card.
  bool? isPaymentByCard;
  
  /// Permission for ATM withdrawals.
  bool? isWithdrawal;
  
  /// Permission for online payments.
  bool? isOnlinePayment;
  
  /// Permission for contactless payments.
  bool? isContactless;
  
  /// When the account was created.
  DateTime? createdAt;
  
  /// Name of the account holder.
  String? accountHolder;
  
  /// Account currency (default: MAD).
  String? currency;

  /// Gets the account number (IBAN).
  String get accountNumber => iban ?? '';

  /// Creates a new [AccountModel] instance.
  AccountModel({
    this.id,
    this.userId,
    this.accountHolder,
    this.type,
    this.iban,
    this.balance,
    this.expirationDate,
    this.isActive,
    this.isPaymentByCard,
    this.isWithdrawal,
    this.isOnlinePayment,
    this.isContactless,
    this.createdAt,
    this.currency,
  });

  /// Creates an [AccountModel] from JSON data.
  AccountModel.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    userId = json['userId'];
    accountHolder = json['userId'].toString();
    type = json['type'];
    iban = json['iban'];
    balance = json['balance'] != null ? double.tryParse(json['balance'].toString()) : null;
    expirationDate = json['expirationDate']?.toString();
    isActive = json['isActive'] ?? false;
    isPaymentByCard = json['isPaymentByCard'] ?? false;
    isWithdrawal = json['isWithdrawal'] ?? false;
    isOnlinePayment = json['isOnlinePayment'] ?? false;
    isContactless = json['isContactless'] ?? false;
    if(json['createdAt'] != null){
      createdAt = DateTime.tryParse(json['createdAt'].toString());
    }
    currency = json['currency'] ?? 'MAD';
  }

  /// Converts this [AccountModel] to a JSON map.
  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = <String, dynamic>{};
    data['id'] = id;
    data['userId'] = userId;
    data['type'] = type;
    data['iban'] = iban;
    data['balance'] = balance;
    data['expirationDate'] = expirationDate;
    data['isActive'] = isActive;
    data['isPaymentByCard'] = isPaymentByCard;
    data['isWithdrawal'] = isWithdrawal;
    data['isOnlinePayment'] = isOnlinePayment;
    data['isContactless'] = isContactless;
    data['createdAt'] = createdAt?.toIso8601String();
    data['currency'] = currency;
    return data;
  }

  /// Creates a copy of this [AccountModel] with specified fields updated.
  ///
  /// Useful for updating account permissions while preserving other properties.
  AccountModel copyWith({
    bool? isActive,
    bool? isPaymentByCard,
    bool? isWithdrawal,
    bool? isOnlinePayment,
    bool? isContactless,
  }) {
    return AccountModel(
      id: id,
      userId: userId,
      accountHolder: accountHolder,
      iban: iban ?? '',
      balance: balance ?? 0.0,
      isActive: isActive ?? this.isActive,
      isPaymentByCard: isPaymentByCard ?? this.isPaymentByCard,
      isWithdrawal: isWithdrawal ?? this.isWithdrawal,
      isOnlinePayment: isOnlinePayment ?? this.isOnlinePayment,
      isContactless: isContactless ?? this.isContactless,
      type: type ?? '',
      expirationDate: expirationDate ?? '',
      createdAt: createdAt,
      currency: currency ?? this.currency,
    );
  }
}
