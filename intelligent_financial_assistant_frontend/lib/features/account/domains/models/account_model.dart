
class AccountModel {
  String? accountHolder;
  String? type;
  String? iban;
  double? balance;
  String? expirationDate;
  bool? isActive;

  bool? isPaymentByCard;
  bool? isWithdrawal;
  bool? isOnlinePayment;
  bool? isContactless;

  AccountModel({
    required this.accountHolder,
    required this.type,
    required this.iban,
    required this.balance,
    required this.expirationDate,
    required this.isActive,
    this.isPaymentByCard,
    this.isWithdrawal,
    this.isOnlinePayment,
    this.isContactless
  });

  AccountModel.fromJson(Map<String, dynamic> json, {this.accountHolder, this.type, this.iban, this.balance, this.expirationDate, this.isActive, this.isPaymentByCard, this.isWithdrawal, this.isOnlinePayment, this.isContactless}) {
    accountHolder = json['account_holder'];
    type = json['type'];
    iban = json['iban'];
    balance = json['balance'] != null ? double.tryParse(json['balance'].toString()) : null;
    expirationDate = json['expiration_date'];
    isActive = json['is_active'];
    isPaymentByCard = json['is_payment_by_card'];
    isWithdrawal = json['is_withdrawal'];
    isOnlinePayment = json['is_online_payment'];
    isContactless = json['is_contactless'];
  }


  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = <String, dynamic>{};
    data['account_holder'] = accountHolder;
    data['type'] = type;
    data['iban'] = iban;
    data['balance'] = balance;
    data['expiration_date'] = expirationDate;
    data['is_active'] = isActive;
    data['is_payment_by_card'] = isPaymentByCard;
    data['is_withdrawal'] = isWithdrawal;
    data['is_online_payment'] = isOnlinePayment;
    data['is_contactless'] = isContactless;
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
      accountHolder: accountHolder ?? '',
      iban: iban ?? '',
      balance: balance ?? 0.0,
      isActive: isActive ?? this.isActive,
      isPaymentByCard: isPaymentByCard ?? this.isPaymentByCard,
      isWithdrawal: isWithdrawal ?? this.isWithdrawal,
      isOnlinePayment: isOnlinePayment ?? this.isOnlinePayment,
      isContactless: isContactless ?? this.isContactless,
      type: type ?? '',
      expirationDate: expirationDate ?? '',
    );
  }

}