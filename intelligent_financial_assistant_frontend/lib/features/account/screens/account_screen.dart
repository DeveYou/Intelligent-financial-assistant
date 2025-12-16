import 'package:flutter/material.dart';
import 'package:intelligent_financial_assistant_frontend/features/account/controllers/account_controller.dart';
import 'package:intelligent_financial_assistant_frontend/features/account/widgets/transaction_card_widget.dart';
import 'package:intelligent_financial_assistant_frontend/features/notifications/screens/notifications_screen.dart';
import 'package:intelligent_financial_assistant_frontend/features/settings/screens/settings_screen.dart';
import 'package:intelligent_financial_assistant_frontend/localization/language_constraints.dart';
import 'package:provider/provider.dart';

import 'package:intelligent_financial_assistant_frontend/common/basewidgets/shimmer_skeleton.dart';

class AccountScreen extends StatefulWidget {
  const AccountScreen({super.key});

  @override
  State<AccountScreen> createState() => _AccountScreenState();
}

class _AccountScreenState extends State<AccountScreen> {
  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      Provider.of<AccountController>(context, listen: false).initAccountData();
    });
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);

    return Scaffold(
      appBar: AppBar(
          title: Text(
              getTranslated("my_account", context)!,
              style: TextStyle(
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
      body: Consumer<AccountController>(
        builder: (context, controller, child) {
          if (controller.accountState == AccountState.loading) {
             return SingleChildScrollView(
              padding: const EdgeInsets.all(20),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                   const ShimmerSkeleton.rectangular(height: 200, width: double.infinity),
                   const SizedBox(height: 24),
                   const ShimmerSkeleton.rectangular(height: 60, width: double.infinity),
                   const SizedBox(height: 10),
                   const ShimmerSkeleton.rectangular(height: 60, width: double.infinity),
                   const SizedBox(height: 10),
                   const ShimmerSkeleton.rectangular(height: 60, width: double.infinity),
                ],
              ),
            );
          }

          if (controller.accountState == AccountState.error) {
             return Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                   Text(controller.error.isNotEmpty ? controller.error : getTranslated("error", context)!, style: const TextStyle(color: Colors.red)),
                   const SizedBox(height: 10),
                   ElevatedButton(
                     onPressed: () => controller.initAccountData(),
                     child: Text(getTranslated("retry", context)!),
                   )
                ],
              ),
             );
          }

          if (controller.accountModel == null) {
            return Center(child: Text(getTranslated("unable_to_load_account_details", context)!));
          }

          final account = controller.accountModel!;

          return SingleChildScrollView(
            padding: const EdgeInsets.all(20),
            physics: const BouncingScrollPhysics(),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                // Account Details Header
                _buildAccountHeader(account, theme),
                const SizedBox(height: 24),

                // Global Activation Switch
                _buildSwitchTile(
                  title: getTranslated("activate_account", context)!,
                  subtitle: getTranslated("activate_account_subtitle", context),
                  value: account.isActive!,
                  onChanged: (val) => controller.toggleSetting('activate_account', val, context),
                  activeColor: Colors.green,
                ),
                const Divider(height: 30),

                // Card Settings
                Text(
                  getTranslated("card_settings", context)!,
                  style: theme.textTheme.titleMedium?.copyWith(fontWeight: FontWeight.bold),
                ),
                const SizedBox(height: 10),
                _buildSwitchTile(
                  title: getTranslated("payment_by_card", context)!,
                  value: account.isPaymentByCard!,
                  onChanged: (val) => controller.toggleSetting('card_payment', val, context),
                ),
                _buildSwitchTile(
                  title: getTranslated("withdrawal", context)!,
                  value: account.isWithdrawal!,
                  onChanged: (val) => controller.toggleSetting('withdrawal', val, context),
                ),
                _buildSwitchTile(
                  title: getTranslated("online_payment", context)!,
                  value: account.isOnlinePayment!,
                  onChanged: (val) => controller.toggleSetting('online_payment', val, context),
                ),
                _buildSwitchTile(
                  title: getTranslated("contactless_payment", context)!,
                  value: account.isContactless!,
                  onChanged: (val) => controller.toggleSetting('contactless', val, context),
                ),

                const SizedBox(height: 20),

                // Resend Code Button
                SizedBox(
                  width: double.infinity,
                  child: OutlinedButton.icon(
                    onPressed: () => controller.resendBankCardCode(context),
                    icon: const Icon(Icons.lock_reset),
                    label: Text(getTranslated("resend_bank_card_code", context)!),
                    style: OutlinedButton.styleFrom(
                      padding: const EdgeInsets.symmetric(vertical: 14),
                      side: BorderSide(color: theme.primaryColor),
                      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
                    ),
                  ),
                ),

                const SizedBox(height: 30),

                // Recent Transactions
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Text(
                      getTranslated("last_10_transactions", context)!,
                      style: theme.textTheme.titleMedium?.copyWith(fontWeight: FontWeight.bold),
                    ),
                    TextButton(
                      onPressed: () {},
                      child: Text(getTranslated("see_all", context)!),
                    )
                  ],
                ),
                const SizedBox(height: 10),

                if (controller.recentTransactions == null || controller.recentTransactions!.isEmpty)
                  Center(
                    child: Padding(
                      padding: const EdgeInsets.all(20),
                      child: Text(getTranslated("no_recent_transactions", context)!),
                    ),
                  )
                else
                  ListView.builder(
                    shrinkWrap: true,
                    physics: const NeverScrollableScrollPhysics(),
                    itemCount: controller.recentTransactions!.length,
                    itemBuilder: (context, index) {
                      return TransactionCardWidget(transaction: controller.recentTransactions![index]);
                    },
                  ),
              ],
            ),
          );
        },
      ),
    );
  }

  Widget _buildAccountHeader(var account, ThemeData theme) {
    return Container(
      padding: const EdgeInsets.all(20),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(16),
        boxShadow: [
          BoxShadow(color: Colors.grey.withOpacity(0.1), blurRadius: 10, offset: const Offset(0, 5))
        ],
        border: Border.all(color: Colors.grey.withOpacity(0.1)),
      ),
      child: Column(
        children: [
          Text(getTranslated("current_balance", context)!, style: TextStyle(color: Colors.grey[600], fontSize: 14)),
          const SizedBox(height: 5),
          Text(
            "${account.currency} ${account.balance.toStringAsFixed(2)}",
            style: TextStyle(color: theme.primaryColor, fontSize: 32, fontWeight: FontWeight.bold),
          ),
          const SizedBox(height: 15),
          Divider(color: Colors.grey[200]),
          const SizedBox(height: 15),
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(getTranslated("account_holder", context)!, style: const TextStyle(fontSize: 12, color: Colors.grey)),
                  Text(account.accountHolder, style: const TextStyle(fontWeight: FontWeight.w600)),
                ],
              ),
              Column(
                crossAxisAlignment: CrossAxisAlignment.end,
                children: [
                  Text(getTranslated("account_number", context)!, style: const TextStyle(fontSize: 12, color: Colors.grey)),
                  Text(account.accountNumber, style: const TextStyle(fontWeight: FontWeight.w600)),
                ],
              ),
            ],
          )
        ],
      ),
    );
  }

  Widget _buildSwitchTile({
    required String title,
    String? subtitle,
    required bool value,
    required Function(bool) onChanged,
    Color? activeColor,
  }) {
    return SwitchListTile(
      title: Text(title, style: const TextStyle(fontWeight: FontWeight.w500)),
      subtitle: subtitle != null ? Text(subtitle, style: const TextStyle(fontSize: 12)) : null,
      value: value,
      onChanged: onChanged,
      activeColor: activeColor ?? Theme.of(context).primaryColor,
      contentPadding: EdgeInsets.zero,
    );
  }
}
