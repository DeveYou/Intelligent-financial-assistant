import 'dart:io';
import 'package:dio/dio.dart';
import 'package:intelligent_financial_assistant_frontend/data/datasource/remote/dio/logging_interceptor.dart';
import 'package:intelligent_financial_assistant_frontend/utils/app_constants.dart';
import 'package:shared_preferences/shared_preferences.dart';

class DioClient {
  final String baseUrl;
  final LoggingInterceptor loggingInterceptor;
  final SharedPreferences sharedPreferences;

  Dio? dio;
  String? token;
  String? countryCode;

  DioClient(
      this.baseUrl,
      Dio? dioc,
      {
        required this.loggingInterceptor,
        required this.sharedPreferences
      }
  ) {
    token = sharedPreferences.getString(AppConstants.userLoginToken);
    countryCode = sharedPreferences.getString(AppConstants.countryCode) ?? AppConstants.languages[0].countryCode;

    dio = dioc ?? Dio();

    dio
      ?..options.baseUrl = baseUrl
      ..options.connectTimeout = const Duration(seconds: 120)
      ..options.receiveTimeout = const Duration(seconds: 120)
      ..httpClientAdapter
      ..options.headers = {
        'Content-Type': 'application/json; charset=UTF-8',
        'Authorization': token != null ? 'Bearer $token' : '',
        'Accept': 'application/json',
        AppConstants.langKey : countryCode == 'US' ? 'en' : countryCode!.toLowerCase(),
      };

    dio!.interceptors.add(loggingInterceptor);
  }
  
  void updateHeader(String? token, String? countryCode) {
    token = token ?? this.token;
    countryCode = countryCode == null ? this.countryCode == 'US' ? 'en' : this.countryCode!.toLowerCase() : countryCode == 'US' ? 'en' : countryCode.toLowerCase();
    this.token = token;
    this.countryCode = countryCode;
    dio!.options.headers = {
      'Content-Type': 'application/json; charset=UTF-8',
      'Authorization': token != null ? 'Bearer $token' : '',
      'Accept': 'application/json',
      AppConstants.langKey : countryCode == 'US' ? 'en' : countryCode.toLowerCase(),
    };
  }

  Future<Response> get(String uri, {Map<String, dynamic>? queryParameters, Options? options, CancelToken? cancelToken, ProgressCallback? onReceiveProgress}) async {
    try {
      Response response = await dio!.get(uri, queryParameters: queryParameters, options: options, cancelToken: cancelToken, onReceiveProgress: onReceiveProgress);
      return response;
    } on SocketException catch (e) {
      throw SocketException(e.toString());
    } on FormatException catch (_) {
      throw const FormatException("Unable to process the data");
    } catch (e) {
      rethrow;
    }
  }


  Future<Response> post(String uri, {dynamic data, Map<String, dynamic>? queryParameters, Options? options, CancelToken? cancelToken, ProgressCallback? onSendProgress, ProgressCallback? onReceiveProgress}) async {
    try {
      Response response = await dio!.post(uri, data: data, queryParameters: queryParameters, options: options, cancelToken: cancelToken, onSendProgress: onSendProgress, onReceiveProgress: onReceiveProgress);
      return response;
    } on SocketException catch (e) {
      throw SocketException(e.toString());
    } on FormatException catch (_) {
      throw const FormatException("Unable to process the data");
    } catch (e) {
      rethrow;
    }
  }

  Future<Response> put(String uri, {dynamic data, Map<String, dynamic>? queryParameters, Options? options, CancelToken? cancelToken, ProgressCallback? onSendProgress, ProgressCallback? onReceiveProgress}) async {
    try {
      Response response = await dio!.put(uri, data: data, queryParameters: queryParameters, options: options, cancelToken: cancelToken, onSendProgress: onSendProgress, onReceiveProgress: onReceiveProgress);
      return response;
    } on SocketException catch (e) {
      throw SocketException(e.toString());
    } on FormatException catch (_) {
      throw const FormatException("Unable to process the data");
    } catch (e) {
      rethrow;
    }
  }

  Future<Response> delete(String uri, {dynamic data, Map<String, dynamic>? queryParameters, Options? options, CancelToken? cancelToken}) async {
    try {
      Response response = await dio!.delete(uri, data: data, queryParameters: queryParameters, options: options, cancelToken: cancelToken);
      return response;
    } on SocketException catch (e) {
      throw SocketException(e.toString());
    } on FormatException catch (_) {
      throw const FormatException("Unable to process the data");
    } catch (e) {
      rethrow;
    }
  }

}