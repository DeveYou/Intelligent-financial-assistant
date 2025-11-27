import 'package:dio/dio.dart';
import 'package:get_it/get_it.dart';
import 'package:intelligent_financial_assistant_frontend/data/datasource/remote/dio/dio_client.dart';
import 'package:intelligent_financial_assistant_frontend/data/datasource/remote/dio/logging_interceptor.dart';
import 'package:intelligent_financial_assistant_frontend/features/authentication/controllers/authentication_controller.dart';
import 'package:intelligent_financial_assistant_frontend/features/authentication/domains/repositories/authentication_repository.dart';
import 'package:intelligent_financial_assistant_frontend/features/authentication/domains/repositories/authentication_repository_interface.dart';
import 'package:intelligent_financial_assistant_frontend/features/authentication/domains/services/authentication_service.dart';
import 'package:intelligent_financial_assistant_frontend/features/authentication/domains/services/authentication_service_interface.dart';
import 'package:intelligent_financial_assistant_frontend/localization/controllers/localization_controller.dart';
import 'package:intelligent_financial_assistant_frontend/theme/controllers/theme_controller.dart';
import 'package:intelligent_financial_assistant_frontend/utils/app_constants.dart';
import 'package:shared_preferences/shared_preferences.dart';

final sl = GetIt.instance;

Future<void> init() async {
  // Core
  final sharedPreferences = await SharedPreferences.getInstance();
  sl.registerLazySingleton<SharedPreferences>(() => sharedPreferences);
  sl.registerLazySingleton(() => Dio());
  sl.registerLazySingleton(() => LoggingInterceptor());
  sl.registerLazySingleton(() => DioClient(AppConstants.baseUrl, sl(), loggingInterceptor: sl(), sharedPreferences: sl()));

  // Repositories
  sl.registerLazySingleton(() => AuthenticationRepository(dioClient: sl(), sharedPreferences: sl()));

  // Interfaces
  sl.registerLazySingleton<AuthenticationRepositoryInterface>(() => sl<AuthenticationRepository>());

  // Services
  sl.registerLazySingleton(() => AuthenticationService(authenticationRepository: sl()));

  // Interface binding for Service
  sl.registerLazySingleton<AuthenticationServiceInterface>(() => sl<AuthenticationService>());

  // Providers / Controllers
  sl.registerFactory(() => LocalizationController(sharedPreferences: sl(), dioClient: sl()));
  sl.registerFactory(() => ThemeController(sharedPreferences: sl()));
  sl.registerFactory(() => AuthenticationController(authenticationService: sl()));

}