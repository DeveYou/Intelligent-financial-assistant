import 'package:flutter/material.dart';
import 'package:intelligent_financial_assistant_frontend/localization/language_constraints.dart';
import '../domains/models/home_model.dart';

class BankCardWidget extends StatelessWidget {
  final HomeModel homeModel;

  const BankCardWidget({super.key, required this.homeModel});

  @override
  Widget build(BuildContext context) {
    final primaryColor = Theme.of(context).primaryColor;
    final secondaryColor = Theme.of(context).colorScheme.secondary;

    return Container(
      width: double.infinity,
      padding: const EdgeInsets.all(24.0),
      decoration: BoxDecoration(
        color: primaryColor,
        borderRadius: BorderRadius.circular(20),
        boxShadow: [
          BoxShadow(
            color: primaryColor.withValues(alpha: 0.4),
            blurRadius: 10,
            offset: const Offset(0, 5),
          ),
        ],
        gradient: LinearGradient(
          begin: Alignment.topLeft,
          end: Alignment.bottomRight,
          colors: [
            primaryColor,
            Color.lerp(primaryColor, Colors.black, 0.2)!,
          ],
        ),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Text(
                homeModel.bankName,
                style: const TextStyle(
                  color: Colors.white,
                  fontWeight: FontWeight.bold,
                  fontSize: 16,
                ),
              ),
              const Icon(Icons.wifi, color: Colors.white70),
            ],
          ),
          const SizedBox(height: 30),
          Text(
            homeModel.accountNumber,
            style: const TextStyle(
              color: Colors.white,
              fontSize: 22,
              letterSpacing: 2.0,
              fontFamily: 'Courier',
              fontWeight: FontWeight.w600,
            ),
          ),
          const SizedBox(height: 30),
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    getTranslated("card_holder", context)!,
                    style: TextStyle(color: Colors.white.withValues(alpha: 0.8), fontSize: 10),
                  ),
                  Text(
                    homeModel.accountHolder.toUpperCase(),
                    style: const TextStyle(color: Colors.white, fontWeight: FontWeight.bold),
                  ),
                ],
              ),
              Column(
                crossAxisAlignment: CrossAxisAlignment.end,
                children: [
                  Text(
                    getTranslated("total_balance", context)!,
                    style: TextStyle(color: Colors.white.withValues(alpha: 0.8), fontSize: 10),
                  ),
                  Text(
                    "${homeModel.currency} ${homeModel.balance.toStringAsFixed(2)}",
                    style: TextStyle(
                      color: secondaryColor,
                      fontSize: 18,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                ],
              ),
            ],
          ),
        ],
      ),
    );
  }
}