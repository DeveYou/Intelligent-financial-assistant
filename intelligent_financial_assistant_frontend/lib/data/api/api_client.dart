import 'dart:convert';
import 'dart:developer';

import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:http/http.dart' as http;
import 'package:intelligent_financial_assistant_frontend/data/response/error_response.dart';
import 'package:intelligent_financial_assistant_frontend/utils/app_constants.dart';
import 'package:shared_preferences/shared_preferences.dart';

/// A service class responsible for making HTTP requests to the backend API.
///
/// This class handles GET, POST, PUT, and DELETE requests, manages headers
/// (including authentication tokens and localization), and processes responses.
class ApiClient extends GetxService {
  /// The base URL of the API.
  final String appBaseUrl;

  /// Shared preferences instance for retrieving cached data like tokens and language settings.
  final SharedPreferences sharedPreferences;

  /// Error message displayed when there is no internet connection.
  static const String noInternetMessage = "Connection to API server failed due to internet connection";

  /// The timeout duration for API requests in seconds.
  final int timeoutInSeconds = 30;

  /// The current authentication token (JWT).
  String? token;
  late Map<String, String>? _mainHeaders;

  /// Creates an instance of [ApiClient].
  ///
  /// [appBaseUrl] is the root URL for the API.
  /// [sharedPreferences] is used to access local storage.
  ApiClient({required this.appBaseUrl, required this.sharedPreferences}) {
    token = sharedPreferences.getString(AppConstants.token);
    debugPrint('Token: $token');
    updateHeader(token, sharedPreferences.getString(AppConstants.languageCode));
  }

  /// Updates the HTTP headers for subsequent requests.
  ///
  /// [token] is the new authentication token.
  /// [languageCode] is the language code for localization (e.g., 'en', 'fr').
  void updateHeader(String? token, String? languageCode) {
    _mainHeaders = {
      'Content-Type' : 'application/json; charset=UTF-8',
      AppConstants.localizationKey : languageCode ?? AppConstants.languages[0].languageCode ?? 'en',
      'Authorization' : 'Bearer $token'
    };
  }


  /// Performs a GET request to the specified [uri].
  ///
  /// [query] is an optional map of query parameters.
  /// [headers] is an optional map of custom headers.
  ///
  /// Returns a [Response] object containing the server's response or an error.
  Future<Response> getData(String uri, {Map<String, dynamic>? query, Map<String, String>? headers}) async {
    try {
      debugPrint('====> API Call: $uri\nHeader: $_mainHeaders');
      http.Response _response = await http.get(
        Uri.parse(appBaseUrl + uri),
        headers: headers ?? _mainHeaders,
      ).timeout(Duration(seconds: timeoutInSeconds));
      return handleResponse(_response, uri);
    } catch (e) {
      return const Response(statusCode: 1, statusText: noInternetMessage);
    }
  }

  /// Performs a POST request to the specified [uri].
  ///
  /// [body] is the payload to send in the request body.
  /// [headers] is an optional map of custom headers.
  ///
  /// Returns a [Response] object containing the server's response or an error.
  Future<Response> postData(String uri, dynamic body, {Map<String, String>? headers}) async {
    try {
      debugPrint('====> API Call: $uri\nHeader: $_mainHeaders');
      debugPrint('====> API Body: $body');
      http.Response _response = await http.post(
        Uri.parse(appBaseUrl + uri),
        body: jsonEncode(body),
        headers: headers ?? _mainHeaders,
      ).timeout(Duration(seconds: timeoutInSeconds));
      return handleResponse(_response, uri);
    } catch (e) {
      return const Response(statusCode: 1, statusText: noInternetMessage);
    }
  }

  /// Performs a PUT request to the specified [uri].
  ///
  /// [body] is the payload to send in the request body (typically for updating resources).
  /// [headers] is an optional map of custom headers.
  ///
  /// Returns a [Response] object containing the server's response or an error.
  Future<Response> putData(String uri, dynamic body, {Map<String, String>? headers}) async {
    try {
      debugPrint('====> API Call: $uri\nHeader: $_mainHeaders');
      debugPrint('====> API Body: $body');
      http.Response _response = await http.put(
        Uri.parse(appBaseUrl + uri),
        body: jsonEncode(body),
        headers: headers ?? _mainHeaders,
      ).timeout(Duration(seconds: timeoutInSeconds));
      return handleResponse(_response, uri);
    } catch (e) {
      return const Response(statusCode: 1, statusText: noInternetMessage);
    }
  }

  /// Performs a DELETE request to the specified [uri].
  ///
  /// [headers] is an optional map of custom headers.
  ///
  /// Returns a [Response] object containing the server's response or an error.
  Future<Response> deleteData(String uri, {Map<String, String>? headers}) async {
    try {
      debugPrint('====> API Call: $uri\nHeader: $_mainHeaders');
      http.Response _response = await http.delete(
        Uri.parse(appBaseUrl + uri),
        headers: headers ?? _mainHeaders,
      ).timeout(Duration(seconds: timeoutInSeconds));
      return handleResponse(_response, uri);
    } catch (e) {
      return const Response(statusCode: 1, statusText: noInternetMessage);
    }
  }

  /// Processes the raw HTTP [response] and converts it into a standardized [Response] object.
  ///
  /// [uri] is the request URI, used for logging.
  ///
  /// Handles JSON decoding, error parsing, and status code verification.
  Response handleResponse(http.Response response, String uri) {

    dynamic _body;
    try {
      _body = jsonDecode(response.body);
    }catch(e) {
      debugPrint(e.toString());
    }
    Response _response = Response(
      body: _body ?? response.body, bodyString: response.body.toString(),
      headers: response.headers, statusCode: response.statusCode, statusText: response.reasonPhrase,
    );
    if(_response.statusCode != 200 && _response.body != null && _response.body is !String) {
      if(_response.body.toString().startsWith('{errors: [{code:')) {
        ErrorResponse _errorResponse = ErrorResponse.fromJson(_response.body);
        _response = Response(statusCode: _response.statusCode, body: _response.body, statusText: _errorResponse.errors![0].message);
      }else if(_response.body.toString().startsWith('{message')) {
        _response = Response(statusCode: _response.statusCode, body: _response.body, statusText: _response.body['message']);
      }
    }else if(_response.statusCode != 200 && _response.body == null) {
      _response = const Response(statusCode: 0, statusText: noInternetMessage);
    }
    log('====> API Response: [${_response.statusCode}] $uri\n${_response.body}');
    return _response;
  }
}