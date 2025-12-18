import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:get/get.dart';
import 'package:get/get_navigation/src/root/get_material_app.dart';
import 'package:mocktail/mocktail.dart';
import 'package:dio/dio.dart' as dio;
import 'package:intelligent_financial_assistant_frontend/features/authentication/controllers/authentication_controller.dart';
import 'package:intelligent_financial_assistant_frontend/features/authentication/domains/services/authentication_service.dart';
import 'package:intelligent_financial_assistant_frontend/data/response/api_response.dart';
import 'package:intelligent_financial_assistant_frontend/features/authentication/domains/models/login_model.dart';
import 'package:intelligent_financial_assistant_frontend/features/authentication/domains/models/register_model.dart';

class MockAuthenticationService extends Mock implements AuthenticationService {}
class MockResponse extends Mock implements dio.Response {}

void main() {
  TestWidgetsFlutterBinding.ensureInitialized();
  late AuthenticationController authController;
  late MockAuthenticationService mockAuthService;

  tearDown(() {
    Get.reset();
  });

  group('AuthenticationController Tests', () {
    setUp(() {
       Get.testMode = true;
       mockAuthService = MockAuthenticationService();
       authController = AuthenticationController(authenticationService: mockAuthService);
    });

    testWidgets('signIn - success', (WidgetTester tester) async {
      final key = GlobalKey<NavigatorState>();
      Get.addKey(key);
      await tester.pumpWidget(GetMaterialApp(navigatorKey: key, home: const Scaffold(body: SizedBox())));
      
      // Arrange
      final loginModel = LoginModel(email: 'test@example.com', password: 'password123');
      final responseData = {
        'message': 'Login Successful',
        'token': 'fake_token_123',
        'temporary_token': '',
        'id': 1
      };
      
      final mockResponse = MockResponse();
      when(() => mockResponse.statusCode).thenReturn(200);
      when(() => mockResponse.data).thenReturn(responseData);

      // Mock specific service calls inside signIn
      when(() => mockAuthService.signIn(any())).thenAnswer((_) async => ApiResponse.withSuccess(mockResponse));
      when(() => mockAuthService.saveUserToken(any())).thenAnswer((_) async => {});
      when(() => mockAuthService.updateToken()).thenAnswer((_) async => ApiResponse.withSuccess(MockResponse()..statusCode=200));
      when(() => mockAuthService.saveUserId(any())).thenAnswer((_) async => {});

      bool callbackCalled = false;
      void callback(bool success, String? token, String? tempToken, String? msg) {
        callbackCalled = true;
        expect(success, true);
        expect(token, 'fake_token_123');
        expect(msg, 'Login Successful');
      }

      // Act
      await authController.signIn(loginModel, callback);

      // Assert
      verify(() => mockAuthService.signIn(any())).called(1);
      verify(() => mockAuthService.saveUserToken('fake_token_123')).called(1);
      verify(() => mockAuthService.saveUserId('1')).called(1);
      expect(callbackCalled, true);
      expect(authController.isLoading, false);
    });

    testWidgets('signIn - failure', (WidgetTester tester) async {
      final key = GlobalKey<NavigatorState>();
      Get.addKey(key);
      await tester.pumpWidget(GetMaterialApp(navigatorKey: key, home: const Scaffold(body: SizedBox())));

      // Arrange
      final loginModel = LoginModel(email: 'test@example.com', password: 'wrong_password');
      final errorMessage = 'Invalid Credentials';

      when(() => mockAuthService.signIn(any())).thenAnswer((_) async => ApiResponse.withError(errorMessage));

      bool callbackCalled = false;
      void callback(bool success, String? token, String? tempToken, String? msg) {
        callbackCalled = true;
      }

      // Act
      await authController.signIn(loginModel, callback);

      // Assert
      verify(() => mockAuthService.signIn(any())).called(1);
      expect(callbackCalled, false); // Callback should not be called on failure according to current impl
      expect(authController.isLoading, false);
    });

    testWidgets('registerUser - success', (WidgetTester tester) async {
       final key = GlobalKey<NavigatorState>();
       Get.addKey(key);
       await tester.pumpWidget(GetMaterialApp(navigatorKey: key, home: const Scaffold(body: SizedBox())));

      // Arrange
      final registerModel = RegisterModel(
        firstName: 'John', 
        lastName: 'Doe',
        email: 'john@example.com',
        password: 'password123'
      );
      
      final responseData = {
        'message': 'Registration Successful',
        'token': 'new_user_token',
        'id': 2
      };

      final mockResponse = MockResponse();
      when(() => mockResponse.statusCode).thenReturn(200);
      when(() => mockResponse.data).thenReturn(responseData);

      when(() => mockAuthService.registerUser(any())).thenAnswer((_) async => ApiResponse.withSuccess(mockResponse));
      when(() => mockAuthService.saveUserToken(any())).thenAnswer((_) async => {});
      when(() => mockAuthService.updateToken()).thenAnswer((_) async => ApiResponse.withSuccess(MockResponse()));
      when(() => mockAuthService.saveUserId(any())).thenAnswer((_) async => {});

      bool callbackCalled = false;
      void callback(bool success, String? token, String? tempToken, String? msg) {
        callbackCalled = true;
        expect(success, true);
        expect(token, 'new_user_token');
      }

      // Act
      await authController.registerUser(registerModel, callback);

      // Assert
      verify(() => mockAuthService.registerUser(any())).called(1);
      expect(callbackCalled, true);
    });
  });
}
