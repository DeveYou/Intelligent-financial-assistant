import 'package:flutter_test/flutter_test.dart';
import 'package:mocktail/mocktail.dart';
import 'package:dio/dio.dart';
import 'package:intelligent_financial_assistant_frontend/features/account/controllers/account_controller.dart';
import 'package:intelligent_financial_assistant_frontend/features/account/domains/services/account_service_interface.dart';
import 'package:intelligent_financial_assistant_frontend/data/response/api_response.dart';

// Mocks
class MockAccountService extends Mock implements AccountServiceInterface {}
class MockResponse extends Mock implements Response {}

void main() {
  late AccountController accountController;
  late MockAccountService mockAccountService;

  setUp(() {
    mockAccountService = MockAccountService();
    accountController = AccountController(accountService: mockAccountService);
  });

  tearDown(() {
    accountController.dispose();
  });

  group('AccountController - initAccountData', () {
    final tAccountApiJson = {
      'id': 1,
      'createdAt': '2023-01-01T00:00:00.000Z',
      'currency': 'MAD',
      'balance': 1000.0,
      'isActive': true,
      'isPaymentByCard': true,
      'isWithdrawal': true,
      'isOnlinePayment': true,
      'isContactless': true,
    };

    final tTransactionApiList = [
      {
        'id': 101, // Changed from String '101' to int 101 to match TransactionModel
        'type': 'transfer',
        'amount': 500.0,
        'date': '2023-01-02T00:00:00.000Z',
        // Add other required fields for TransactionModel.fromJson if needed
      }
    ];

    test('should fetch account details and transactions successfully', () async {
      // Arrange
      final responseDetails = MockResponse();
      when(() => responseDetails.statusCode).thenReturn(200);
      when(() => responseDetails.data).thenReturn(tAccountApiJson);

      final responseTransactions = MockResponse();
      when(() => responseTransactions.statusCode).thenReturn(200);
      when(() => responseTransactions.data).thenReturn(tTransactionApiList);

      when(() => mockAccountService.getAccountDetails())
          .thenAnswer((_) async => ApiResponse.withSuccess(responseDetails));
      when(() => mockAccountService.getRecentTransactions())
          .thenAnswer((_) async => ApiResponse.withSuccess(responseTransactions));

      // Act
      // Initial state check
      expect(accountController.isLoading, true);
      
      final future = accountController.initAccountData();
      
      // State check during execution (should still be loading)
      expect(accountController.isLoading, true);

      await future;

      // Assert Final Success State
      expect(accountController.accountState, AccountState.success);
      expect(accountController.accountModel, isNotNull);
      expect(accountController.accountModel!.id, 1);
      expect(accountController.recentTransactions, isNotNull);
      expect(accountController.recentTransactions!.length, 1);
      expect(accountController.error, isEmpty);

      // Verify Service Calls
      verify(() => mockAccountService.getAccountDetails()).called(1);
      verify(() => mockAccountService.getRecentTransactions()).called(1);
    });

    test('should handle API error when fetching account details fails', () async {
      // Arrange
      final errorMessage = 'Server Error 500';
      
      when(() => mockAccountService.getAccountDetails())
          .thenAnswer((_) async => ApiResponse.withError(errorMessage));
      
      // Mock transactions as success to isolate failure
      final responseTrans = MockResponse();
      when(() => responseTrans.statusCode).thenReturn(200);
      when(() => responseTrans.data).thenReturn([]);
      when(() => mockAccountService.getRecentTransactions())
          .thenAnswer((_) async => ApiResponse.withSuccess(responseTrans));

      // Act
      await accountController.initAccountData();

      // Assert
      expect(accountController.accountState, AccountState.error);
      expect(accountController.error, contains(errorMessage));
      expect(accountController.accountModel, isNull);
      
      verify(() => mockAccountService.getAccountDetails()).called(1);
    });

    test('should handle Exception thrown by service', () async {
      // Arrange
      when(() => mockAccountService.getAccountDetails()).thenThrow(Exception('Network Failure'));
      when(() => mockAccountService.getRecentTransactions())
          .thenAnswer((_) async => ApiResponse.withError('Cancelled'));

      // Act
      await accountController.initAccountData();

      // Assert
      expect(accountController.accountState, AccountState.error);
      expect(accountController.error, contains('Unexpected error'));
      
      verify(() => mockAccountService.getAccountDetails()).called(1);
    });
  });
}
