import 'package:flutter/material.dart';
import 'package:intelligent_financial_assistant_frontend/features/authentication/widgets/auth_text_input_field_widget.dart';
import 'package:intelligent_financial_assistant_frontend/features/authentication/widgets/auth_password_input_field_widget.dart';
import 'package:intelligent_financial_assistant_frontend/features/authentication/widgets/auth_submit_btn_widget.dart';
import 'package:intelligent_financial_assistant_frontend/localization/language_constraints.dart';

class RegisterWidget extends StatefulWidget {
  const RegisterWidget({super.key});

  @override
  State<RegisterWidget> createState() => _RegisterWidgetState();
}

class _RegisterWidgetState extends State<RegisterWidget> {
  final TextEditingController _nameController = TextEditingController();
  final TextEditingController _emailController = TextEditingController();
  final TextEditingController _passwordController = TextEditingController();
  final TextEditingController _confirmPasswordController = TextEditingController();
  final GlobalKey<FormState> _formKey = GlobalKey<FormState>();

  @override
  void dispose() {
    _nameController.dispose();
    _emailController.dispose();
    _passwordController.dispose();
    _confirmPasswordController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Form(
      key: _formKey,
      child: Column(
        children: [
          // Name Input
          AuthTextInputFieldWidget(
            controller: _nameController,
            hintText: getTranslated("full_name", context)!,
            prefixIcon: Icons.person_outline,
            textCapitalization: TextCapitalization.words,
          ),
          const SizedBox(height: 16),

          // Email Input
          AuthTextInputFieldWidget(
            controller: _emailController,
            hintText: getTranslated("email_address", context)!,
            prefixIcon: Icons.email_outlined,
            keyboardType: TextInputType.emailAddress,
          ),
          const SizedBox(height: 16),

          // Password Input
          AuthPasswordInputFieldWidget(
            controller: _passwordController,
            hintText: getTranslated("password", context)!,
          ),
          const SizedBox(height: 16),

          // Confirm Password Input
          AuthPasswordInputFieldWidget(
            controller: _confirmPasswordController,
            hintText: getTranslated("confirm_password", context)!,
          ),

          const SizedBox(height: 32),

          // Submit Button
          AuthSubmitBtnWidget(
            text: getTranslated("create_account", context)!,
            onTap: () {
              if (_formKey.currentState!.validate()) {
                // Call controller signup logic here
                print("Register Pressed");
              }
            },
          ),

          const SizedBox(height: 20),

          Text(
            getTranslated("by_signing_up_you_agree_to_our_terms_and_conditions", context)!,
            textAlign: TextAlign.center,
            style: TextStyle(
              color: Colors.grey[500],
              fontSize: 12,
            ),
          ),
        ],
      ),
    );
  }
}