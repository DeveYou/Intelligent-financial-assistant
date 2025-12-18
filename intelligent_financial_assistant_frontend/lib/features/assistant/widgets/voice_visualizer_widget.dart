import 'package:flutter/material.dart';
import 'package:intelligent_financial_assistant_frontend/features/assistant/controllers/assistant_controller.dart';

class VoiceVisualizerWidget extends StatefulWidget {
  final AssistantState state;
  const VoiceVisualizerWidget({super.key, required this.state});

  @override
  State<VoiceVisualizerWidget> createState() => _VoiceVisualizerWidgetState();
}

class _VoiceVisualizerWidgetState extends State<VoiceVisualizerWidget> with SingleTickerProviderStateMixin {
  late AnimationController _controller;

  @override
  void initState() {
    super.initState();
    _controller = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 1000),
    )..repeat(reverse: true);
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    // Only animate if listening or speaking
    bool isAnimating = (widget.state == AssistantState.listening || widget.state == AssistantState.speaking);

    return Center(
      child: isAnimating
          ? AnimatedBuilder(
        animation: _controller,
        builder: (context, child) {
          return Row(
            mainAxisAlignment: MainAxisAlignment.center,
            children: List.generate(5, (index) {
              return Container(
                margin: const EdgeInsets.symmetric(horizontal: 5),
                width: 10,
                height: 40 + (_controller.value * 40 * (index % 2 == 0 ? 1 : 0.5)),
                decoration: BoxDecoration(
                  color: Theme.of(context).primaryColor.withValues(alpha: 0.8),
                  borderRadius: BorderRadius.circular(15),
                ),
              );
            }),
          );
        },
      )
          : Container(
        width: 80,
        height: 80,
        decoration: BoxDecoration(
          color: Colors.grey[200],
          shape: BoxShape.circle,
        ),
        child: Icon(Icons.mic_none, size: 40, color: Colors.grey[400]),
      ),
    );
  }
}