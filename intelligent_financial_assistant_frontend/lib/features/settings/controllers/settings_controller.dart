import 'package:flutter/material.dart';
import 'package:intelligent_financial_assistant_frontend/data/response/api_response.dart';
import 'package:intelligent_financial_assistant_frontend/features/settings/domains/services/settings_service.dart';
import 'package:intelligent_financial_assistant_frontend/localization/language_constraints.dart';

import '../../../main.dart';

/// Manages application settings and help documentation.
///
/// This controller handles settings-related operations including
/// exporting bank details and providing assistant command documentation.
class SettingsController with ChangeNotifier {
  /// The service for settings-related operations.
  final SettingsService settingsService;

  /// Creates a [SettingsController] with the required service.
  SettingsController({required this.settingsService});

  bool _isExporting = false;
  
  /// Returns true if a bank details export is in progress.
  bool get isExporting => _isExporting;

  /// List of available voice assistant commands grouped by category.
  ///
  /// Each entry contains a category name, icon, and list of example commands.
  final List<Map<String, dynamic>> assistantCommands = [
    {
      "category": getTranslated("balance_and_account", Get.context!)!,
      "icon": Icons.account_balance_wallet,
      "commands": [
        getTranslated("how_much_money_do_i_have", Get.context!)!,
        getTranslated("give_me_my_balance", Get.context!)!,
        getTranslated("what_is_my_current_balance", Get.context!)!,
      ]
    },
    {
      "category": "transactions",
      "icon": Icons.swap_horiz,
      "commands": [
        getTranslated("send_money_example", Get.context!)!,
        getTranslated("transfer_money_example", Get.context!)!,
        getTranslated("check_last_transaction", Get.context!)!,
      ]
    },
  ];

  /// Exports the user's bank account details to a PDF file.
  ///
  /// Shows a success or error snackbar based on the result.
  ///
  /// [context] is used to display snackbar messages.
  Future<void> exportBankDetails(BuildContext context) async {
    _isExporting = true;
    notifyListeners();

    ApiResponse? response = await settingsService.exportBankDetailsToPdf("CURRENT_USER_ID");

    _isExporting = false;
    notifyListeners();

    if (context.mounted) {
      if (response?.error == null) {
        ScaffoldMessenger.of(context).showSnackBar(
           SnackBar(content: Text(getTranslated("bank_details_exported_successfully", context)!)),
        );
      } else {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text(response!.error.toString()), backgroundColor: Colors.red),
        );
      }
    }
  }
}