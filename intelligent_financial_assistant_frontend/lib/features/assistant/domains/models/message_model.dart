class MessageModel {
  String? sender;
  String? content;
  DateTime? createdAt;
  bool? isVoice;

  MessageModel({this.sender, this.content, this.createdAt, this.isVoice = false});

  MessageModel.fromJson(Map<String, dynamic> json) {
    sender = json['sender'];
    content = json['content'];
    createdAt = DateTime.parse(json['created_at'] ?? DateTime.now().toIso8601String());
    isVoice  = json['is_voice'] ?? false;
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = <String, dynamic>{};
    data['sender'] = sender;
    data['content'] = content;
    data['created_at'] = createdAt?.toIso8601String();
    data['is_voice'] = isVoice;
    return data;
  }
}