/// Represents a language option in the application.
///
/// Contains information about a supported language including its name,
/// language code, country code, and flag image.
class LanguageModel {
  /// URL/path to the language's flag image.
  String? imageUrl;
  
  /// Human-readable name of the language (e.g., "English", "French").
  String? languageName;
  
  /// ISO 639-1 language code (e.g., "en", "fr").
  String? languageCode;
  
  /// ISO 3166-1 country code (e.g., "US", "FR").
  String? countryCode;

  /// Creates a [LanguageModel] with the specified properties.
  LanguageModel({this.imageUrl, this.languageName, this.countryCode, this.languageCode});
}