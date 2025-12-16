import 'package:flutter/material.dart';
import 'package:intelligent_financial_assistant_frontend/data/response/api_response.dart';
import 'package:intelligent_financial_assistant_frontend/features/assistant/domains/models/message_model.dart';
import 'package:intelligent_financial_assistant_frontend/features/assistant/domains/services/assistant_service_interface.dart';
import 'package:intelligent_financial_assistant_frontend/features/account/domains/repositories/account_repository_interface.dart';
import 'package:intelligent_financial_assistant_frontend/features/transaction/domains/repositories/transaction_repository_interface.dart';
import 'package:intelligent_financial_assistant_frontend/features/account/domains/models/account_model.dart';
import 'package:intl/intl.dart';
import 'package:intelligent_financial_assistant_frontend/localization/language_constraints.dart';
import 'package:intelligent_financial_assistant_frontend/main.dart';
import 'package:speech_to_text/speech_to_text.dart' as stt;
import 'package:flutter_tts/flutter_tts.dart';
import 'package:provider/provider.dart';
import 'package:uuid/uuid.dart';
import 'package:intelligent_financial_assistant_frontend/features/transaction/controllers/transaction_controller.dart';
import 'package:intelligent_financial_assistant_frontend/features/transaction/domains/models/transaction_model.dart';
import 'package:intelligent_financial_assistant_frontend/features/recipient/domains/models/recipient_model.dart';
import 'package:intelligent_financial_assistant_frontend/utils/app_constants.dart';
import 'package:permission_handler/permission_handler.dart';

enum AssistantState { idle, listening, processing, speaking }

class AssistantController with ChangeNotifier {
  final AssistantServiceInterface assistantService;
  final AccountRepositoryInterface accountRepository;
  final TransactionRepositoryInterface transactionRepository;

  AssistantController({required this.assistantService, required this.accountRepository, required this.transactionRepository});

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
  bool get isSpeechAvailable => _isSpeechAvailable;

  Future<void> initAssistant() async {
    _speech = stt.SpeechToText();
    _flutterTts = FlutterTts();

    await _checkPermissions();
    if (await Permission.microphone.isGranted) {
        _isSpeechAvailable = await _speech.initialize(
          onStatus: (status) {
            if (status == 'done' || status == 'notListening') {
              if (_state == AssistantState.listening) {
                 _stopListeningAndSend();
              }
            }
          },
          onError: (error) {
             print('STT Error: ${error.errorMsg}');
             if (_state == AssistantState.listening) {
                _state = AssistantState.idle;
                notifyListeners();
             }
          },
        );
    }

    await _flutterTts.setLanguage("en-US");
    await _flutterTts.setPitch(1.0);

    // Set completion handler for TTS to return to idle
    _flutterTts.setCompletionHandler(() {
      _state = AssistantState.idle;
      notifyListeners();
    });

    notifyListeners();
  }
  
  Future<void> _checkPermissions() async {
    var status = await Permission.microphone.status;
    if (!status.isGranted) {
      status = await Permission.microphone.request();
    }
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
    var status = await Permission.microphone.status;
    if (!status.isGranted) {
         await _checkPermissions();
         if (!await Permission.microphone.isGranted) {
             // Show permission guidance if still denied
             return;
         }
    }

    if (!_isSpeechAvailable) {
       // Try initializing again if it failed before but now we might have perms
       _isSpeechAvailable = await _speech.initialize(
          onStatus: (status) {
            if (status == 'done' || status == 'notListening') {
              if (_state == AssistantState.listening) {
                 _stopListeningAndSend();
              }
            }
          },
          onError: (error) {
              print('STT Error: ${error.errorMsg}');
              _state = AssistantState.idle;
              notifyListeners();
          },
       );
       if (!_isSpeechAvailable) return;
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

  Future<void> stopListening() async {
      await _stopListeningAndSend();
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
      try {
        Map<String, dynamic> data = response.response!.data;
        await handleAssistantAction(data);
      } catch (e) {
        String errorMsg = "Error processing assistant response.";
         debugPrint("Assistant Parsing Error: $e");
        _addMessage(errorMsg, sender: "assistant");
        _speak(errorMsg);
      }
    } else {
      String errorMessage = response.error != null ? response.error.toString() : getTranslated("i_am_sorry_i_could_not_process_that_right_now", Get.context!)!;
      debugPrint("Assistant API Error: $errorMessage");
      
      _addMessage(errorMessage, sender: "assistant");
      _speak(errorMessage);
    }
  }

  Future<void> handleAssistantAction(Map<String, dynamic> data) async {
    String action = data['action'];
    final context = Get.context!; 
    final transactionController = Provider.of<TransactionController>(context, listen: false);

    switch (action) {
      case "getBalance":
        try {
           ApiResponse apiResponse = await accountRepository.getAccountDetails();
           if (apiResponse.response != null && apiResponse.response!.statusCode == 200) {
              var responseData = apiResponse.response!.data;
              AccountModel? account;
              
              if (responseData is List) {
                 if(responseData.isNotEmpty) {
                    account = AccountModel.fromJson(responseData[0]);
                 }
              } else {
                 account = AccountModel.fromJson(responseData);
              }

              if (account != null) {
                  double balance = account.balance ?? 0.0;
                  String currency = account.currency ?? "MAD";
                  String reply = "Your current balance is $currency ${balance.toStringAsFixed(2)}";
                  _addMessage(reply, sender: "assistant");
                  _speak(reply);
              } else {
                  String reply = "I couldn't find any account information.";
                  _addMessage(reply, sender: "assistant");
                  _speak(reply);
              }
           } else {
              String reply = "I had trouble accessing your account details.";
              _addMessage(reply, sender: "assistant");
              _speak(reply);
           }
        } catch (e) {
           String reply = "Sorry, something went wrong while fetching your balance.";
           _addMessage(reply, sender: "assistant");
           _speak(reply);
        }
        break;

      case "getLastTransaction":
        ApiResponse apiResponse = await transactionRepository.getLastTransaction();
        if (apiResponse.response != null && apiResponse.response!.statusCode == 200) {
           var responseData = apiResponse.response!.data;
           TransactionModel? lastTrx;
           
           if (responseData is List && responseData.isNotEmpty) {
               lastTrx = TransactionModel.fromJson(responseData[0]);
           } else if (responseData is Map<String, dynamic>) {
               lastTrx = TransactionModel.fromJson(responseData);
           }

            if (lastTrx != null) {
              String date = lastTrx.createdAt != null 
                  ? DateFormat('yyyy-MM-dd HH:mm').format(lastTrx.createdAt!) 
                  : "recently";
              String reply = "Your last transaction was a ${lastTrx.type ?? 'transaction'} of \$${lastTrx.amount} to ${lastTrx.beneficiary?.fullName ?? 'Unknown'} on $date";
              _addMessage(reply, sender: "assistant");
              _speak(reply);
            } else {
              String reply = "No recent transactions found.";
              _addMessage(reply, sender: "assistant");
              _speak(reply);
            }
        } else {
            String reply = "I couldn't fetch your transaction history.";
            _addMessage(reply, sender: "assistant");
            _speak(reply);
        }
        break;

      case "sendMoney":
        try {
          double amount = (data['amount'] as num).toDouble();
          String recipientName = data['recipient'];

          TransactionModel transaction = TransactionModel(
            type: "TRANSFER",
            amount: amount,
            beneficiary: RecipientModel(fullName: recipientName),
            reference: const Uuid().v4(),
            chargeAmount: AppConstants.transactionCharge,
            reason: "AI Assistant Transfer",
            createdAt: DateTime.now(),
          );

          bool success = await transactionController.sendMoney(transaction);

          if (success) {
            String reply = "Transfer successful. \$$amount sent to $recipientName.";
            _addMessage(reply, sender: "assistant");
            _speak(reply);
          } else {
            String reply = "Transfer failed. Please check your balance or try again.";
            _addMessage(reply, sender: "assistant");
            _speak(reply);
          }
        } catch (e) {
           String reply = "I couldn't complete the transfer details.";
          _addMessage(reply, sender: "assistant");
          _speak(reply);
        }
        break;

      default:
        String reply = "I'm not sure how to do that yet.";
        _addMessage(reply, sender: "assistant");
        _speak(reply);
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