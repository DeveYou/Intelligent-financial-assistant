/// Model representing a standardized error response from the API.
class ErrorResponse {
  List<Errors>? _errors;

  /// List of specific error details.
  List<Errors>? get errors => _errors;

  /// Creates an [ErrorResponse].
  ErrorResponse({
      List<Errors>? errors}){
    _errors = errors;
}

  /// Creates an [ErrorResponse] from a JSON [json] object or map.
  ErrorResponse.fromJson(dynamic json) {
    if (json["errors"] != null) {
      _errors = [];
      json["errors"].forEach((v) {
        _errors!.add(Errors.fromJson(v));
      });
    }
  }

  /// Converts this [ErrorResponse] to a JSON map.
  Map<String, dynamic> toJson() {
    var map = <String, dynamic>{};
    if (_errors != null) {
      map["errors"] = _errors!.map((v) => v.toJson()).toList();
    }
    return map;
  }
}


/// Model representing a specific error detail (code and message).
class Errors {
  String? _code;
  String? _message;

  /// The error code.
  String? get code => _code;

  /// The error message.
  String? get message => _message;

  /// Creates an [Errors] instance.
  Errors({
      String? code,
      String? message}){
    _code = code;
    _message = message;
}

  /// Creates an [Errors] instance from a JSON [json] object.
  Errors.fromJson(dynamic json) {
    _code = json["code"];
    _message = json["message"];
  }

  /// Converts this [Errors] instance to a JSON map.
  Map<String, dynamic> toJson() {
    var map = <String, dynamic>{};
    map["code"] = _code;
    map["message"] = _message;
    return map;
  }

}