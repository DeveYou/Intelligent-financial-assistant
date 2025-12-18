
/// Service interface for home screen operations.
///
/// Handles fetching account summary data displayed on the home screen.
abstract class HomeServiceInterface {
  /// Retrieves the account summary for the home screen.
  ///
  /// Returns account holder name, balance, IBAN, and currency information.
  Future<dynamic> getAccountSummary();
}