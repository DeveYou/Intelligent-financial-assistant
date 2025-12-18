/// Repository interface for home screen data operations.
///
/// Handles direct API communication for fetching home screen account summary.
abstract class HomeRepositoryInterface {
  /// Fetches the account summary from the API.
  ///
  /// Returns account information including balance, IBAN, and account holder details.
  Future<dynamic> getAccountSummary();
}