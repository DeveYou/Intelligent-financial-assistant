import 'package:flutter_test/flutter_test.dart';
import 'package:intelligent_financial_assistant_frontend/features/account/domains/models/account_model.dart';

void main() {
  test('AccountModel.fromJson handles DateTime object in createdAt field', () {
    final Map<String, dynamic> jsonWithDateTime = {
      'id': 1,
      'createdAt': DateTime.now(), // Simulating the issue where it's already a DateTime
      'currency': 'MAD'
    };

    try {
      final account = AccountModel.fromJson(jsonWithDateTime);
      expect(account.createdAt, isNotNull);
      print('Successfully parsed AccountModel with DateTime object in createdAt');
    } catch (e) {
      print('Caught expected error: $e');
      rethrow;
    }
  });

  test('AccountModel.fromJson handles String in createdAt field', () {
     final Map<String, dynamic> jsonWithString = {
      'id': 1,
      'createdAt': DateTime.now().toIso8601String(),
      'currency': 'MAD'
    };

    final account = AccountModel.fromJson(jsonWithString);
    expect(account.createdAt, isNotNull);
  });
}
