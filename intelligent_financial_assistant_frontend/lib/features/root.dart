import 'package:flutter/material.dart';
import 'package:intelligent_financial_assistant_frontend/common/basewidgets/app_exit_card_widget.dart';
import 'package:intelligent_financial_assistant_frontend/common/basewidgets/custom_menu_widget.dart';
import 'package:intelligent_financial_assistant_frontend/common/models/navigation_model.dart';
import 'package:intelligent_financial_assistant_frontend/features/account/screens/account_screen.dart';
import 'package:intelligent_financial_assistant_frontend/features/assistant/screens/assistant_screen.dart';
import 'package:intelligent_financial_assistant_frontend/features/home/screens/home_screen.dart';
import 'package:intelligent_financial_assistant_frontend/features/transaction/screens/transactions_screen.dart';
import 'package:intelligent_financial_assistant_frontend/utils/dimensions.dart';
import 'package:intelligent_financial_assistant_frontend/utils/images.dart';

class Root extends StatefulWidget {
  static final GlobalKey<_RootState> _rootKey = GlobalKey<_RootState>();

  Root({Key? key}) : super(key: _rootKey);

  static void setPage(int page) => _rootKey.currentState?._setPage(page);

  @override
  State<Root> createState() => _RootState();
}

class _RootState extends State<Root> {
  int _pageIndex = 0;
  late List<NavigationModel> _screens ;
  final GlobalKey<ScaffoldMessengerState> _scaffoldKey = GlobalKey();
  final PageStorageBucket bucket = PageStorageBucket();

  @override
  void initState() {
    super.initState();

    _screens = [
      NavigationModel(
        name: 'home',
        icon: Images.homeImage,
        screen: const HomeScreen(),
      ),

      NavigationModel(name: 'transaction', icon: Images.transaction, screen: const TransactionsScreen()),
      NavigationModel(name: 'account', icon: Images.accountImage, screen:  const AccountScreen()),
      NavigationModel(name: 'assistant', icon: Images.assistantImage, screen:  const AssistantScreen()),

    ];

  }
  @override
  Widget build(BuildContext context) {
    return PopScope(
      canPop: false,
      onPopInvoked: (val) async {
          if(_pageIndex != 0) {
            _setPage(0);
            return;
          }else {
            showModalBottomSheet(backgroundColor: Colors.transparent,
                context: context, builder: (_)=> const AppExitCard());
          }
          return;
      },
      child: Scaffold(
          key: _scaffoldKey,
          body: PageStorage(bucket: bucket, child: _screens[_pageIndex].screen),
          bottomNavigationBar: _pageIndex == 3 ? null : Container(height: 68,
              decoration: BoxDecoration(borderRadius: BorderRadius.vertical(
                  top: Radius.circular(Dimensions.paddingSizeLarge)),
                color: Theme.of(context).cardColor,
                boxShadow: [BoxShadow(offset: const Offset(1,1), blurRadius: 2, spreadRadius: 1,
                    color: Theme.of(context).primaryColor.withOpacity(.125))],),
              child: Row(mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                  children: _getBottomWidget()))
      ),
    );
  }

  void _setPage(int pageIndex) {
    setState(() {
      _pageIndex = pageIndex;
    });
  }

  List<Widget> _getBottomWidget() {
    List<Widget> list = [];
    for(int index = 0; index < _screens.length; index++) {
      list.add(Expanded(child: CustomMenuWidget(
          isSelected: _pageIndex == index,
          name: _screens[index].name,
          icon: _screens[index].icon,
          onTap: () => _setPage(index))));
    }
    return list;
  }
}
