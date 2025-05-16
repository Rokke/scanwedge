import 'package:flutter/material.dart';
import 'package:scanwedge/scanwedge.dart';

class BatteryState {
  final int batteryLevel;
  final BatteryStatus status;
  final BatteryHealth health;

  BatteryState._({required this.batteryLevel, required this.status, required this.health});
  factory BatteryState.fromJsonReceiver(Map<String, dynamic> json) {
    debugPrint('BatteryState.fromJsonReceiver: $json');
    if (json.isEmpty) {
      throw Exception('Invalid batteryState');
    }
    return BatteryState._(
      batteryLevel: json['batteryLevel'] ?? -1,
      status: BatteryStatus.fromInt(json['status'] ?? 1),
      health: BatteryHealth.fromInt(json['health'] ?? 1),
    );
  }
  factory BatteryState.fromReceiver(String batteryState) {
    if (batteryState.isEmpty) {
      throw Exception('Invalid batteryState');
    }
    final state = batteryState.split('|').map((e) => int.parse(e)).toList();
    if (state.length != 3) {
      throw Exception('Invalid batteryState: $batteryState');
    }
    return BatteryState._(batteryLevel: state[0], status: BatteryStatus.fromInt(state[1]), health: BatteryHealth.fromInt(state[2]));
  }
  factory BatteryState.unknown([int? level]) => BatteryState._(batteryLevel: level ?? -1, status: BatteryStatus.unknown, health: BatteryHealth.unknown);

  Map<String, dynamic> toJson() {
    return {
      'batteryLevel': batteryLevel,
      'status': status.name,
      'health': health.name,
    };
  }

  IconData get icon => !health.isOK
      ? Icons.battery_alert
      : switch (batteryLevel) {
          > 95 => Icons.battery_full,
          > 80 => Icons.battery_6_bar,
          > 60 => Icons.battery_5_bar,
          > 40 => Icons.battery_4_bar,
          > 20 => Icons.battery_3_bar,
          > 10 => Icons.battery_2_bar,
          _ => Icons.battery_0_bar
        };

  @override
  String toString() => 'BatteryState{batteryLevel: $batteryLevel, status: $status, health: $health}';
}
