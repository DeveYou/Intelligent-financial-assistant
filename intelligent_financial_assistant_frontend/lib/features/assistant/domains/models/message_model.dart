/// Represents a message in the AI assistant chat.
///
/// Messages can be from either the user or the assistant, and can be text or voice-based.
class MessageModel {
  /// Who sent the message ('user' or 'assistant').
  String? sender;
  
  /// The message content/text.
  String? content;
  
  /// When the message was created.
  DateTime? createdAt;
  
  /// Whether this message was sent via voice input.
  bool? isVoice;

  /// Creates a new [MessageModel] instance.
  MessageModel({this.sender, this.content, this.createdAt, this.isVoice = false});

  /// Creates a [MessageModel] from JSON data.
  MessageModel.fromJson(Map<String, dynamic> json) {
    sender = json['sender'];
    content = json['content'];
    createdAt = DateTime.parse(json['created_at'] ?? DateTime.now().toIso8601String());
    isVoice  = json['is_voice'] ?? false;
  }

  /// Converts this [MessageModel] to a JSON map.
  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = <String, dynamic>{};
    data['sender'] = sender;
    data['content'] = content;
    data['created_at'] = createdAt?.toIso8601String();
    data['is_voice'] = isVoice;
    return data;
  }
}