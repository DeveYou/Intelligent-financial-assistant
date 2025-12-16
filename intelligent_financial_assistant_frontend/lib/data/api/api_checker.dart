import 'dart:developer';

import 'package:intelligent_financial_assistant_frontend/common/basewidgets/show_custom_snackbar_widget.dart';
import 'package:intelligent_financial_assistant_frontend/data/response/api_response.dart';
import 'package:intelligent_financial_assistant_frontend/features/authentication/controllers/authentication_controller.dart';
import 'package:get/get.dart';
import 'package:intelligent_financial_assistant_frontend/features/authentication/screens/authentication_screen.dart';
import 'package:intelligent_financial_assistant_frontend/localization/language_constraints.dart';
import 'package:provider/provider.dart';


class ApiChecker {
  static void checkApi(ApiResponse apiResponse) {
    log('API Response Status Code: ${apiResponse.response?.statusCode}');
    log('API Response Data: ${apiResponse.response?.data}');
    log('API Error: ${apiResponse.error}');

    if(apiResponse.response?.statusCode == 401) {
      log('Authentication error: 401 Unauthorized');
      Provider.of<AuthenticationController>(Get.context!, listen: false).clearSharedData();
      Get.offAll(() => const AuthenticationScreen());
    } else if (apiResponse.response?.statusCode == 403) {
      log('Authorization error: 403 Forbidden');
      showCustomSnackBarWidget(
          getTranslated('you_do_not_have_permission_to_perform_this_action', Get.context!),
          Get.context!
      );
    }else if(apiResponse.response?.statusCode == 400){
      log('Bad Request error: 400');
      showCustomSnackBarWidget(
          getTranslated('bad_request', Get.context!),
          Get.context!
      );
    } else if(apiResponse.response?.statusCode == 500) {
      log('Server error: 500 Internal Server Error');
      showCustomSnackBarWidget(
          getTranslated('internal_server_error', Get.context!),
          Get.context!
      );
    } else {
      String errorMessage = apiResponse.error ?? 'An unexpected error occurred';
      if (Get.context != null) {
        errorMessage = apiResponse.error ?? getTranslated('an_unexpected_error_occurred_Please_try_again_later', Get.context!) ?? 'An unexpected error occurred';
        log('General API error: $errorMessage');
        showCustomSnackBarWidget(errorMessage, Get.context!);
      } else {
        log('General API error (No Context): $errorMessage');
      }
    }
  }
}