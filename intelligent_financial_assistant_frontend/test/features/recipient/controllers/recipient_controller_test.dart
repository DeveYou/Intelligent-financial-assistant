import 'package:flutter_test/flutter_test.dart';
import 'package:mocktail/mocktail.dart';
import 'package:dio/dio.dart';
import 'package:intelligent_financial_assistant_frontend/features/recipient/controllers/recipient_controller.dart';
import 'package:intelligent_financial_assistant_frontend/features/recipient/domains/services/recipient_service_interface.dart';
import 'package:intelligent_financial_assistant_frontend/data/response/api_response.dart';
import 'package:intelligent_financial_assistant_frontend/features/recipient/domains/models/recipient_model.dart';

class MockRecipientService extends Mock implements RecipientServiceInterface {}
class MockResponse extends Mock implements Response {}

void main() {
  late RecipientController recipientController;
  late MockRecipientService mockRecipientService;

  setUp(() {
    mockRecipientService = MockRecipientService();
    recipientController = RecipientController(recipientService: mockRecipientService);
  });

  group('RecipientController Tests', () {
    final tRecipientList = [
      {'id': 1, 'full_name': 'John Doe', 'iban': 'XA123'},
      {'id': 2, 'full_name': 'Jane Doe', 'iban': 'XA456'}
    ];

    test('getRecipientList - success', () async {
      // Arrange
      final responseMock = MockResponse();
      when(() => responseMock.statusCode).thenReturn(200);
      when(() => responseMock.data).thenReturn(tRecipientList); // List direct

      when(() => mockRecipientService.getRecipientList())
          .thenAnswer((_) async => ApiResponse.withSuccess(responseMock));

      // Act
      await recipientController.getRecipientList();

      // Assert
      expect(recipientController.recipientState, RecipientState.success);
      expect(recipientController.recipientList!.length, 2);
      expect(recipientController.error, isNull);
    });

    test('getRecipientList - failure', () async {
      // Arrange
      final errorMessage = 'Network Error';
      when(() => mockRecipientService.getRecipientList())
          .thenAnswer((_) async => ApiResponse.withError(errorMessage));

      // Act
      await recipientController.getRecipientList();

      // Assert
      expect(recipientController.recipientState, RecipientState.error);
      expect(recipientController.error, errorMessage);
    });

    test('addRecipient - success', () async {
      // Arrange
      final responseMock = MockResponse();
      when(() => responseMock.statusCode).thenReturn(200);
      when(() => responseMock.data).thenReturn({});

      when(() => mockRecipientService.addRecipient(any(), any()))
          .thenAnswer((_) async => ApiResponse.withSuccess(responseMock));
      
      // It calls getRecipientList on success
      final listResponseMock = MockResponse();
      when(() => listResponseMock.statusCode).thenReturn(200);
      when(() => listResponseMock.data).thenReturn([]);
      when(() => mockRecipientService.getRecipientList())
          .thenAnswer((_) async => ApiResponse.withSuccess(listResponseMock));

      // Act
      final result = await recipientController.addRecipient('New User', 'XA999');

      // Assert
      expect(result, true);
      verify(() => mockRecipientService.addRecipient('New User', 'XA999')).called(1);
      verify(() => mockRecipientService.getRecipientList()).called(1);
    });

    test('addRecipient - failure', () async {
      // Arrange
      when(() => mockRecipientService.addRecipient(any(), any()))
          .thenAnswer((_) async => ApiResponse.withError('Failed'));

      // Act
      final result = await recipientController.addRecipient('New User', 'XA999');

      // Assert
      expect(result, false);
      expect(recipientController.error, 'Failed');
    });
  });
}
