import 'package:flutter/material.dart';

class BatteryState {
  final int batteryLevel;
  final BatteryStatus status;
  final BatteryHealth health;

  BatteryState._({required this.batteryLevel, required this.status, required this.health});
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

enum BatteryStatus {
  charging,
  discharging,
  full,
  notCharging,
  unknown,
  ;

  static BatteryStatus fromInt(int value) => switch (value) {
        2 => BatteryStatus.charging,
        3 => BatteryStatus.discharging,
        5 => BatteryStatus.full,
        4 => BatteryStatus.notCharging,
        1 => BatteryStatus.unknown,
        _ => throw Exception('Unknown BatteryStatus: $value'),
      };
}

enum BatteryHealth {
  cold,
  dead,
  good,
  overheat,
  overVoltage,
  unknown,
  unspecifiedFailure,
  ;

  static BatteryHealth fromInt(int value) => switch (value) {
        2 => BatteryHealth.cold,
        4 => BatteryHealth.dead,
        3 => BatteryHealth.good,
        5 => BatteryHealth.overheat,
        6 => BatteryHealth.overVoltage,
        1 => BatteryHealth.unknown,
        7 => BatteryHealth.unspecifiedFailure,
        _ => throw Exception('Unknown BatteryHealth: $value'),
      };
  bool get isOK => this == BatteryHealth.good;
}
