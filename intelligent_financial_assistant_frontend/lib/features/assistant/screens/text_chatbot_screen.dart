import 'package:flutter/material.dart';
import 'package:intelligent_financial_assistant_frontend/features/assistant/controllers/assistant_controller.dart';
import 'package:intelligent_financial_assistant_frontend/features/assistant/domains/models/message_model.dart';
import 'package:intelligent_financial_assistant_frontend/localization/language_constraints.dart';
import 'package:provider/provider.dart';
import 'package:intl/intl.dart';

class TextChatbotScreen extends StatefulWidget {
  const TextChatbotScreen({super.key});

  @override
  State<TextChatbotScreen> createState() => _TextChatbotScreenState();
}

class _TextChatbotScreenState extends State<TextChatbotScreen> {
  final TextEditingController _textController = TextEditingController();
  final ScrollController _scrollController = ScrollController();

  @override
  Widget build(BuildContext context) {
    final assistantController = Provider.of<AssistantController>(context);

    // Scroll to bottom when message added
    WidgetsBinding.instance.addPostFrameCallback((_) {
      if (_scrollController.hasClients) {
        _scrollController.animateTo(
          _scrollController.position.maxScrollExtent,
          duration: const Duration(milliseconds: 300),
          curve: Curves.easeOut,
        );
      }
    });

    return Column(
      children: [
        // Chat List
        Expanded(
          child: ListView.builder(
            controller: _scrollController,
            padding: const EdgeInsets.all(16),
            itemCount: assistantController.messages.length,
            itemBuilder: (context, index) {
              final msg = assistantController.messages[index];
              return _buildChatBubble(msg, context);
            },
          ),
        ),

        // Input Area
        Container(
          padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 10),
          decoration: BoxDecoration(
              color: Colors.white,
              boxShadow: [BoxShadow(color: Colors.grey.withOpacity(0.1), blurRadius: 5, offset: const Offset(0,-2))]
          ),
          child: Row(
            children: [
              Expanded(
                child: TextField(
                  controller: _textController,
                  decoration: InputDecoration(
                    hintText: getTranslated("type_your_request", context)!,
                    border: OutlineInputBorder(borderRadius: BorderRadius.circular(30), borderSide: BorderSide.none),
                    filled: true,
                    fillColor: Colors.grey[100],
                    contentPadding: const EdgeInsets.symmetric(horizontal: 20, vertical: 12),
                  ),
                  onSubmitted: (val) => _sendMessage(assistantController),
                ),
              ),
              const SizedBox(width: 10),
              FloatingActionButton(
                mini: true,
                elevation: 0,
                backgroundColor: Theme.of(context).primaryColor,
                child: const Icon(Icons.send, color: Colors.white, size: 18),
                onPressed: () => _sendMessage(assistantController),
              ),
            ],
          ),
        ),
      ],
    );
  }

  void _sendMessage(AssistantController controller) {
    if (_textController.text.trim().isNotEmpty) {
      controller.sendTextMessage(_textController.text);
      _textController.clear();
    }
  }

  Widget _buildChatBubble(MessageModel msg, BuildContext context) {
    final sender = msg.sender;
    final align = sender != null ? CrossAxisAlignment.end : CrossAxisAlignment.start;
    final bgColor = sender != null ? Theme.of(context).primaryColor : Colors.grey[200];
    final textColor = sender != null ? Colors.white : Colors.black87;

    return Column(
      crossAxisAlignment: align,
      children: [
        Container(
          margin: const EdgeInsets.only(bottom: 8),
          padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
          constraints: BoxConstraints(maxWidth: MediaQuery.of(context).size.width * 0.75),
          decoration: BoxDecoration(
            color: bgColor,
            borderRadius: BorderRadius.only(
              topLeft: const Radius.circular(16),
              topRight: const Radius.circular(16),
              bottomLeft: sender != null ? const Radius.circular(16) : Radius.zero,
              bottomRight: sender != null ? Radius.zero : const Radius.circular(16),
            ),
          ),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              if (msg.isVoice!)
                Icon(Icons.mic, size: 14, color: sender != null ? Colors.white70 : Colors.grey),
              Text(
                msg.content!,
                style: TextStyle(color: textColor, fontSize: 15),
              ),
            ],
          ),
        ),
        Padding(
          padding: const EdgeInsets.only(bottom: 12.0),
          child: Text(
            DateFormat('h:mm a').format(msg.createdAt!),
            style: const TextStyle(fontSize: 10, color: Colors.grey),
          ),
        ),
      ],
    );
  }
}