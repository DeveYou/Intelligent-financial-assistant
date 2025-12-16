import 'package:flutter/material.dart';
import 'package:intelligent_financial_assistant_frontend/features/assistant/controllers/assistant_controller.dart';
import 'package:intelligent_financial_assistant_frontend/features/assistant/widgets/voice_visualizer_widget.dart';
import 'package:intelligent_financial_assistant_frontend/localization/language_constraints.dart';
import 'package:provider/provider.dart';

class VocalChatbotScreen extends StatefulWidget {
  const VocalChatbotScreen({super.key});

  @override
  State<VocalChatbotScreen> createState() => _VocalChatbotScreenState();
}

class _VocalChatbotScreenState extends State<VocalChatbotScreen> with SingleTickerProviderStateMixin {
  late AnimationController _rippleController;

  @override
  void initState() {
    super.initState();
    // Initialize Assistant (Permissions + STT)
    WidgetsBinding.instance.addPostFrameCallback((_) {
      Provider.of<AssistantController>(context, listen: false).initAssistant();
    });

    _rippleController = AnimationController(
      vsync: this,
      duration: const Duration(seconds: 2),
    )..repeat();
  }

  @override
  void dispose() {
    _rippleController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Consumer<AssistantController>(
      builder: (context, controller, child) {
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

            // Animation Visualizer (Kept as is for frequency visualization)
            VoiceVisualizerWidget(state: controller.state),

            const Spacer(),

            // Mic Button with Ripple Animation
            GestureDetector(
              onTap: () {
                if (controller.state == AssistantState.listening) {
                   controller.stopListening();
                } else if (controller.state == AssistantState.idle || controller.state == AssistantState.speaking) {
                  controller.startListening();
                }
              },
              child: Stack(
                alignment: Alignment.center,
                children: [
                  // Ripple Effect
                  if (controller.state == AssistantState.listening)
                    CustomPaint(
                      painter: RipplePainter(_rippleController),
                      child: SizedBox(
                        width: 120,
                        height: 120,
                      ),
                    ),
                  
                  // Main Button
                  Container(
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
                ],
              ),
            ),
            const SizedBox(height: 50),
          ],
        );
      },
    );
  }
}

class RipplePainter extends CustomPainter {
  final Animation<double> animation;

  RipplePainter(this.animation) : super(repaint: animation);

  @override
  void paint(Canvas canvas, Size size) {
    final Paint paint = Paint()
      ..color = Colors.redAccent.withOpacity(1.0 - animation.value)
      ..style = PaintingStyle.stroke
      ..strokeWidth = 2;

    double radius = (size.width / 2) * animation.value;
    canvas.drawCircle(Offset(size.width / 2, size.height / 2), radius + 40, paint);
    
    // Second ripple
    double secondValue = (animation.value + 0.5) % 1.0;
    final Paint paint2 = Paint()
      ..color = Colors.redAccent.withOpacity(1.0 - secondValue)
      ..style = PaintingStyle.stroke
      ..strokeWidth = 2;
      
    double radius2 = (size.width / 2) * secondValue;
    canvas.drawCircle(Offset(size.width / 2, size.height / 2), radius2 + 40, paint2);
  }

  @override
  bool shouldRepaint(covariant CustomPainter oldDelegate) {
    return true;
  }
}