import 'package:flutter/material.dart';
import 'package:intelligent_financial_assistant_frontend/features/home/controllers/home_controller.dart';
import 'package:intelligent_financial_assistant_frontend/features/home/widgets/bank_card_widget.dart';
import 'package:intelligent_financial_assistant_frontend/features/home/widgets/quick_access_buttons_widget.dart';
import 'package:intelligent_financial_assistant_frontend/localization/language_constraints.dart';
import 'package:provider/provider.dart';

class HomeScreen extends StatefulWidget {
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
        title: Text(getTranslated("VoxBank_home", context)!),
        centerTitle: true,
        actions: [
          IconButton(
            icon: const Icon(Icons.notifications_none),
            onPressed: () {
              // Navigator.pushNamed(context, '/notifications');
            },
          ),
        ],
      ),
      body: Consumer<HomeController>(
        builder: (context, controller, child) {
          if (controller.isLoading) {
            return const Center(child: CircularProgressIndicator());
          }

          if (controller.errorMessage.isNotEmpty) {
            return Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Text(controller.errorMessage, style: const TextStyle(color: Colors.red)),
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
                    style: TextStyle(
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