import 'package:flutter/material.dart';
import 'package:intelligent_financial_assistant_frontend/features/authentication/screens/authentication_screen.dart';
import 'package:intelligent_financial_assistant_frontend/features/home/screens/home_screen.dart';
import 'package:intelligent_financial_assistant_frontend/features/onboarding/screens/onboarding_screen.dart';
import 'package:intelligent_financial_assistant_frontend/helpers/app_storage_service.dart';

class SplashScreen extends StatefulWidget {
  const SplashScreen({Key? key}) : super(key: key);

  @override
  State<SplashScreen> createState() => _SplashScreenState();
}

class _SplashScreenState extends State<SplashScreen> with SingleTickerProviderStateMixin {
  final Color _primaryColor = const Color(0xFFF44E1E);
  final Color _secondaryColor = const Color(0xFFFFBD09);

  late AnimationController _controller;
  late Animation<double> _scaleAnimation;
  late Animation<double> _opacityAnimation;

  @override
  void initState() {
    super.initState();

    // Animation setup for a modern feel
    _controller = AnimationController(
      vsync: this,
      duration: const Duration(seconds: 2),
    );

    _scaleAnimation = Tween<double>(begin: 0.8, end: 1.0).animate(
      CurvedAnimation(parent: _controller, curve: Curves.easeOutBack),
    );

    _opacityAnimation = Tween<double>(begin: 0.0, end: 1.0).animate(
      CurvedAnimation(parent: _controller, curve: Curves.easeIn),
    );

    _controller.forward();

    // Execute logic after animation/delay
    _checkNavigationLogic();
  }

  Future<void> _checkNavigationLogic() async {
    // 1. Wait for animation + minimum splash time
    await Future.delayed(const Duration(seconds: 3));

    // 2. Check Logic
    final bool isLoggedIn = await AppStorageService.isLoggedIn();
    final bool isFirstTime = await AppStorageService.isFirstTime();

    if (!mounted) return;

    if (isLoggedIn) {
      // Logic A: User already authenticated -> Go directly to home
      Navigator.pushReplacement(
        context,
        MaterialPageRoute(builder: (context) => const HomeScreen()),
      );
    } else {
      if (isFirstTime) {
        // Logic B: Not auth + First time -> Go to Onboarding
        // Note: The Onboarding screen should navigate to AuthenticationScreen when done
        Navigator.pushReplacement(
          context,
          MaterialPageRoute(builder: (context) => const OnboardingScreen()),
        );
      } else {
        // Logic C: Not auth + Not first time -> Go to Authentication
        Navigator.pushReplacement(
          context,
          MaterialPageRoute(builder: (context) => const AuthenticationScreen()),
        );
      }
    }
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.white,
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            // Animated Logo
            AnimatedBuilder(
              animation: _controller,
              builder: (context, child) {
                return Transform.scale(
                  scale: _scaleAnimation.value,
                  child: Opacity(
                    opacity: _opacityAnimation.value,
                    child: Container(
                      height: 120,
                      width: 120,
                      decoration: BoxDecoration(
                        color: Theme.of(context).colorScheme.secondary.withOpacity(0.15),
                        shape: BoxShape.circle,
                      ),
                      child: Icon(
                        Icons.account_balance_wallet_rounded,
                        size: 60,
                        color: Theme.of(context).primaryColor,
                      ),
                    ),
                  ),
                );
              },
            ),
            const SizedBox(height: 24),

            // App Name
            FadeTransition(
              opacity: _opacityAnimation,
              child: const Text(
                "VoxBank",
                style: TextStyle(
                  fontSize: 28,
                  fontWeight: FontWeight.bold,
                  color: Colors.black87,
                  letterSpacing: 1.2,
                ),
              ),
            ),

            const SizedBox(height: 48),

            // Loading Indicator
            SizedBox(
              width: 40,
              height: 40,
              child: CircularProgressIndicator(
                color: Theme.of(context).primaryColor,
                strokeWidth: 3,
              ),
            ),
          ],
        ),
      ),
    );
  }
}