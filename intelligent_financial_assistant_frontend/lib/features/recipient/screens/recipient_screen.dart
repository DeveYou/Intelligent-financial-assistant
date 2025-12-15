import 'package:flutter/material.dart';
import 'package:intelligent_financial_assistant_frontend/localization/language_constraints.dart';
import 'package:provider/provider.dart';
import '../controllers/recipient_controller.dart';
import '../domains/models/recipient_model.dart';
import '../widgets/recipient_widget_card.dart';

class RecipientScreen extends StatefulWidget {
  const RecipientScreen({super.key});

  @override
  State<RecipientScreen> createState() => _RecipientScreenState();
}

class _RecipientScreenState extends State<RecipientScreen> {
  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      Provider.of<RecipientController>(context, listen: false).getRecipientList();
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Theme.of(context).scaffoldBackgroundColor,
      appBar: AppBar(
        title: Text(getTranslated("recipients", context)!, style: Theme.of(context).textTheme.titleLarge),
        backgroundColor: Colors.transparent,
        elevation: 0,
        iconTheme: IconThemeData(color: Theme.of(context).textTheme.bodyLarge?.color),
      ),
      floatingActionButton: FloatingActionButton.extended(
        onPressed: () => _showAddOrUpdateRecipientModal(context),
        label: Text(getTranslated("add_new", context)!),
        icon: const Icon(Icons.add_circle_outline),
        backgroundColor: Theme.of(context).primaryColor,
        elevation: 4,
      ),
      body: Consumer<RecipientController>(
        builder: (context, controller, child) {
          if (controller.isLoading && (controller.recipientList == null)) {
            return const Center(child: CircularProgressIndicator());
          }

          if (controller.recipientList == null || controller.recipientList!.isEmpty) {
            return Center(
              child: Text(
                getTranslated("no_recipients_yet", context)!,
                style: Theme.of(context).textTheme.bodyLarge?.copyWith(
                  color: Theme.of(context).disabledColor,
                ),
              ),
            );
          }

          return RefreshIndicator(
            onRefresh: () async {
              await controller.getRecipientList(reload: true);
            },
            child: ListView.builder(
              padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 10),
              itemCount: controller.recipientList!.length,
              itemBuilder: (context, index) {
                final recipient = controller.recipientList![index];
                return RecipientWidgetCard(
                  recipient: recipient,
                  onEdit: () => _showAddOrUpdateRecipientModal(context, recipient: recipient),
                  onDelete: () => _showDeleteConfirmation(context, recipient.id!, controller),
                );
              },
            ),
          );
        },
      ),
    );
  }

  // Unified Modal for Add and Update
  void _showAddOrUpdateRecipientModal(BuildContext context, {RecipientModel? recipient}) {
    final bool isUpdate = recipient != null;
    final TextEditingController nameController = TextEditingController(text: isUpdate ? recipient.fullName : '');
    final TextEditingController ibanController = TextEditingController(text: isUpdate ? recipient.iban : '');
    final GlobalKey<FormState> formKey = GlobalKey<FormState>();

    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      backgroundColor: Colors.transparent,
      builder: (context) {
        return Container(
          padding: EdgeInsets.only(
            bottom: MediaQuery.of(context).viewInsets.bottom + 20,
            top: 25,
            left: 20,
            right: 20,
          ),
          decoration: BoxDecoration(
            color: Theme.of(context).cardColor,
            borderRadius: const BorderRadius.vertical(top: Radius.circular(25)),
          ),
          child: Form(
            key: formKey,
            child: Column(
              mainAxisSize: MainAxisSize.min,
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Center(
                  child: Container(
                    width: 50,
                    height: 5,
                    decoration: BoxDecoration(
                      color: Theme.of(context).dividerColor,
                      borderRadius: BorderRadius.circular(5),
                    ),
                  ),
                ),
                const SizedBox(height: 25),
                Text(
                  isUpdate ? getTranslated("update_recipient", context)! : getTranslated("add_new_recipient", context)!,
                  style: Theme.of(context).textTheme.headlineSmall?.copyWith(fontWeight: FontWeight.bold),
                ),
                const SizedBox(height: 25),
                TextFormField(
                  controller: nameController,
                  textInputAction: TextInputAction.next,
                  decoration: InputDecoration(
                    labelText: getTranslated("full_name", context)!,
                    prefixIcon: const Icon(Icons.person),
                    border: OutlineInputBorder(borderRadius: BorderRadius.circular(12)),
                    filled: true,
                    fillColor: Theme.of(context).canvasColor,
                  ),
                  validator: (value) => value!.isEmpty ? getTranslated('please_enter_name', context)! : null,
                ),
                const SizedBox(height: 15),
                TextFormField(
                  controller: ibanController,
                  textInputAction: TextInputAction.done,
                  decoration: InputDecoration(
                    labelText: getTranslated("iban_number", context)!,
                    prefixIcon: const Icon(Icons.account_balance_wallet),
                    border: OutlineInputBorder(borderRadius: BorderRadius.circular(12)),
                    filled: true,
                    fillColor: Theme.of(context).canvasColor,
                  ),
                  validator: (value) {
                    if (value == null || value.isEmpty) return getTranslated('please_enter_iban', context)!;
                    if (value.length < 15) return getTranslated('iban_too_short', context)!;
                    return null;
                  },
                ),
                const SizedBox(height: 30),
                Consumer<RecipientController>(
                  builder: (context, controller, child) {
                    return SizedBox(
                      width: double.infinity,
                      height: 54,
                      child: ElevatedButton(
                        style: ElevatedButton.styleFrom(
                          backgroundColor: Theme.of(context).primaryColor,
                          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(14)),
                        ),
                        onPressed: controller.isLoading
                            ? null
                            : () async {
                          if (formKey.currentState!.validate()) {
                            bool success;
                            if (isUpdate) {
                              success = await controller.updateRecipient(
                                recipient.id!,
                                nameController.text,
                                ibanController.text,
                              );
                            } else {
                              success = await controller.addRecipient(
                                nameController.text,
                                ibanController.text,
                              );
                            }

                            if (success && context.mounted) {
                              Navigator.pop(context);
                              ScaffoldMessenger.of(context).showSnackBar(
                                SnackBar(content: Text(isUpdate ? getTranslated('updated_successfully', context)! : getTranslated('added_successfully', context)!)),
                              );
                            }
                          }
                        },
                        child: controller.isLoading
                            ? const CircularProgressIndicator(color: Colors.white)
                            : Text(
                          isUpdate ? getTranslated("update_recipient", context)! : getTranslated("save_recipient", context)!,
                          style: const TextStyle(color: Colors.white, fontSize: 16, fontWeight: FontWeight.w600),
                        ),
                      ),
                    );
                  },
                ),
              ],
            ),
          ),
        );
      },
    );
  }

  void _showDeleteConfirmation(BuildContext context, int id, RecipientController controller) {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text("Delete Recipient"),
        content: const Text("Are you sure you want to remove this recipient?"),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: Text("Cancel", style: TextStyle(color: Theme.of(context).hintColor)),
          ),
          TextButton(
            onPressed: () async {
              Navigator.pop(context); // Close dialog
              bool success = await controller.deleteRecipient(id);
              if (success && context.mounted) {
                ScaffoldMessenger.of(context).showSnackBar(
                  const SnackBar(content: Text("Recipient removed successfully")),
                );
              }
            },
            child: Text("Delete", style: TextStyle(color: Theme.of(context).colorScheme.error)),
          ),
        ],
      ),
    );
  }
}