import 'package:flutter/material.dart';
import 'package:intelligent_financial_assistant_frontend/features/authentication/controllers/authentication_controller.dart';
import 'package:intelligent_financial_assistant_frontend/features/authentication/screens/authentication_screen.dart';
import 'package:intelligent_financial_assistant_frontend/features/settings/controllers/settings_controller.dart';
import 'package:intelligent_financial_assistant_frontend/features/settings/screens/documentation_screen.dart';
import 'package:intelligent_financial_assistant_frontend/features/settings/widgets/settings_tile_widget.dart';
import 'package:intelligent_financial_assistant_frontend/localization/controllers/localization_controller.dart';
import 'package:intelligent_financial_assistant_frontend/localization/language_constraints.dart';
import 'package:intelligent_financial_assistant_frontend/theme/controllers/theme_controller.dart';
import 'package:provider/provider.dart';
import 'package:intelligent_financial_assistant_frontend/utils/app_constants.dart';

/// Settings screen for app preferences and account management.
///
/// Provides controls for dark mode, language selection, PDF export,
/// password changes, PIN reset, help documentation, and logout.
class SettingsScreen extends StatelessWidget {
  /// Creates a [SettingsScreen].
  const SettingsScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Theme.of(context).scaffoldBackgroundColor,
      appBar: AppBar(
        title: Text(getTranslated("settings", context)!, style: Theme.of(context).textTheme.titleLarge),
        backgroundColor: Colors.transparent,
        elevation: 0,
        iconTheme: IconThemeData(color: Theme.of(context).textTheme.bodyLarge?.color),
      ),
      body: SingleChildScrollView(
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const SizedBox(height: 20),

            _buildSectionHeader(context, getTranslated("preferences", context)!),
            Consumer<ThemeController>(
              builder: (context, themeController, _) {
                return SettingsTileWidget(
                  title: getTranslated("dark_mode", context)!,
                  icon: Icons.dark_mode_outlined,
                  trailing: Switch(
                    value: themeController.isDarkMode,
                    thumbColor: WidgetStateProperty.all(Theme.of(context).primaryColor),
                    inactiveTrackColor: Colors.grey[300],
                    onChanged: (val) {
                      themeController.toggleThemeMode();
                    },
                  ),
                );
              },
            ),
            Consumer<LocalizationController>(
              builder: (context, localController, _) {
                return SettingsTileWidget(
                  title: getTranslated("language", context)!,
                  icon: Icons.language,
                  trailing: Row(
                    mainAxisSize: MainAxisSize.min,
                    children: [
                      Text(
                        AppConstants.languages.firstWhere((l) => l.languageCode == localController.locale.languageCode).languageName!,
                        style: Theme.of(context).textTheme.bodyMedium?.copyWith(color: Theme.of(context).hintColor),
                      ),
                      const SizedBox(width: 8),
                      Icon(Icons.arrow_forward_ios, size: 16, color: Theme.of(context).hintColor),
                    ],
                  ),
                  onTap: () => _showLanguageBottomSheet(context),
                );
              },
            ),

            const Divider(height: 40),

            _buildSectionHeader(context, getTranslated("account_and_data", context)!),
            Consumer<SettingsController>(
              builder: (context, settingsController, _) {
                return SettingsTileWidget(
                  title: getTranslated("export_bank_details_pdf", context)!,
                  icon: Icons.picture_as_pdf_outlined,
                  trailing: settingsController.isExporting
                      ? SizedBox(width: 20, height: 20, child: CircularProgressIndicator(strokeWidth: 2, color: Theme.of(context).primaryColor))
                      : null,
                  onTap: () => settingsController.exportBankDetails(context),
                );
              },
            ),
            SettingsTileWidget(
              title: getTranslated("help_and_documentation", context)!,
              icon: Icons.help_outline,
              onTap: () {
                Navigator.push(
                  context,
                  MaterialPageRoute(builder: (context) => const DocumentationScreen()),
                );
              },
            ),

            const Divider(height: 40),

            _buildSectionHeader(context, getTranslated("security", context)!),
            SettingsTileWidget(
              title: getTranslated("change_password", context)!,
              icon: Icons.lock_outline,
              onTap: () {
                // Navigate to Change Password Screen (You would create this similarly to Auth screens)
                ScaffoldMessenger.of(context).showSnackBar( SnackBar(content: Text(getTranslated("navigate_to_change_password", context)!)));
              },
            ),
            SettingsTileWidget(
              title: getTranslated("reset_app_code", context)!,
              icon: Icons.pin_outlined,
              onTap: () {
                ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text(getTranslated("navigate_to_reset_pin", context)!)));
              },
            ),
            Consumer<AuthenticationController>(
              builder: (context, authController, _) {
                return SettingsTileWidget(
                  title: getTranslated("logout", context)!,
                  icon: Icons.logout,
                  isDestructive: true,
                  onTap: () => _showLogoutDialog(context, authController),
                );
              },
            ),

            const SizedBox(height: 50),
            Center(
              child: Text(
                getTranslated("app_version", context)!,
                style: Theme.of(context).textTheme.bodySmall?.copyWith(color: Theme.of(context).disabledColor),
              ),
            ),
            const SizedBox(height: 20),
          ],
        ),
      ),
    );
  }

  Widget _buildSectionHeader(BuildContext context, String title) {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 10),
      child: Text(
        title.toUpperCase(),
        style: Theme.of(context).textTheme.bodySmall?.copyWith(
          fontWeight: FontWeight.bold,
          color: Theme.of(context).hintColor,
          letterSpacing: 1.2,
        ),
      ),
    );
  }

  void _showLanguageBottomSheet(BuildContext context) {
    showModalBottomSheet(
      context: context,
      shape: const RoundedRectangleBorder(borderRadius: BorderRadius.vertical(top: Radius.circular(20))),
      builder: (context) {
        return Consumer<LocalizationController>(
          builder: (context, localizationController, child) {
            return Container(
              padding: const EdgeInsets.all(20),
              child: Column(
                mainAxisSize: MainAxisSize.min,
                children: [
                  Text(getTranslated("select_language", context)!, style: Theme.of(context).textTheme.titleLarge),
                  const SizedBox(height: 20),
                  ListView.builder(
                    shrinkWrap: true,
                    physics: const NeverScrollableScrollPhysics(),
                    itemCount: AppConstants.languages.length,
                    itemBuilder: (context, index) {
                      final language = AppConstants.languages[index];
                      bool isSelected = localizationController.locale.languageCode == language.languageCode;
                      
                      return ListTile(
                        title: Text(language.languageName!),
                        trailing: isSelected ? const Icon(Icons.check, color: Colors.green) : null,
                        onTap: () {
                          localizationController.setLanguage(Locale(language.languageCode!, language.countryCode));
                          Navigator.pop(context);
                        },
                      );
                    },
                  ),
                ],
              ),
            );
          }
        );
      },
    );
  }

  void _showLogoutDialog(BuildContext context, AuthenticationController authController) {
    showDialog(
      context: context,
      builder: (dialogContext) => AlertDialog(
        title: Text(getTranslated("logout", context)!),
        content: Text(getTranslated("confirm_logout", context)!),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(dialogContext),
            child: Text(getTranslated("cancel", context)!, style: TextStyle(color: Theme.of(context).hintColor)),
          ),
          TextButton(
            onPressed: () async {
              // Close the dialog using dialog context
              Navigator.pop(dialogContext);
              // Perform logout
              await authController.signOut();
              // Navigate to Login Screen using parent context (still valid)
              if (context.mounted) {
                Navigator.pushAndRemoveUntil(
                    context,
                    MaterialPageRoute(builder: (_) => const AuthenticationScreen()),
                    (route) => false
                );
              }
            },
            child: Text(getTranslated("logout", context)!, style: TextStyle(color: Theme.of(context).colorScheme.error)),
          ),
        ],
      ),
    );
  }
}