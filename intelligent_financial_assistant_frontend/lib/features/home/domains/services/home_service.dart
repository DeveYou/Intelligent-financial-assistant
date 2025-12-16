import 'package:intelligent_financial_assistant_frontend/data/response/api_response.dart';
import 'package:intelligent_financial_assistant_frontend/features/home/domains/repositories/home_repository.dart';
import 'package:intelligent_financial_assistant_frontend/features/home/domains/services/home_service_interface.dart';

class HomeService implements HomeServiceInterface {

  HomeRepository homeRepository;

  HomeService({required this.homeRepository});

  @override
  Future<ApiResponse> getAccountSummary() async {
    return homeRepository.getAccountSummary();
  }

}