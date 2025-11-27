
import 'package:flutter/foundation.dart';
import 'package:flutter/widgets.dart';

class ErrorBoundary extends StatefulWidget {
  final Widget child;

  const ErrorBoundary({super.key, required this.child});

  @override
  _ErrorBoundaryState createState() => _ErrorBoundaryState();
}

class _ErrorBoundaryState extends State<ErrorBoundary> {
  FlutterErrorDetails? _errorDetails;
  FlutterExceptionHandler? _oldOnError;

  @override
  void initState() {
    super.initState();

    // Save previous handler so we can restore it on dispose.
    _oldOnError = FlutterError.onError;

    FlutterError.onError = (FlutterErrorDetails details) {
      // Preserve default behaviour (log / present).
      FlutterError.presentError(details);

      // Schedule state update after the current frame — safe time to call setState.
      if (!mounted) return;
      WidgetsBinding.instance.addPostFrameCallback((_) {
        if (!mounted) return;
        setState(() {
          _errorDetails = details;
        });
      });
    };
  }

  @override
  void dispose() {
    // restore the original handler
    FlutterError.onError = _oldOnError;
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    if (_errorDetails != null) {
      // Provide a Directionality here so error UI can render anywhere in the tree.
      return Directionality(
        textDirection: TextDirection.ltr,
        child: Center(
          child: Container(
            padding: const EdgeInsets.all(16),
            width: double.infinity,
            height: double.infinity,
            child: SingleChildScrollView(
              child: Column(
                mainAxisSize: MainAxisSize.min,
                crossAxisAlignment: CrossAxisAlignment.center,
                children: [
                  const Text(
                    'An error occurred.',
                    style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold),
                  ),
                  const SizedBox(height: 16),

                  // The long stack trace needs scrolling
                  Text(
                    _errorDetails!.exceptionAsString(),
                    style: const TextStyle(fontSize: 14),
                  ),
                ],
              ),
            ),
          ),
        ),
      );

    }

    return widget.child;
  }
}
