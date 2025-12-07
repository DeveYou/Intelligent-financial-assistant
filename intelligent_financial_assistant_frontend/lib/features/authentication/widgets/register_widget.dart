import 'package:flutter/material.dart';
import 'package:intelligent_financial_assistant_frontend/common/basewidgets/show_custom_snackbar_widget.dart';
import 'package:intelligent_financial_assistant_frontend/features/authentication/controllers/authentication_controller.dart';
import 'package:intelligent_financial_assistant_frontend/features/authentication/domains/models/register_model.dart';
import 'package:intelligent_financial_assistant_frontend/features/authentication/widgets/auth_text_input_field_widget.dart';
import 'package:intelligent_financial_assistant_frontend/features/authentication/widgets/auth_password_input_field_widget.dart';
import 'package:intelligent_financial_assistant_frontend/features/authentication/widgets/auth_submit_btn_widget.dart';
import 'package:intelligent_financial_assistant_frontend/features/dashboard/screens/dashboard_screen.dart';
import 'package:intelligent_financial_assistant_frontend/features/profile/controllers/profile_controller.dart';
import 'package:intelligent_financial_assistant_frontend/features/splash/controllers/splash_controller.dart';
import 'package:intelligent_financial_assistant_frontend/localization/language_constraints.dart';
import 'package:provider/provider.dart';

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

  RegisterModel registerBody = RegisterModel();

  void registerUser() async {
    if (_formKey.currentState!.validate()) {
      _formKey.currentState!.save();

      String firstName = _nameController.text.trim().split(" ")[0];
      String LastName = _nameController.text.trim().split(" ")[1];
      String email = _emailController.text.trim();
      String password = _passwordController.text.trim();

      // Get the AuthController instance once to keep code clean
      final authController = Provider.of<AuthenticationController>(context, listen: false);

      // Handle "Remember Me" logic
      if (authController.isAuthenticated) {
        authController.saveUserCredentials(email, password);
      } else {
        authController.clearUserEmailAndPassword();
      }

      registerBody.firstName = firstName;
      registerBody.lastName = LastName;
      registerBody.email = email;
      registerBody.password = password;

      if (email.isEmpty) {
        showCustomSnackBarWidget(getTranslated('user_name_is_required', context), context);
      } else if (password.isEmpty) {
        showCustomSnackBarWidget(getTranslated('password_is_required', context), context);
      } else if (password.length < 8) {
        showCustomSnackBarWidget(getTranslated('minimum_password_length', context), context);
      } else {
        await authController.registerUser(registerBody, route);
      }
    }
  }

  // The route callback handles the response from the API
  route(bool isRoute, String? token, String? temporaryToken, String? errorMessage) async {
    if (isRoute) {
      if (token == null || token.isEmpty) {
        final splashController = Provider.of<SplashController>(context, listen: false);

      }
      else {
        await Provider.of<ProfileController>(context, listen: false).getUserInfo(context);
        Navigator.pushAndRemoveUntil(
            context,
            MaterialPageRoute(builder: (_) => const DashboardScreen()),
                (route) => false
        );
      }
    } else {
      // Register failed
      showCustomSnackBarWidget(errorMessage, context);
    }
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
                registerUser();
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