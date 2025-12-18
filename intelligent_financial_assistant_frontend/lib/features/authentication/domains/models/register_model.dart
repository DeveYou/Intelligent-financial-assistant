/// Represents user registration data.
///
/// Used when creating a new user account in the system.
class RegisterModel {
  /// User's first name.
  String? firstName;
  
  /// User's last name.
  String? lastName;
  
  /// User's email address.
  String? email;
  
  /// User's password.
  String? password;

  /// Creates a new [RegisterModel] instance.
  RegisterModel({
    this.firstName,
    this.lastName,
    this.email,
    this.password,
  });

  /// Creates a [RegisterModel] from JSON data.
  RegisterModel.fromJson(Map<String, dynamic> json) {
    firstName = json['firstName'];
    lastName = json['lastName'];
    email = json['email'];
    password = json['password'];
  }

  /// Converts this [RegisterModel] to a JSON map for API requests.
  Map<String, dynamic> toJson() {
    return {
      'firstName': firstName,
      'lastName': lastName,
      'email': email,
      'password': password,
    };
  }
}