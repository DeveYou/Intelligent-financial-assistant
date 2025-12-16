import 'package:flutter/material.dart';
import 'package:intelligent_financial_assistant_frontend/data/response/api_response.dart';
import 'package:intelligent_financial_assistant_frontend/features/settings/domains/services/settings_service.dart';
import 'package:intelligent_financial_assistant_frontend/localization/language_constraints.dart';

import '../../../main.dart';


class SettingsController with ChangeNotifier {
  final SettingsService settingsService;

  SettingsController({required this.settingsService});

  bool _isExporting = false;
  bool get isExporting => _isExporting;

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