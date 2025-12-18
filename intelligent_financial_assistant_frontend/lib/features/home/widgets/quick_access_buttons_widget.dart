import 'package:flutter/material.dart';
import 'package:intelligent_financial_assistant_frontend/features/recipient/screens/recipient_screen.dart';
import 'package:intelligent_financial_assistant_frontend/localization/language_constraints.dart';
import 'package:intelligent_financial_assistant_frontend/features/root.dart';

class QuickAccessButtonsWidget extends StatelessWidget {
  const QuickAccessButtonsWidget({super.key});

  @override
  Widget build(BuildContext context) {
    final List<Map<String, dynamic>> features = [
      {'icon': Icons.account_balance_wallet, 'label': getTranslated('account', context)!, 'route': '/account'},
      {'icon': Icons.smart_toy, 'label': getTranslated('assistant', context)!, 'route': '/assistant'},
      {'icon': Icons.people, 'label': getTranslated('recipients', context)!, 'route': '/recipient'},
      {'icon': Icons.swap_horiz, 'label': getTranslated('transactions', context)!, 'route': '/transaction'},
    ];

    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 20.0),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            getTranslated("quick_access", context)!,
            style: Theme.of(context).textTheme.titleLarge?.copyWith(
              fontWeight: FontWeight.bold,
              color: Colors.black87,
            ),
          ),
          const SizedBox(height: 15),
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: features.map((feature) {
              return _buildFeatureButton(context, feature);
            }).toList(),
          ),
        ],
      ),
    );
  }

  Widget _buildFeatureButton(BuildContext context, Map<String, dynamic> feature) {
    return Column(
      children: [
        InkWell(
          onTap: () {
            if (feature['route'] == '/account') {
              Root.setPage(2);
            } else if (feature['route'] == '/assistant') {
              Root.setPage(3);
            } else if (feature['route'] == '/transaction') {
              Root.setPage(1);
            } else {
              Navigator.of(context).push(
                  MaterialPageRoute(
                      builder: (context) => const RecipientScreen()
                  )
              );
            }
          },
          borderRadius: BorderRadius.circular(16),
          child: Container(
            height: 60,
            width: 60,
            decoration: BoxDecoration(
              color: Colors.white,
              borderRadius: BorderRadius.circular(16),
              boxShadow: [
                BoxShadow(
                  color: Colors.grey.withValues(alpha: 0.1),
                  spreadRadius: 2,
                  blurRadius: 5,
                )
              ],
              border: Border.all(color: Theme.of(context).primaryColor.withValues(alpha: 0.1)),
            ),
            child: Icon(
              feature['icon'],
              color: Theme.of(context).primaryColor,
              size: 28,
            ),
          ),
        ),
        const SizedBox(height: 8),
        Text(
          feature['label'],
          style: const TextStyle(fontSize: 12, fontWeight: FontWeight.w500),
        ),
      ],
    );
  }
}