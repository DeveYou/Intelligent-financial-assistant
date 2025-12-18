import 'package:flutter/material.dart';
import 'package:intelligent_financial_assistant_frontend/features/authentication/screens/authentication_screen.dart';
import 'package:intelligent_financial_assistant_frontend/helpers/app_storage_service.dart';
import 'package:intelligent_financial_assistant_frontend/localization/language_constraints.dart';

/// Onboarding screen shown on first app launch.
///
/// Displays app introduction and navigates to authentication screen after completion.
class OnboardingScreen extends StatelessWidget {
  /// Creates an [OnboardingScreen].
  const OnboardingScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.white,
      body: SafeArea(
        child: Padding(
          padding: const EdgeInsets.all(24.0),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              const Spacer(),
              Icon(Icons.trending_up, size: 100, color: Theme.of(context).primaryColor),
              const SizedBox(height: 24),
              Text(
                getTranslated("smart_financial_tracking", context)!,
                style: TextStyle(fontSize: 24, fontWeight: FontWeight.bold),
              ),
              const SizedBox(height: 16),
              Text(
                getTranslated("manage_your_income_and_expenses_with_ease", context)!,
                textAlign: TextAlign.center,
                style: TextStyle(fontSize: 16, color: Colors.grey),
              ),
              const Spacer(),
              SizedBox(
                width: double.infinity,
                height: 56,
                child: ElevatedButton(
                  style: ElevatedButton.styleFrom(
                    backgroundColor: Theme.of(context).primaryColor,
                    shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
                  ),
                  onPressed: () async {
                    await AppStorageService.completeOnboarding();

                    if (context.mounted) {
                      Navigator.pushReplacement(
                        context,
                        MaterialPageRoute(builder: (_) => const AuthenticationScreen()),
                      );
                    }
                  },
                  child: Text(
                      getTranslated("get_started", context)!,
                      style: TextStyle(
                          color: Colors.white,
                          fontSize: 18)),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}