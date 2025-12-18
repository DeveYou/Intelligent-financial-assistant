
import 'package:dio/dio.dart';

/// Wrapper class for API responses.
class ApiResponse {
  /// The raw [Response] object from the HTTP client.
  final Response? response;

  /// Any error object associated with the request (e.g., exception or error message).
  final dynamic error;

  /// Creates an [ApiResponse] with both [response] and [error] (one is usually null).
  ApiResponse(this.response, this.error);

  /// Creates an [ApiResponse] representing an error state with [errorMessage].
  ApiResponse.withError(dynamic errorMessage) : response = null, error = errorMessage;

  /// Creates an [ApiResponse] representing a success state with [responseData].
  ApiResponse.withSuccess(Response responseData) : response = responseData, error = null;
}