import 'package:flutter/material.dart';
import 'package:intelligent_financial_assistant_frontend/features/assistant/controllers/assistant_controller.dart';
import 'package:intelligent_financial_assistant_frontend/features/assistant/widgets/voice_visualizer_widget.dart';
import 'package:intelligent_financial_assistant_frontend/localization/language_constraints.dart';
import 'package:provider/provider.dart';

class VocalChatbotScreen extends StatelessWidget {
  const VocalChatbotScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final controller = Provider.of<AssistantController>(context);

    return Column(
      mainAxisAlignment: MainAxisAlignment.center,
      children: [
        const Spacer(),
        // Status Text
        Text(
          controller.state == AssistantState.listening ? getTranslated("listening", context)! :
          controller.state == AssistantState.processing ? getTranslated("thinking", context)! :
          controller.state == AssistantState.speaking ? getTranslated("speaking", context)! : getTranslated("tap_to_speak", context)!,
          style: TextStyle(fontSize: 24, fontWeight: FontWeight.bold, color: Colors.grey[800]),
        ),
        const SizedBox(height: 20),

        // Live Transcript while listening
        if (controller.state == AssistantState.listening)
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 30),
            child: Text(
              controller.currentTranscript,
              textAlign: TextAlign.center,
              style: TextStyle(fontSize: 18, color: Colors.grey[600]),
            ),
          ),

        const Spacer(),

        // Animation Visualizer
        VoiceVisualizerWidget(state: controller.state),

        const Spacer(),

        // Mic Button
        GestureDetector(
          onTap: () {
            if (controller.state == AssistantState.idle || controller.state == AssistantState.speaking) {
              controller.startListening();
            }
          },
          child: Container(
            height: 80,
            width: 80,
            decoration: BoxDecoration(
              color: controller.state == AssistantState.listening ? Colors.redAccent : Theme.of(context).primaryColor,
              shape: BoxShape.circle,
              boxShadow: [
                BoxShadow(
                    color: (controller.state == AssistantState.listening ? Colors.redAccent : Theme.of(context).primaryColor).withOpacity(0.4),
                    blurRadius: 20,
                    spreadRadius: 5
                )
              ],
            ),
            child: Icon(
              controller.state == AssistantState.listening ? Icons.stop : Icons.mic,
              color: Colors.white,
              size: 36,
            ),
          ),
        ),
        const SizedBox(height: 50),
      ],
    );
  }
}