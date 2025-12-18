import 'package:flutter/material.dart';
import 'package:intelligent_financial_assistant_frontend/features/assistant/controllers/assistant_controller.dart';
import 'package:intelligent_financial_assistant_frontend/features/root.dart';
import 'package:intelligent_financial_assistant_frontend/localization/language_constraints.dart';
import 'package:provider/provider.dart';
import 'text_chatbot_screen.dart';
import 'vocal_chatbot_screen.dart';

/// AI Assistant screen with voice and text interfaces.
///
/// Provides a toggle between voice assistant mode and text chat mode.
/// Initializes the assistant controller on screen load.
class AssistantScreen extends StatefulWidget {
  /// Creates an [AssistantScreen].
  const AssistantScreen({super.key});

  @override
  State<AssistantScreen> createState() => _AssistantScreenState();
}

class _AssistantScreenState extends State<AssistantScreen> {
  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      Provider.of<AssistantController>(context, listen: false).initAssistant();
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        leading: IconButton(
            onPressed: () {
              Root.setPage(0);
            },
            icon: const Icon(Icons.arrow_back)
        ),
        title: Text(getTranslated("vox_assistant", context)!),
        actions: [
          // Switcher Button
          Consumer<AssistantController>(
            builder: (context, controller, _) {
              return IconButton(
                icon: Icon(controller.isVoiceMode ? Icons.keyboard : Icons.mic),
                tooltip: controller.isVoiceMode ? getTranslated("switch_to_keyboard", context)! : getTranslated("switch_to_voice", context)!,
                onPressed: () => controller.toggleViewMode(),
              );
            },
          )
        ],
      ),
      body: Consumer<AssistantController>(
        builder: (context, controller, _) {
          return AnimatedSwitcher(
            duration: const Duration(milliseconds: 300),
            child: controller.isVoiceMode
                ? const VocalChatbotScreen()
                : const TextChatbotScreen(),
          );
        },
      ),
    );
  }
}