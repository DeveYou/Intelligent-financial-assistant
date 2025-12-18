/// Represents user login credentials.
///
/// Used for authenticating users via email and password.
class LoginModel {
  /// User's email address.
  String? email;
  
  /// User's password.
  String? password;

  /// Creates a new [LoginModel] instance.
  LoginModel({this.email, this.password});

  /// Creates a [LoginModel] from JSON data.
  LoginModel.fromJson(Map<String, dynamic> json) {
    email = json['email'];
    password = json['password'];
  }

  /// Converts this [LoginModel] to a JSON map for API requests.
  Map<String, dynamic> toJson() {
    final map = <String, dynamic>{};
    map['email'] = email;
    map['password'] = password;
    return map;
  }
}