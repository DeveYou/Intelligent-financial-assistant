import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:get/get.dart';
import 'package:mocktail/mocktail.dart';
import 'package:dio/dio.dart' as dio;
import 'package:intelligent_financial_assistant_frontend/features/transaction/controllers/transaction_controller.dart';
import 'package:intelligent_financial_assistant_frontend/features/transaction/domains/services/transaction_service_interface.dart';
import 'package:intelligent_financial_assistant_frontend/features/transaction/domains/models/transaction_model.dart';
import 'package:intelligent_financial_assistant_frontend/features/recipient/domains/models/recipient_model.dart';
import 'package:intelligent_financial_assistant_frontend/data/response/api_response.dart';

// Mock Service
class MockTransactionService extends Mock implements TransactionServiceInterface {}

// Mock Translations to prevent localization errors
class _MockTranslations extends Translations {
  @override
  Map<String, Map<String, String>> get keys => {
    'en_US': {
      'insufficient_balance': 'Insufficient Balance',
      'request_timed_out_check_history': 'Request timed out',
    },
  };
}

void main() {
  TestWidgetsFlutterBinding.ensureInitialized();

  late TransactionController transactionController;
  late MockTransactionService mockTransactionService;

  setUpAll(() {
    Get.testMode = true;
  });

  setUp(() {
    mockTransactionService = MockTransactionService();
    transactionController = TransactionController(transactionService: mockTransactionService);

    registerFallbackValue(TransactionModel(amount: 0, type: 'transfer'));
  });

  tearDown(() {
    Get.reset();
  });

  group('TransactionController Tests', () {
    final fakeBeneficiary = RecipientModel(
      id: 1,
      bank: "VoxBank",
      iban: "MA123456789",
      fullName: "Test User",
      createdAt: DateTime.now(),
    );

    final tTransactionModel = TransactionModel(
      amount: 100.0,
      type: 'transfer',
      beneficiary: fakeBeneficiary,
    );

    // Helper to setup the Widget tree and Get Context
    Future<void> pumpContext(WidgetTester tester) async {
      await tester.pumpWidget(
        GetMaterialApp(
          home: const Scaffold(body: SizedBox()),
          translations: _MockTranslations(),
          locale: const Locale('en', 'US'),
        ),
      );
      // pumpAndSettle ensures the GetX navigation stack is fully mounted
      await tester.pumpAndSettle();
    }

    testWidgets('sendMoney - insufficient balance', (WidgetTester tester) async {
      await pumpContext(tester);

      // Arrange
      transactionController.accountBalance = 50.0; // Less than 100.0

      // Act
      final result = await transactionController.sendMoney(tTransactionModel);

      // Assert
      expect(result, false);
      expect(transactionController.isLoading, false);
      verifyNever(() => mockTransactionService.sendMoney(any()));
    });

    testWidgets('sendMoney - success', (WidgetTester tester) async {
      await pumpContext(tester);

      // Arrange
      when(() => mockTransactionService.getAccountBalance()).thenAnswer(
            (_) async => ApiResponse.withSuccess(
          dio.Response(
            requestOptions: dio.RequestOptions(path: ''),
            statusCode: 200,
            data: {'balance': 1000.0},
          ),
        ),
      );

      when(() => mockTransactionService.sendMoney(any())).thenAnswer(
            (_) async => ApiResponse.withSuccess(
          dio.Response(
            requestOptions: dio.RequestOptions(path: ''),
            statusCode: 200,
            data: {},
          ),
        ),
      );

      when(() => mockTransactionService.getTransactions(limit: any(named: 'limit')))
          .thenAnswer((_) async => ApiResponse.withSuccess(
        dio.Response(
          requestOptions: dio.RequestOptions(path: ''),
          statusCode: 200,
          data: [],
        ),
      ));

      // Act
      await transactionController.getAccountBalance();
      final result = await transactionController.sendMoney(tTransactionModel);

      // Assert
      expect(result, true);
      verify(() => mockTransactionService.sendMoney(tTransactionModel)).called(1);
    });

    testWidgets('sendMoney - timeout exception', (WidgetTester tester) async {
      await pumpContext(tester);

      // Arrange
      transactionController.accountBalance = 500.0; // Sufficient balance to pass first check

      when(() => mockTransactionService.sendMoney(any())).thenThrow(
        dio.DioException(
          requestOptions: dio.RequestOptions(path: ''),
          type: dio.DioExceptionType.connectionTimeout,
        ),
      );

      // Act
      final result = await transactionController.sendMoney(tTransactionModel);

      // Assert
      expect(result, false);
      expect(transactionController.isLoading, false);
    });
  });
}