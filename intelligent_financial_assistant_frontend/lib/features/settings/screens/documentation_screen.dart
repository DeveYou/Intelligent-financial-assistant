import 'package:flutter/material.dart';
import 'package:intelligent_financial_assistant_frontend/features/settings/controllers/settings_controller.dart';
import 'package:intelligent_financial_assistant_frontend/localization/language_constraints.dart';
import 'package:provider/provider.dart';

class DocumentationScreen extends StatelessWidget {
  const DocumentationScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Theme.of(context).scaffoldBackgroundColor,
      appBar: AppBar(
        title: Text(getTranslated("help_and_documentation", context)!, style: Theme.of(context).textTheme.titleLarge),
        backgroundColor: Colors.transparent,
        elevation: 0,
        iconTheme: IconThemeData(color: Theme.of(context).textTheme.bodyLarge?.color),
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(20),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            _buildHeader(context),
            const SizedBox(height: 30),
            Text(
              getTranslated("vocal_assistant_commands", context)!,
              style: Theme.of(context).textTheme.titleMedium?.copyWith(
                fontWeight: FontWeight.bold,
                color: Theme.of(context).primaryColor,
              ),
            ),
            const SizedBox(height: 15),
            Consumer<SettingsController>(
              builder: (context, controller, _) {
                return ListView.builder(
                  shrinkWrap: true,
                  physics: const NeverScrollableScrollPhysics(),
                  itemCount: controller.assistantCommands.length,
                  itemBuilder: (context, index) {
                    final item = controller.assistantCommands[index];
                    return _buildCommandCategory(context, item);
                  },
                );
              },
            ),
            const SizedBox(height: 30),
            _buildInfoCard(
                context,
                getTranslated("general_usage", context)!,
                getTranslated("general_usage_description", context)!,
                Icons.touch_app
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildHeader(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(20),
      decoration: BoxDecoration(
        gradient: LinearGradient(
          colors: [Theme.of(context).primaryColor, Theme.of(context).primaryColor.withOpacity(0.7)],
          begin: Alignment.topLeft,
          end: Alignment.bottomRight,
        ),
        borderRadius: BorderRadius.circular(20),
      ),
      child: Row(
        children: [
          Container(
            padding: const EdgeInsets.all(12),
            decoration: BoxDecoration(
              color: Colors.white.withOpacity(0.2),
              shape: BoxShape.circle,
            ),
            child: const Icon(Icons.mic, color: Colors.white, size: 30),
          ),
          const SizedBox(width: 20),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  getTranslated("voice_assistant", context)!,
                  style: TextStyle(color: Colors.white, fontSize: 18, fontWeight: FontWeight.bold),
                ),
                const SizedBox(height: 5),
                Text(
                  getTranslated("voice_assistant_description", context)!,
                  style: TextStyle(color: Colors.white.withOpacity(0.9), fontSize: 14),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildCommandCategory(BuildContext context, Map<String, dynamic> data) {
    return Container(
      margin: const EdgeInsets.only(bottom: 20),
      decoration: BoxDecoration(
        color: Theme.of(context).cardColor,
        borderRadius: BorderRadius.circular(15),
        border: Border.all(color: Theme.of(context).dividerColor.withOpacity(0.5)),
      ),
      child: Column(
        children: [
          ListTile(
            leading: Icon(data['icon'], color: Theme.of(context).primaryColor),
            title: Text(data['category'], style: const TextStyle(fontWeight: FontWeight.w600)),
          ),
          Padding(
            padding: const EdgeInsets.fromLTRB(20, 0, 20, 20),
            child: Column(
              children: (data['commands'] as List).map<Widget>((command) {
                return Container(
                  margin: const EdgeInsets.only(bottom: 8),
                  padding: const EdgeInsets.symmetric(vertical: 10, horizontal: 15),
                  decoration: BoxDecoration(
                    color: Theme.of(context).scaffoldBackgroundColor,
                    borderRadius: BorderRadius.circular(10),
                  ),
                  child: Row(
                    children: [
                      Icon(Icons.format_quote, size: 16, color: Theme.of(context).hintColor),
                      const SizedBox(width: 10),
                      Expanded(
                        child: Text(
                          command,
                          style: TextStyle(
                            fontStyle: FontStyle.italic,
                            color: Theme.of(context).textTheme.bodyMedium?.color,
                          ),
                        ),
                      ),
                    ],
                  ),
                );
              }).toList(),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildInfoCard(BuildContext context, String title, String content, IconData icon) {
    return Container(
      padding: const EdgeInsets.all(20),
      decoration: BoxDecoration(
        color: Theme.of(context).cardColor,
        borderRadius: BorderRadius.circular(15),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              Icon(icon, size: 20, color: Theme.of(context).hintColor),
              const SizedBox(width: 10),
              Text(title, style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 16)),
            ],
          ),
          const SizedBox(height: 10),
          Text(content, style: TextStyle(color: Theme.of(context).hintColor)),
        ],
      ),
    );
  }
}