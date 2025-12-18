/// Represents a payment recipient in the financial assistant application.
///
/// A recipient contains bank account information (IBAN and bank name)
/// along with the full name of the account holder.
class RecipientModel {
  /// Unique identifier for the recipient.
  int? id;
  
  /// Name of the recipient's bank.
  String? bank;
  
  /// The recipient's International Bank Account Number.
  String? iban;
  
  /// Full name of the recipient.
  String? fullName;
  
  /// Timestamp when this recipient was created.
  DateTime? createdAt;

  /// Creates a new [RecipientModel] instance.
  RecipientModel({this.id, this.bank, this.iban, this.fullName, this.createdAt});

  /// Creates a [RecipientModel] from JSON data.
  ///
  /// Parses the JSON map and creates a recipient object with the provided data.
  RecipientModel.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    bank = json['bank'];
    iban = json['iban'];
    fullName = json['full_name'];
    createdAt = json['created_at'] != null ? DateTime.tryParse(json['created_at']) : null;
  }

  /// Converts this [RecipientModel] to a JSON map.
  ///
  /// Returns a map representation suitable for API requests.
  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = <String, dynamic>{};
    data['id'] = id;
    data['bank'] = bank;
    data['iban'] = iban;
    data['fullName'] = fullName;
    data['createdAt'] = createdAt?.toIso8601String();
    return data;
  }
}