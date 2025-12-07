import 'package:dio/dio.dart';
import 'package:intelligent_financial_assistant_frontend/data/datasource/remote/dio/dio_client.dart';
import 'package:intelligent_financial_assistant_frontend/data/response/api_response.dart';
import 'package:intelligent_financial_assistant_frontend/features/home/domains/repositories/home_repository_interface.dart';
import 'package:intelligent_financial_assistant_frontend/utils/app_constants.dart';

class HomeRepository implements HomeRepositoryInterface {
  final DioClient? dioClient;

  HomeRepository({required this.dioClient});

  @override
  Future<ApiResponse> getAccountSummary() async {
    final Response apiResponse = await dioClient!.get(AppConstants.homeUri);
    try {
      return ApiResponse.withSuccess(apiResponse);
    } catch (e) {
      return ApiResponse.withError('Failed to load account summary');
    }
  }
}