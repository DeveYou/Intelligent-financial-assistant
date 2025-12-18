import 'package:flutter/material.dart';

import 'package:intelligent_financial_assistant_frontend/features/authentication/widgets/login_widget.dart';
import 'package:intelligent_financial_assistant_frontend/features/authentication/widgets/register_widget.dart';
import 'package:intelligent_financial_assistant_frontend/localization/language_constraints.dart';

/// Authentication screen with login and registration functionality.
///
/// Displays a tabbed interface allowing users to switch between login and signup forms.
/// Includes a custom animated tab selector and smooth transitions between the two states.
class AuthenticationScreen extends StatefulWidget {
  /// Creates an [AuthenticationScreen].
  const AuthenticationScreen({super.key});

  @override
  State<AuthenticationScreen> createState() => _AuthenticationScreenState();
}

class _AuthenticationScreenState extends State<AuthenticationScreen> {
  // State to toggle between Login (0) and Signup (1)
  int _pageState = 0;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.white,
      body: SafeArea(
        child: SingleChildScrollView(
          child: Padding(
            padding: const EdgeInsets.symmetric(horizontal: 24.0, vertical: 20.0),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.center,
              children: [
                const SizedBox(height: 40),
                // Placeholder for Logo
                Container(
                  height: 80,
                  width: 80,
                  decoration: BoxDecoration(
                    color: Theme.of(context).colorScheme.secondary.withValues(alpha: 0.2),
                    shape: BoxShape.circle,
                  ),
                  child: Icon(
                    Icons.account_balance_wallet_rounded,
                    size: 40,
                    color: Theme.of(context).primaryColor,
                  ),
                ),
                const SizedBox(height: 24),

                // Header Text
                Text(
                  _pageState == 0 ? getTranslated("welcome_back", context)! : getTranslated("create_account", context)!,
                  style: const TextStyle(
                    fontSize: 28,
                    fontWeight: FontWeight.bold,
                    color: Colors.black87,
                  ),
                ),
                const SizedBox(height: 8),
                Text(
                  _pageState == 0
                      ? getTranslated("please_sign_in_to_your_account", context)!
                      : getTranslated("start_your_financial_journey_today", context)!,
                  style: TextStyle(
                    fontSize: 16,
                    color: Colors.grey[600],
                  ),
                ),
                const SizedBox(height: 32),

                // Custom Tab/Toggle
                Container(
                  height: 55,
                  decoration: BoxDecoration(
                    color: Colors.grey[100],
                    borderRadius: BorderRadius.circular(25),
                  ),
                  child: Row(
                    children: [
                      Expanded(
                        child: GestureDetector(
                          onTap: () {
                            setState(() {
                              _pageState = 0;
                            });
                          },
                          child: Container(
                            decoration: BoxDecoration(
                              color: _pageState == 0 ? Theme.of(context).primaryColor : Colors.transparent,
                              borderRadius: BorderRadius.circular(25),
                              boxShadow: _pageState == 0
                                  ? [BoxShadow(color: Theme.of(context).primaryColor.withValues(alpha: 0.3), blurRadius: 10, offset: const Offset(0, 4))]
                                  : [],
                            ),
                            alignment: Alignment.center,
                            child: Text(
                              getTranslated("sign_in", context)!,
                              style: TextStyle(
                                color: _pageState == 0 ? Colors.white : Colors.grey[600],
                                fontWeight: FontWeight.bold,
                                fontSize: 16,
                              ),
                            ),
                          ),
                        ),
                      ),
                      Expanded(
                        child: GestureDetector(
                          onTap: () {
                            setState(() {
                              _pageState = 1;
                            });
                          },
                          child: Container(
                            decoration: BoxDecoration(
                              color: _pageState == 1 ? Theme.of(context).primaryColor : Colors.transparent,
                              borderRadius: BorderRadius.circular(25),
                              boxShadow: _pageState == 1
                                  ? [BoxShadow(color: Theme.of(context).primaryColor.withValues(alpha: 0.3), blurRadius: 10, offset: const Offset(0, 4))]
                                  : [],
                            ),
                            alignment: Alignment.center,
                            child: Text(
                              getTranslated("sign_up", context)!,
                              style: TextStyle(
                                color: _pageState == 1 ? Colors.white : Colors.grey[600],
                                fontWeight: FontWeight.bold,
                                fontSize: 16,
                              ),
                            ),
                          ),
                        ),
                      ),
                    ],
                  ),
                ),

                const SizedBox(height: 32),

                // Animated Switcher for Smooth Transition
                AnimatedSwitcher(
                  duration: const Duration(milliseconds: 300),
                  child: _pageState == 0
                      ? const LoginWidget()
                      : const RegisterWidget(),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}