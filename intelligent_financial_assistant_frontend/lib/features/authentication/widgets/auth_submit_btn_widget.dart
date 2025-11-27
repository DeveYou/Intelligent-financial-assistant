import 'package:flutter/material.dart';

class AuthSubmitBtnWidget extends StatelessWidget {
  final String text;
  final VoidCallback onTap;

  const AuthSubmitBtnWidget({
    super.key,
    required this.text,
    required this.onTap,
  });

  final Color _primaryColor = const Color(0xFFF44E1E);
  final Color _secondaryColor = const Color(0xFFFFBD09);

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: onTap,
      child: Container(
        width: double.infinity,
        height: 56,
        decoration: BoxDecoration(
          color: _primaryColor,
          borderRadius: BorderRadius.circular(16),
          boxShadow: [
            BoxShadow(
              color: _primaryColor.withOpacity(0.4),
              blurRadius: 15,
              offset: const Offset(0, 8),
            ),
          ],
        ),
        alignment: Alignment.center,
        child: Text(
          text,
          style: const TextStyle(
            color: Colors.white,
            fontSize: 18,
            fontWeight: FontWeight.bold,
            letterSpacing: 1.0,
          ),
        ),
      ),
    );
  }
}