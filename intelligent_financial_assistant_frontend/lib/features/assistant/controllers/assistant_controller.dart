import 'package:flutter/material.dart';
import 'package:intelligent_financial_assistant_frontend/data/response/api_response.dart';
import 'package:intelligent_financial_assistant_frontend/features/assistant/domains/models/message_model.dart';
import 'package:intelligent_financial_assistant_frontend/features/assistant/domains/services/assistant_service_interface.dart';
import 'package:intelligent_financial_assistant_frontend/localization/language_constraints.dart';
import 'package:intelligent_financial_assistant_frontend/main.dart';
import 'package:speech_to_text/speech_to_text.dart' as stt;
import 'package:flutter_tts/flutter_tts.dart';

enum AssistantState { idle, listening, processing, speaking }

class AssistantController with ChangeNotifier {
  final AssistantServiceInterface assistantService;

  AssistantController({required this.assistantService});

  // State
  List<MessageModel> _messages = [];
  AssistantState _state = AssistantState.idle;
  bool _isVoiceMode = true;
  String _currentTranscript = "";

  // Tools
  late stt.SpeechToText _speech;
  late FlutterTts _flutterTts;
  bool _isSpeechAvailable = false;

  List<MessageModel> get messages => _messages;
  AssistantState get state => _state;
  bool get isVoiceMode => _isVoiceMode;
  String get currentTranscript => _currentTranscript;

  Future<void> initAssistant() async {
    _speech = stt.SpeechToText();
    _flutterTts = FlutterTts();
    _isSpeechAvailable = await _speech.initialize(
      onStatus: (status) {
        if (status == 'done' || status == 'notListening') {
          if (_state == AssistantState.listening) {
            _stopListeningAndSend();
          }
        }
      },
      onError: (error) => print('STT Error: $error'),
    );

    await _flutterTts.setLanguage("en-US");
    await _flutterTts.setPitch(1.0);

    // Set completion handler for TTS to return to idle
    _flutterTts.setCompletionHandler(() {
      _state = AssistantState.idle;
      notifyListeners();
    });

    notifyListeners();
  }

  void toggleViewMode() {
    _isVoiceMode = !_isVoiceMode;
    // Stop any active audio when switching views
    if (_state == AssistantState.speaking) _flutterTts.stop();
    if (_state == AssistantState.listening) _speech.stop();
    _state = AssistantState.idle;
    notifyListeners();
  }


  Future<void> startListening() async {
    if (!_isSpeechAvailable) {
      return;
    }

    if (_state == AssistantState.speaking) {
      await _flutterTts.stop();
    }

    _state = AssistantState.listening;
    _currentTranscript = "";
    notifyListeners();

    _speech.listen(
      onResult: (result) {
        _currentTranscript = result.recognizedWords;
        notifyListeners();
      },
    );
  }

  Future<void> _stopListeningAndSend() async {
    await _speech.stop();

    if (_currentTranscript.isNotEmpty) {
      // Add User Voice Message to History
      _addMessage(_currentTranscript, sender: "user", isVoice: true);

      // Process
      await _processMessage(_currentTranscript);
    } else {
      _state = AssistantState.idle;
      notifyListeners();
    }
  }

  Future<void> sendTextMessage(String text) async {
    if (text.trim().isEmpty) return;
    _addMessage(text, sender: "user", isVoice: false);
    await _processMessage(text);
  }

  Future<void> _processMessage(String text) async {
    _state = AssistantState.processing;
    notifyListeners();

    ApiResponse response = await assistantService.sendMessage(text);

    if (response.response != null && response.response!.statusCode == 200) {
      String botReply = response.response!.data['reply'] ?? getTranslated("i_processed_your_request", Get.context!)!;

      _addMessage(botReply, sender: "assistant");
      _speak(botReply);
    } else {
      String errorMsg = getTranslated("i_am_sorry_i_could_not_process_that_right_now", Get.context!)!;
      _addMessage(errorMsg, sender: "assistant");
      _speak(errorMsg);
    }
  }

  Future<void> _speak(String text) async {
    _state = AssistantState.speaking;
    notifyListeners();
    await _flutterTts.speak(text);
  }

  void _addMessage(String content, {required String sender, bool isVoice = false}) {
    _messages.add(MessageModel(
      content: content,
      sender: sender,
      createdAt: DateTime.now(),
      isVoice: isVoice,
    ));
    notifyListeners();
  }
}