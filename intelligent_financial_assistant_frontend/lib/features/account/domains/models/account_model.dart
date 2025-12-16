
class AccountModel {
  int? id;
  int? userId;
  String? type;
  String? iban;
  double? balance;
  String? expirationDate;
  bool? isActive; 
  bool? isPaymentByCard;
  bool? isWithdrawal;
  bool? isOnlinePayment;
  bool? isContactless;
  DateTime? createdAt;
  String? accountHolder;
  String? currency;

  String get accountNumber => iban ?? ''; 

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
      createdAt = DateTime.tryParse(json['createdAt']);
    }
    currency = json['currency'] ?? 'MAD';
  }

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
