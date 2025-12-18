import 'package:flutter/material.dart';
import 'package:intelligent_financial_assistant_frontend/features/home/controllers/home_controller.dart';
import 'package:intelligent_financial_assistant_frontend/features/home/widgets/bank_card_widget.dart';
import 'package:intelligent_financial_assistant_frontend/features/home/widgets/quick_access_buttons_widget.dart';
import 'package:intelligent_financial_assistant_frontend/features/notifications/screens/notifications_screen.dart';
import 'package:intelligent_financial_assistant_frontend/features/settings/screens/settings_screen.dart';
import 'package:intelligent_financial_assistant_frontend/localization/language_constraints.dart';
import 'package:provider/provider.dart';
import 'package:intelligent_financial_assistant_frontend/common/basewidgets/shimmer_skeleton.dart';

/// Home screen displaying account summary and quick actions.
///
/// Shows the user's bank card with balance, account holder name, and IBAN.
/// Includes quick access buttons for common actions and displays loading/error states.
class HomeScreen extends StatefulWidget {
  /// Creates a [HomeScreen].
  const HomeScreen({super.key});

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      Provider.of<HomeController>(context, listen: false).getAccountSummary();
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Theme.of(context).scaffoldBackgroundColor,
      appBar: AppBar(
        title: Text(
            getTranslated("VoxBank_home", context)!,
            style: const TextStyle(
                color: Colors.white
            )
        ),
        backgroundColor: Theme.of(context).primaryColor,
        elevation: 0,
        actions: [
          IconButton(
            icon: const Icon(Icons.notifications_none),
            onPressed: () {
              Navigator.of(context).push(
                  MaterialPageRoute(
                      builder: (context) => const NotificationsScreen()
                  )
              );
            },
          ),
          IconButton(
            icon: const Icon(Icons.settings),
            onPressed: () {
              Navigator.of(context).push(
                  MaterialPageRoute(
                      builder: (context) => const SettingsScreen()
                  )
              );
            },
          ),
        ],
      ),
      body: Consumer<HomeController>(
        builder: (context, controller, child) {
          if (controller.homeState == HomeState.loading) {
             return SingleChildScrollView(
              padding: const EdgeInsets.all(20.0),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                   const ShimmerSkeleton.rectangular(height: 20, width: 150),
                   const SizedBox(height: 20),
                   const ShimmerSkeleton.rectangular(height: 200, width: double.infinity),
                   const SizedBox(height: 30),
                   Row(
                     mainAxisAlignment: MainAxisAlignment.spaceBetween,
                     children: List.generate(4, (index) => const ShimmerSkeleton.circular(height: 60, width: 60)),
                   ),
                   const SizedBox(height: 20),
                   const ShimmerSkeleton.rectangular(height: 100, width: double.infinity),
                ],
              ),
            );
          }

          if (controller.homeState == HomeState.error) {
            return Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Text(controller.errorMessage.isEmpty ? getTranslated("error", context)! : controller.errorMessage, style: const TextStyle(color: Colors.red), textAlign: TextAlign.center),
                  const SizedBox(height: 10),
                  ElevatedButton(
                    onPressed: () => controller.getAccountSummary(),
                    child: Text(getTranslated("retry", context)!),
                  )
                ],
              ),
            );
          }

           if (controller.accountDetails == null) {
            return Center(child: Text(
                getTranslated("no_account_data_available", context)!)
            );
          }


          return RefreshIndicator(
            onRefresh: () async => await controller.getAccountSummary(),
            child: SingleChildScrollView(
              padding: const EdgeInsets.all(20.0),
              physics: const AlwaysScrollableScrollPhysics(),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    getTranslated("welcome_back", context)!,
                    style: const TextStyle(
                      fontSize: 24,
                      fontWeight: FontWeight.bold,
                      color: Colors.black87,
                    ),
                  ),
                  const SizedBox(height: 20),
                  // Bank Card Widget
                  BankCardWidget(homeModel: controller.accountDetails!),

                  const SizedBox(height: 30),

                  // Quick Access Buttons
                  const QuickAccessButtonsWidget(),

                  // Placeholder for recent transactions
                  const SizedBox(height: 20),
                  Container(
                    width: double.infinity,
                    padding: const EdgeInsets.all(16),
                    decoration: BoxDecoration(
                      color: Colors.grey[50],
                      borderRadius: BorderRadius.circular(12),
                    ),
                    child: Center(
                      child: Text(
                        getTranslated("latest_offers_and_news", context)!,
                        style: TextStyle(color: Colors.grey[600]),
                      ),
                    ),
                  ),
                ],
              ),
            ),
          );
        },
      ),
    );
  }

}
