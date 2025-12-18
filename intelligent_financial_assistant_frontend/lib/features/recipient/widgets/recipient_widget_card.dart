import 'package:flutter/material.dart';
import 'package:intelligent_financial_assistant_frontend/localization/language_constraints.dart';
import '../domains/models/recipient_model.dart';

class RecipientWidgetCard extends StatelessWidget {
  final RecipientModel recipient;
  final VoidCallback? onTap;
  final VoidCallback? onEdit;
  final VoidCallback? onDelete;

  const RecipientWidgetCard({
    super.key,
    required this.recipient,
    this.onTap,
    this.onEdit,
    this.onDelete,
  });

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: onTap,
      child: Container(
        margin: const EdgeInsets.only(bottom: 15),
        padding: const EdgeInsets.all(16),
        decoration: BoxDecoration(
          color: Theme.of(context).cardColor,
          borderRadius: BorderRadius.circular(16),
          boxShadow: [
            BoxShadow(
              color: Theme.of(context).shadowColor.withValues(alpha: 0.08),
              blurRadius: 15,
              offset: const Offset(0, 5),
            ),
          ],
        ),
        child: Row(
          children: [
            Container(
              height: 50,
              width: 50,
              decoration: BoxDecoration(
                color: Theme.of(context).primaryColor.withValues(alpha: 0.1),
                shape: BoxShape.circle,
              ),
              child: Center(
                child: Text(
                  (recipient.fullName?.isNotEmpty ?? false)
                      ? recipient.fullName![0].toUpperCase()
                      : "?",
                  style: TextStyle(
                    color: Theme.of(context).primaryColor,
                    fontSize: 20,
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ),
            ),
            const SizedBox(width: 16),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    recipient.fullName ?? getTranslated("unknown_recipient", context)!,
                    style: Theme.of(context).textTheme.titleMedium?.copyWith(
                      fontWeight: FontWeight.w700,
                    ),
                  ),
                  const SizedBox(height: 4),
                  Text(
                    recipient.iban ?? getTranslated("no_iban", context)!,
                    style: Theme.of(context).textTheme.bodySmall?.copyWith(
                      color: Theme.of(context).hintColor,
                      fontFamily: 'Monospace',
                    ),
                    overflow: TextOverflow.ellipsis,
                  ),
                ],
              ),
            ),
            // Menu for Edit/Delete
            PopupMenuButton<String>(
              icon: Icon(Icons.more_vert, color: Theme.of(context).hintColor),
              shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
              onSelected: (value) {
                if (value == 'edit') {
                  if (onEdit != null) onEdit!();
                } else if (value == 'delete') {
                  if (onDelete != null) onDelete!();
                }
              },
              itemBuilder: (BuildContext context) => <PopupMenuEntry<String>>[
                PopupMenuItem<String>(
                  value: 'edit',
                  child: Row(
                    children: [
                      Icon(Icons.edit, size: 20, color: Theme.of(context).primaryColor),
                      const SizedBox(width: 10),
                      Text(getTranslated('edit', context)!, style: Theme.of(context).textTheme.bodyMedium),
                    ],
                  ),
                ),
                PopupMenuItem<String>(
                  value: 'delete',
                  child: Row(
                    children: [
                      Icon(Icons.delete, size: 20, color: Theme.of(context).colorScheme.error),
                      const SizedBox(width: 10),
                      Text(getTranslated('delete', context)!, style: Theme.of(context).textTheme.bodyMedium),
                    ],
                  ),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }
}