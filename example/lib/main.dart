import 'dart:developer';
import 'dart:io';

import 'package:flutter/material.dart';
import 'package:scanwedge/scanwedge.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();

  final _demoProfileName = 'DemoProfile';
}

class _MyAppState extends State<MyApp> {
  Scanwedge? _scanwedgePlugin;
  String? _deviceInfo;
  final notifierDisableKeystroke = ValueNotifier(true);
  final notifierBatteryStatus = ValueNotifier<ExtendedBatteryStatus?>(null);
  final notifierAimType = ValueNotifier(AimType.trigger);
  Stream<ExtendedBatteryStatus>? _batteryStream;
  final listItems = <dynamic>[];

  @override
  void initState() {
    super.initState();
    try {
      Scanwedge.initialize().then((scanwedge) {
        _scanwedgePlugin = scanwedge;
        _deviceInfo = scanwedge.deviceName;
        _scanwedgePlugin?.stream.listen((scanResult) {
          log('Scan result: $scanResult');
          _addListItem(scanResult);
        }, onError: (error) {
          log('Error: $error');
        });
        setState(() {});
      });
    } catch (e) {
      log('initState Exception: $e');
    }
  }

  @override
  dispose() {
    super.dispose();
  }

  _createProfile() async {
    try {
      final wasCreateProfileSuccessful = await _scanwedgePlugin?.createScanProfile(
        switch (_scanwedgePlugin?.manufacturer) {
          'ZEBRA' => ZebraProfileModel(
              profileName: widget._demoProfileName,
              enabledBarcodes: [
                BarcodeConfig(barcodeType: BarcodeTypes.datamatrix),
                BarcodeConfig(barcodeType: BarcodeTypes.gs1DataBar),
                BarcodeConfig(barcodeType: BarcodeTypes.gs1DataBarExpanded),
              ],
              enableKeyStroke: !notifierDisableKeystroke.value,
              aimType: notifierAimType.value,
            ),
          'Honeywell' => HoneywellProfileModel(
              profileName: widget._demoProfileName,
              enableEanCheckDigitTransmission: true,
              enabledBarcodes: [
                BarcodeConfig(barcodeType: BarcodeTypes.code39),
                BarcodeConfig(barcodeType: BarcodeTypes.code128),
                BarcodeConfig(barcodeType: BarcodeTypes.ean8),
                BarcodeConfig(barcodeType: BarcodeTypes.ean13),
              ],
            ),
          _ => ProfileModel(
              profileName: widget._demoProfileName,
              enabledBarcodes: [
                BarcodeTypes.code39.create(),
                BarcodeTypes.code128.create(minLength: 10, maxLength: 15),
                BarcodeTypes.qrCode.create(),
                BarcodeTypes.datamatrix.create(),
              ],
              keepDefaults: false,
            ),
        },
      );

      log('_createProfile()-$wasCreateProfileSuccessful');
    } catch (e) {
      log('_createProfile Exception: $e');
    }
  }

  _addListItem(ScanResult item) {
    debugPrint('Adding item: $item');
    if (listItems.length > 10) {
      listItems.removeAt(0);
    }
    listItems.insert(0, item);
    setState(() {});
  }

  _triggerScan() async {
    try {
      log('_triggerScan()-${await _scanwedgePlugin?.toggleScanning()}');
    } catch (e) {
      log('_triggerScan Exception: $e');
    }
  }

  void _activateBatteryMonitor() async {
    if (_batteryStream != null) {
      log('Battery monitor already active');
      return;
    }
    _batteryStream = await _scanwedgePlugin?.monitorBatteryStatus();
    _batteryStream?.listen((batteryStatus) {
      log('Battery status: $batteryStatus');
      notifierBatteryStatus.value = batteryStatus;
    }, onError: (error) {
      log('Error: $error');
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
          actions: [
            PopupMenuButton(
              itemBuilder: (context) => [
                const PopupMenuItem(
                  value: 'trigger',
                  child: Text('Trigger scan'),
                ),
                const PopupMenuItem(
                  value: 'enable',
                  child: Text('Enable scanner'),
                ),
                const PopupMenuItem(
                  value: 'disable',
                  child: Text('Disable scanner'),
                ),
                PopupMenuItem(
                  value: 'monitor',
                  enabled: _batteryStream == null,
                  child: Text(_batteryStream == null ? 'Activate monitor battery' : '🏃 Monitoring battery'),
                ),
                const PopupMenuItem(
                  value: 'exit',
                  child: Text('Exit application'),
                ),
                const PopupMenuItem(
                  value: 'battery',
                  child: Text('Get battery status'),
                ),
              ],
              onSelected: (value) => switch (value) {
                'trigger' => _triggerScan(),
                'enable' => _scanwedgePlugin?.enableScanner(),
                'disable' => _scanwedgePlugin?.disableScanner(),
                'battery' => _scanwedgePlugin?.getExtendedBatteryStatus().then((status) => log('Battery status(${status?.batteryDecommissionThreshold}): $status')),
                'monitor' => _activateBatteryMonitor(),
                'exit' => exit(0),
                _ => null,
              },
            ),
          ],
        ),
        body: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          // mainAxisAlignment: MainAxisAlignment.spaceAround,
          children: [
            // ElevatedButton(onPressed: _scanwedgePlugin.test, child: const Text('Create profile')),
            Text(_deviceInfo ?? 'No device info', style: Theme.of(context).textTheme.labelSmall),
            Card(
                child: Padding(
              padding: const EdgeInsets.all(8.0),
              child: Row(
                // crossAxisAlignment: CrossAxisAlignment.end,
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Flexible(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      mainAxisSize: MainAxisSize.min,
                      children: [
                        Row(
                          mainAxisSize: MainAxisSize.min,
                          children: [
                            const Text('Disable keystroke'),
                            const SizedBox(width: 5),
                            ValueListenableBuilder(
                                valueListenable: notifierDisableKeystroke,
                                builder: (context, disableKeyboard, _) => Switch(value: disableKeyboard, onChanged: (value) => notifierDisableKeystroke.value = value)),
                          ],
                        ),
                        ValueListenableBuilder(
                            valueListenable: notifierAimType,
                            builder: (context, aimType, _) => PopupMenuButton(
                                  elevation: 4,
                                  padding: const EdgeInsets.all(5),
                                  itemBuilder: ((context) => AimType.values
                                      .map((e) => PopupMenuItem(
                                            value: e,
                                            child: Text(e.toString().split('.').last),
                                          ))
                                      .toList()),
                                  child: Container(
                                    padding: const EdgeInsets.symmetric(vertical: 4, horizontal: 8),
                                    decoration: BoxDecoration(
                                        color: Colors.blue, borderRadius: BorderRadius.circular(5), boxShadow: const [BoxShadow(offset: Offset(2, 2), blurRadius: 1, color: Colors.black54)]),
                                    child: Column(
                                      mainAxisSize: MainAxisSize.min,
                                      crossAxisAlignment: CrossAxisAlignment.start,
                                      children: [
                                        Text('AimType:', style: Theme.of(context).textTheme.bodyMedium?.copyWith(color: Colors.white)),
                                        Padding(
                                          padding: const EdgeInsets.only(left: 6.0),
                                          child: Text(aimType.toString().split('.').last, style: Theme.of(context).textTheme.titleLarge?.copyWith(color: Colors.white)),
                                        ),
                                      ],
                                    ),
                                  ),
                                  onSelected: (newAimType) => notifierAimType.value = newAimType,
                                )),
                      ],
                    ),
                  ),
                  ElevatedButton(onPressed: _createProfile, child: const Text('Create profile')),
                ],
              ),
            )),
            TextFormField(
              decoration: const InputDecoration(hintText: 'auto inserted if keystroke and focused', contentPadding: EdgeInsets.symmetric(horizontal: 6)),
            ),
            ValueListenableBuilder(
                valueListenable: notifierBatteryStatus,
                builder: (context, batteryStatus, _) {
                  debugPrint('Battery status rebuild');
                  if (batteryStatus == null) {
                    return const SizedBox.shrink();
                  }
                  return Container(
                    padding: const EdgeInsets.all(2),
                    decoration: BoxDecoration(
                        color: batteryStatus.batteryDecommissionStatus == BatteryDecommissionStatus.decommissionedBattery ? Colors.red : Colors.green,
                        borderRadius: BorderRadius.circular(5),
                        boxShadow: const [BoxShadow(offset: Offset(2, 2), blurRadius: 1, color: Colors.black54)]),
                    child: Column(
                      mainAxisSize: MainAxisSize.min,
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Row(
                          mainAxisAlignment: MainAxisAlignment.spaceBetween,
                          children: [
                            Text('Battery: ${batteryStatus.batteryPercentage}%'),
                            Text(batteryStatus.health.toString()),
                            Text(batteryStatus.capacityLevel.toString()),
                          ],
                        ),
                        Row(
                          mainAxisAlignment: MainAxisAlignment.spaceBetween,
                          children: [
                            Text('Serial: ${batteryStatus.serialNumber}'),
                            Text('Part: ${batteryStatus.partNumber}'),
                            Text('Mfd: ${batteryStatus.mfd?.toIso8601String().split('T').first}'),
                          ],
                        ),
                        Row(
                          mainAxisAlignment: MainAxisAlignment.spaceBetween,
                          children: [
                            Text('Updated: ${batteryStatus.createdAt.toIso8601String().split('T').first}'),
                            Text('Cycles: ${batteryStatus.cycleCount}'),
                          ],
                        ),
                      ],
                    ),
                  );
                }),
            Flexible(
              child: Card(
                child: Padding(
                  padding: const EdgeInsets.all(8.0),
                  child: listItems.isEmpty
                      ? const Center(child: Text('No scans available'))
                      : Column(
                          children: [
                            const Text('Last scan:'),
                            Flexible(
                                child: ListView.builder(
                                    itemCount: listItems.length,
                                    itemBuilder: (context, index) {
                                      final item = listItems[index];
                                      return switch (item) {
                                        (ScanResult scanResult) => ListTile(title: Text(scanResult.barcode), subtitle: Text(scanResult.barcodeType.name)),
                                        (ExtendedBatteryStatus batteryStatus) => ListTile(
                                            title: Text('Battery status: ${batteryStatus.batteryPercentage}%'),
                                            subtitle: Text('Battery decommission: ${batteryStatus.batteryDecommissionStatus}'),
                                          ),
                                        _ => throw Exception('Unknown item type'),
                                      };
                                    }))
                          ],
                        ),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
