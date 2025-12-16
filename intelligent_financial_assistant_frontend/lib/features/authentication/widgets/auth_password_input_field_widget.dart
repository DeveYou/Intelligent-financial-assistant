import 'package:flutter/material.dart';
import 'package:intelligent_financial_assistant_frontend/localization/language_constraints.dart';

class AuthPasswordInputFieldWidget extends StatefulWidget {
  final TextEditingController controller;
  final String hintText;

  const AuthPasswordInputFieldWidget({
    super.key,
    required this.controller,
    required this.hintText,
  });

  @override
  State<AuthPasswordInputFieldWidget> createState() => _AuthPasswordInputFieldWidgetState();
}

class _AuthPasswordInputFieldWidgetState extends State<AuthPasswordInputFieldWidget> {
  bool _isObscured = true;
  final Color _secondaryColor = const Color(0xFFFFBD09);

  @override
  Widget build(BuildContext context) {
    return Container(
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(16),
        boxShadow: [
          BoxShadow(
            color: Colors.grey.withOpacity(0.08),
            blurRadius: 15,
            offset: const Offset(0, 5),
          ),
        ],
      ),
      child: TextFormField(
        controller: widget.controller,
        obscureText: _isObscured,
        style: const TextStyle(fontSize: 16),
        decoration: InputDecoration(
          hintText: widget.hintText,
          hintStyle: TextStyle(color: Colors.grey[400], fontSize: 15),
          prefixIcon: Icon(Icons.lock_outline, color: _secondaryColor),
          suffixIcon: IconButton(
            icon: Icon(
              _isObscured ? Icons.visibility_outlined : Icons.visibility_off_outlined,
              color: Colors.grey[400],
            ),
            onPressed: () {
              setState(() {
                _isObscured = !_isObscured;
              });
            },
          ),
          contentPadding: const EdgeInsets.symmetric(vertical: 18, horizontal: 20),
          enabledBorder: OutlineInputBorder(
            borderRadius: BorderRadius.circular(16),
            borderSide: BorderSide(color: Colors.grey[200]!),
          ),
          focusedBorder: OutlineInputBorder(
            borderRadius: BorderRadius.circular(16),
            borderSide: BorderSide(color: Theme.of(context).primaryColor, width: 1.5),
          ),
          errorBorder: OutlineInputBorder(
            borderRadius: BorderRadius.circular(16),
            borderSide: const BorderSide(color: Colors.red, width: 1),
          ),
          focusedErrorBorder: OutlineInputBorder(
            borderRadius: BorderRadius.circular(16),
            borderSide: const BorderSide(color: Colors.red, width: 1.5),
          ),
          filled: true,
          fillColor: Colors.white,
        ),
        validator: (value) {
          if (value == null || value.isEmpty) {
            return getTranslated('password_is_required', context)!;
          }
          if (value.length < 6) {
            return getTranslated('password_must_be_at_least_6_characters', context)!;
          }
          return null;
        },
      ),
    );
  }
}