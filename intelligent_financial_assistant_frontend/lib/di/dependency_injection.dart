import 'package:dio/dio.dart';
import 'package:get_it/get_it.dart';
import 'package:intelligent_financial_assistant_frontend/data/datasource/remote/dio/dio_client.dart';
import 'package:intelligent_financial_assistant_frontend/data/datasource/remote/dio/logging_interceptor.dart';
import 'package:intelligent_financial_assistant_frontend/features/account/controllers/account_controller.dart';
import 'package:intelligent_financial_assistant_frontend/features/account/domains/repositories/account_repository.dart';
import 'package:intelligent_financial_assistant_frontend/features/account/domains/repositories/account_repository_interface.dart';
import 'package:intelligent_financial_assistant_frontend/features/account/domains/services/account_service.dart';
import 'package:intelligent_financial_assistant_frontend/features/account/domains/services/account_service_interface.dart';
import 'package:intelligent_financial_assistant_frontend/features/assistant/controllers/assistant_controller.dart';
import 'package:intelligent_financial_assistant_frontend/features/assistant/domains/repositories/assistant_repository.dart';
import 'package:intelligent_financial_assistant_frontend/features/assistant/domains/repositories/assistant_repository_interface.dart';
import 'package:intelligent_financial_assistant_frontend/features/assistant/domains/services/assistant_service.dart';
import 'package:intelligent_financial_assistant_frontend/features/assistant/domains/services/assistant_service_interface.dart';
import 'package:intelligent_financial_assistant_frontend/features/authentication/controllers/authentication_controller.dart';
import 'package:intelligent_financial_assistant_frontend/features/authentication/domains/repositories/authentication_repository.dart';
import 'package:intelligent_financial_assistant_frontend/features/authentication/domains/repositories/authentication_repository_interface.dart';
import 'package:intelligent_financial_assistant_frontend/features/authentication/domains/services/authentication_service.dart';
import 'package:intelligent_financial_assistant_frontend/features/authentication/domains/services/authentication_service_interface.dart';
import 'package:intelligent_financial_assistant_frontend/features/home/controllers/home_controller.dart';
import 'package:intelligent_financial_assistant_frontend/features/home/domains/repositories/home_repository.dart';
import 'package:intelligent_financial_assistant_frontend/features/home/domains/repositories/home_repository_interface.dart';
import 'package:intelligent_financial_assistant_frontend/features/home/domains/services/home_service.dart';
import 'package:intelligent_financial_assistant_frontend/features/home/domains/services/home_service_interface.dart';
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
  sl.registerLazySingleton(() => HomeRepository(dioClient: sl()));
  sl.registerLazySingleton(() => AccountRepository(dioClient: sl()));
  sl.registerLazySingleton(() => AssistantRepository(dioClient: sl()));
  

  // Interfaces
  sl.registerLazySingleton<AuthenticationRepositoryInterface>(() => sl<AuthenticationRepository>());
  sl.registerLazySingleton<HomeRepositoryInterface>(() => sl<HomeRepository>());
  sl.registerLazySingleton<AccountRepositoryInterface>(() => sl<AccountRepository>());
  sl.registerLazySingleton<AssistantRepositoryInterface>(() => sl<AssistantRepository>());

  // Services
  sl.registerLazySingleton(() => AuthenticationService(authenticationRepository: sl()));
  sl.registerLazySingleton(() => HomeService(homeRepository: sl()));
  sl.registerLazySingleton(() => AccountService(accountRepository: sl()));
  sl.registerLazySingleton(() => AssistantService(assistantRepository: sl()));

  // Interface binding for Service
  sl.registerLazySingleton<AuthenticationServiceInterface>(() => sl<AuthenticationService>());
  sl.registerLazySingleton<HomeServiceInterface>(() => sl<HomeService>());
  sl.registerLazySingleton<AccountServiceInterface>(() => sl<AccountService>());
  sl.registerLazySingleton<AssistantServiceInterface>(() => sl<AssistantService>());

  // Providers / Controllers
  sl.registerFactory(() => LocalizationController(sharedPreferences: sl(), dioClient: sl()));
  sl.registerFactory(() => ThemeController(sharedPreferences: sl()));
  sl.registerFactory(() => AuthenticationController(authenticationService: sl()));
  sl.registerFactory(() => HomeController(homeService: sl()));
  sl.registerFactory(() => AccountController(accountService: sl()));
  sl.registerFactory(() => AssistantController(assistantService: sl()));

}