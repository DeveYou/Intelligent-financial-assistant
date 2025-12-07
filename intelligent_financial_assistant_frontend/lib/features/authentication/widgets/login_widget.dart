import 'package:flutter/material.dart';
import 'package:intelligent_financial_assistant_frontend/common/basewidgets/show_custom_snackbar_widget.dart';
import 'package:intelligent_financial_assistant_frontend/features/authentication/controllers/authentication_controller.dart';
import 'package:intelligent_financial_assistant_frontend/features/authentication/domains/models/login_model.dart';
import 'package:intelligent_financial_assistant_frontend/features/authentication/widgets/auth_text_input_field_widget.dart';
import 'package:intelligent_financial_assistant_frontend/features/authentication/widgets/auth_password_input_field_widget.dart';
import 'package:intelligent_financial_assistant_frontend/features/authentication/widgets/auth_submit_btn_widget.dart';
import 'package:intelligent_financial_assistant_frontend/features/dashboard/screens/dashboard_screen.dart';
import 'package:intelligent_financial_assistant_frontend/features/profile/controllers/profile_controller.dart';
import 'package:intelligent_financial_assistant_frontend/features/splash/controllers/splash_controller.dart';
import 'package:intelligent_financial_assistant_frontend/localization/language_constraints.dart';
import 'package:provider/provider.dart';

class LoginWidget extends StatefulWidget {
  const LoginWidget({super.key});

  @override
  State<LoginWidget> createState() => _LoginWidgetState();
}

class _LoginWidgetState extends State<LoginWidget> {
  final TextEditingController _emailController = TextEditingController();
  final TextEditingController _passwordController = TextEditingController();
  final GlobalKey<FormState> _formKey = GlobalKey<FormState>();


  @override
  void dispose() {
    _emailController.dispose();
    _passwordController.dispose();
    super.dispose();
  }

  LoginModel loginBody = LoginModel();

  void loginUser() async {
    if (_formKey.currentState!.validate()) {
      _formKey.currentState!.save();

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

      loginBody.email = email;
      loginBody.password = password;

      if (email.isEmpty) {
        showCustomSnackBarWidget(getTranslated('user_name_is_required', context), context);
      } else if (password.isEmpty) {
        showCustomSnackBarWidget(getTranslated('password_is_required', context), context);
      } else if (password.length < 8) {
        showCustomSnackBarWidget(getTranslated('minimum_password_length', context), context);
      } else {
        await authController.signIn(loginBody, route);
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
      // Login failed
      showCustomSnackBarWidget(errorMessage, context);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Form(
      key: _formKey,
      child: Column(
        children: [
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

          // Forgot Password
          Align(
            alignment: Alignment.centerRight,
            child: TextButton(
              onPressed: () {
                // Navigate to forgot password
              },
              child: Text(
                getTranslated("forgot_password", context)!,
                style: TextStyle(
                  color: Theme.of(context).primaryColor,
                  fontWeight: FontWeight.w600,
                ),
              ),
            ),
          ),
          const SizedBox(height: 24),

          // Submit Button
          AuthSubmitBtnWidget(
            text: getTranslated("sign_in", context)!,
            onTap: () {
              if (_formKey.currentState!.validate()) {
                // Call controller login logic here
                loginUser();
              }
            },
          ),

          const SizedBox(height: 20),

          // Social Login Divider (Optional aesthetic addition)
          Row(
            children: [
              Expanded(child: Divider(color: Colors.grey[300])),
              Padding(
                padding: const EdgeInsets.symmetric(horizontal: 16),
                child: Text(
                  getTranslated("or", context)!,
                  style: TextStyle(color: Colors.grey[500], fontSize: 12),
                ),
              ),
              Expanded(child: Divider(color: Colors.grey[300])),
            ],
          ),
        ],
      ),
    );
  }
}