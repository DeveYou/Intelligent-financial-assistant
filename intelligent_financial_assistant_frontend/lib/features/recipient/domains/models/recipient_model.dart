class RecipientModel {
  int? id;
  String? bank;
  String? iban;
  String? fullName;
  DateTime? createdAt;

  RecipientModel({this.bank, this.iban, this.fullName, this.createdAt});

  RecipientModel.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    bank = json['bank'];
    iban = json['iban'];
    fullName = json['full_name'];
    createdAt = json['created_at'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = <String, dynamic>{};
    data['id'] = id;
    data['bank'] = bank;
    data['iban'] = iban;
    data['full_name'] = fullName;
    data['created_at'] = createdAt;
    return data;
  }
}