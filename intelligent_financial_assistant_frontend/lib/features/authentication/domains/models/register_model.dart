class RegisterModel {
  String? firstName;
  String? lastName;
  String? email;
  String? password;

  RegisterModel({
    this.firstName,
    this.lastName,
    this.email,
    this.password,
  });

  RegisterModel.fromJson(Map<String, dynamic> json) {
    firstName = json['firstName'];
    lastName = json['lastName'];
    email = json['email'];
    password = json['password'];
  }

  Map<String, dynamic> toJson() {
    return {
      'firstName': firstName,
      'lastName': lastName,
      'email': email,
      'password': password,
    };
  }
}