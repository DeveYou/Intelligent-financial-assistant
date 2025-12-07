class ProfileModel {
  String? firstName;
  String? lastName;
  String? identityCode;
  String? email;
  String? phoneNumber;
  String? address;
  DateTime? dateOfBirth;
  DateTime? createdAt;

  ProfileModel({
    this.firstName,
    this.lastName,
    this.identityCode,
    this.email,
    this.phoneNumber,
    this.address,
    this.dateOfBirth,
    this.createdAt,
  });

  ProfileModel.fromJson(Map<String, dynamic> json) {
    firstName = json['first_name'];
    lastName = json['last_name'];
    identityCode = json['identity_code'];
    email = json['email'];
    phoneNumber = json['phone_number'];
    address = json['address'];
    dateOfBirth = json['date_of_birth'] != null ? DateTime.parse(json['date_of_birth']) : null;
    createdAt = json['created_at'] != null ? DateTime.parse(json['created_at']) : null;
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = <String, dynamic>{};
    data['first_name'] = firstName;
    data['last_name'] = lastName;
    data['identity_code'] = identityCode;
    data['email'] = email;
    data['phone_number'] = phoneNumber;
    data['address'] = address;
    data['date_of_birth'] = dateOfBirth?.toIso8601String();
    data['created_at'] = createdAt?.toIso8601String();
    return data;
  }

}