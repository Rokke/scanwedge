/*{batteryPercentage=100, status=5, health=2, temperature=330, voltage=4326, cycleCount=0, chargingStatus=-1, capacity=0, batteryUsageNumber=18, batteryErrorStatus=0, batteryType=206, technology=Li-ion, batteryUsageDecommissionThreshold=400, iconSmall=17303637,
maxChargingVoltage=5000000, zcmMode=, shutdownLevel=4, zcmEnabled=false, maxDischargeTempShutdownLevel=600, maxChargingCurrent=475000, adjustShutdownLevel=100, lowLevel=18, ratedCapacity=3300, plugged=2, present=true, mfd=2023-02-02, seq=2074, zcmExtra=,
chargeCounter=3395080, level=100, baseCumulativeCharge=61149, scale=100, partNumber=BT-000409-50 R.B, criticalLevel=10, minDischargeTempShutdownLevel=-200, serialNumber=T5362, invalidCharger=0, batteryLow=false, batteryDecommission=0}*/

import 'dart:developer';

import 'package:flutter/foundation.dart';

class ExtendedBatteryStatus {
  final BatteryStatus status;
  final BatteryHealth health;
  final BatteryCapacityLevel capacityLevel;
  final double temperature, voltage;
  final String technology;
  final bool present;
  final int cycleCount, chargingStatus, plugged, chargeCounter, level, scale;
  final Map<String, dynamic>? extraMap;
  final DateTime createdAt = DateTime.now();
  // Zebra specific, null if not supported
  final int? batteryErrorStatus, batteryUsageDecommissionThreshold, maxChargingVoltage, shutdownLevel;
  final double? maxDischargeTempShutdownLevel, minDischargeTempShutdownLevel;
  final int? maxChargingCurrent, adjustShutdownLevel, lowLevel, seq;
  final int? criticalLevel, invalidCharger, iconSmall;
  // Below are only supported on Zebra devices
  final String? zcmMode, zcmExtra; // '' if Zebra device but not supported
  final bool? zcmEnabled, batteryLow;
  final int? batteryType;
  // Below are only supported on Power Precision devices
  final int? batteryUsageNumber, ratedCapacity, baseCumulativeCharge, batteryDecommission;
  final String? partNumber, serialNumber; // '' if Zebra device but not supported
  DateTime? mfd;
  // Below are only supported on Power Precision Plus devices
  final int? totalCumulativeCharge, secondsSinceFirstUse, healthPercentage, timeToEmpty, timeToFull, presentCharge, presentCapacity;
  // Below are only supported on devices with backup battery
  final int? bkVoltage;
  ExtendedBatteryStatus({
    required this.status,
    required this.health,
    required this.capacityLevel,
    required this.temperature,
    required this.voltage,
    required this.technology,
    required this.present,
    required this.cycleCount,
    required this.chargingStatus,
    required this.plugged,
    required this.chargeCounter,
    required this.level,
    required this.scale,
    // Zebra specific
    required this.batteryUsageNumber,
    required this.batteryErrorStatus,
    required this.batteryType,
    required this.batteryUsageDecommissionThreshold,
    required this.iconSmall,
    required this.maxChargingVoltage,
    required this.zcmMode,
    required this.shutdownLevel,
    required this.zcmEnabled,
    required this.maxDischargeTempShutdownLevel,
    required this.maxChargingCurrent,
    required this.adjustShutdownLevel,
    required this.lowLevel,
    required this.ratedCapacity,
    required this.mfd,
    required this.seq,
    required this.zcmExtra,
    required this.baseCumulativeCharge,
    required this.partNumber,
    required this.criticalLevel,
    required this.minDischargeTempShutdownLevel,
    required this.batteryLow,
    required this.serialNumber,
    required this.invalidCharger,
    required this.batteryDecommission,
    required this.totalCumulativeCharge,
    required this.secondsSinceFirstUse,
    required this.presentCapacity,
    required this.healthPercentage,
    required this.timeToEmpty,
    required this.timeToFull,
    required this.presentCharge,
    required this.bkVoltage,
    required this.extraMap,
  });
  BatteryDecommissionStatus? get batteryDecommissionStatus => batteryDecommission == null ? null : BatteryDecommissionStatus.fromInt(batteryDecommission!);
  factory ExtendedBatteryStatus.fromJson(Map<String, dynamic> json) {
    try {
      debugPrint('ExtendedBatteryStatus.fromJson: $json');
      return ExtendedBatteryStatus(
        status: json['status'] == null ? BatteryStatus.unknown : BatteryStatus.fromInt(json['status']),
        health: json['health'] == null ? BatteryHealth.unknown : BatteryHealth.fromInt(json['health']),
        temperature: json['temperature'] ?? -1.0,
        voltage: json['voltage'] ?? -1.0,
        cycleCount: json['cycleCount'] ?? -1,
        chargingStatus: json['chargingStatus'] ?? -1,
        capacityLevel: json['capacityLevel'] == null ? BatteryCapacityLevel.unsupported : BatteryCapacityLevel.fromInt(json['capacityLevel']),
        technology: json['technology'] ?? '',
        plugged: json['plugged'] ?? -1,
        present: json['present'] == true,
        chargeCounter: json['chargeCounter'] ?? -1,
        level: json['level'] ?? -1,
        scale: json['scale'] ?? -1,

        // Zebra specific
        batteryUsageNumber: json['batteryUsageNumber'],
        batteryErrorStatus: json['batteryErrorStatus'],
        batteryType: json['batteryType'],
        batteryUsageDecommissionThreshold: json['batteryUsageDecommissionThreshold'],
        iconSmall: json['iconSmall'],
        maxChargingVoltage: json['maxChargingVoltage'],
        zcmMode: json['zcmMode'],
        shutdownLevel: json['shutdownLevel'],
        zcmEnabled: json['zcmEnabled'] ?? false,
        maxDischargeTempShutdownLevel: json['maxDischargeTempShutdownLevel'],
        maxChargingCurrent: json['maxChargingCurrent'],
        adjustShutdownLevel: json['adjustShutdownLevel'],
        lowLevel: json['lowLevel'],
        ratedCapacity: json['ratedCapacity'],
        mfd: json['mfd'] == null ? null : DateTime.tryParse(json['mfd']),
        seq: json['seq'],
        zcmExtra: json['zcmExtra'],
        baseCumulativeCharge: json['baseCumulativeCharge'],
        partNumber: json['partNumber'],
        criticalLevel: json['criticalLevel'],
        minDischargeTempShutdownLevel: json['minDischargeTempShutdownLevel'],
        batteryLow: json['batteryLow'] == 'true',
        serialNumber: json['serialNumber'],
        invalidCharger: json['invalidCharger'],
        batteryDecommission: json['batteryDecommission'],
        totalCumulativeCharge: json['totalCumulativeCharge'],
        secondsSinceFirstUse: json['secondsSinceFirstUse'],
        presentCapacity: json['presentCapacity'],
        healthPercentage: json['healthPercentage'],
        timeToEmpty: json['timeToEmpty'],
        timeToFull: json['timeToFull'],
        presentCharge: json['presentCharge'],
        bkVoltage: json['bkVoltage'],
        extraMap: json['extraMap'] == null ? null : Map<String, dynamic>.from(json['extraMap']),
      );
    } catch (e, s) {
      log('ExtendedBatteryStatus.fromJson, Error: $e, Stack: $s');
      rethrow;
    }
  }
  double? get batteryDecommissionThreshold {
    if (batteryUsageNumber == null || batteryUsageDecommissionThreshold == -1) return null;
    return batteryUsageNumber! / batteryUsageDecommissionThreshold!;
  }

  int get batteryPercentage => ((level / scale) * 100).toInt();

  BatteryPluggedStatus get pluggedStatus => BatteryPluggedStatus.fromInt(plugged);
  @override
  operator ==(Object other) {
    if (identical(this, other)) return true;
    if (other is! ExtendedBatteryStatus) return false;
    return other.status == status &&
        other.health == health &&
        other.temperature == temperature &&
        other.voltage == voltage &&
        other.cycleCount == cycleCount &&
        other.chargingStatus == chargingStatus &&
        other.capacityLevel == capacityLevel &&
        other.batteryUsageNumber == batteryUsageNumber &&
        other.batteryErrorStatus == batteryErrorStatus &&
        other.batteryType == batteryType &&
        other.technology == technology &&
        other.batteryUsageDecommissionThreshold == batteryUsageDecommissionThreshold &&
        other.iconSmall == iconSmall &&
        other.maxChargingVoltage == maxChargingVoltage &&
        other.zcmMode == zcmMode &&
        other.shutdownLevel == shutdownLevel &&
        other.zcmEnabled == zcmEnabled &&
        other.maxDischargeTempShutdownLevel == maxDischargeTempShutdownLevel &&
        other.maxChargingCurrent == maxChargingCurrent &&
        other.adjustShutdownLevel == adjustShutdownLevel &&
        other.lowLevel == lowLevel &&
        other.ratedCapacity == ratedCapacity &&
        other.plugged == plugged &&
        other.present == present &&
        other.mfd == mfd &&
        other.seq == seq &&
        other.zcmExtra == zcmExtra &&
        other.chargeCounter == chargeCounter &&
        other.level == level &&
        other.baseCumulativeCharge == baseCumulativeCharge &&
        other.scale == scale &&
        other.partNumber == partNumber &&
        other.criticalLevel == criticalLevel &&
        other.minDischargeTempShutdownLevel == minDischargeTempShutdownLevel &&
        other.batteryLow == batteryLow &&
        other.serialNumber == serialNumber &&
        other.invalidCharger == invalidCharger;
  }

  @override
  int get hashCode =>
      status.hashCode ^
      health.hashCode ^
      temperature.hashCode ^
      voltage.hashCode ^
      cycleCount.hashCode ^
      chargingStatus.hashCode ^
      capacityLevel.hashCode ^
      batteryUsageNumber.hashCode ^
      batteryErrorStatus.hashCode ^
      batteryType.hashCode ^
      technology.hashCode ^
      batteryUsageDecommissionThreshold.hashCode ^
      iconSmall.hashCode ^
      maxChargingVoltage.hashCode ^
      zcmMode.hashCode ^
      shutdownLevel.hashCode ^
      zcmEnabled.hashCode ^
      maxDischargeTempShutdownLevel.hashCode ^
      maxChargingCurrent.hashCode ^
      adjustShutdownLevel.hashCode ^
      lowLevel.hashCode ^
      ratedCapacity.hashCode ^
      plugged.hashCode ^
      present.hashCode ^
      mfd.hashCode ^
      seq.hashCode ^
      zcmExtra.hashCode ^
      chargeCounter.hashCode ^
      level.hashCode ^
      baseCumulativeCharge.hashCode ^
      scale.hashCode ^
      partNumber.hashCode ^
      criticalLevel.hashCode ^
      minDischargeTempShutdownLevel.hashCode ^
      batteryLow.hashCode ^
      serialNumber.hashCode ^
      invalidCharger.hashCode;

  Map<String, dynamic> toJson() => {
        'batteryPercentage': batteryPercentage,
        'status': status.value,
        'health': health.value,
        'temperature': temperature,
        'voltage': voltage,
        'cycleCount': cycleCount,
        'chargingStatus': chargingStatus,
        'capacityLevel': capacityLevel.value,
        'batteryUsageNumber': batteryUsageNumber,
        'batteryErrorStatus': batteryErrorStatus,
        'batteryType': batteryType,
        'technology': technology,
        'batteryUsageDecommissionThreshold': batteryUsageDecommissionThreshold,
        'iconSmall': iconSmall,
        'maxChargingVoltage': maxChargingVoltage,
        'zcmMode': zcmMode,
        'shutdownLevel': shutdownLevel,
        'zcmEnabled': zcmEnabled,
        'maxDischargeTempShutdownLevel': maxDischargeTempShutdownLevel,
        'maxChargingCurrent': maxChargingCurrent,
        'adjustShutdownLevel': adjustShutdownLevel,
        'lowLevel': lowLevel,
        'ratedCapacity': ratedCapacity,
        'plugged': plugged,
        'present': present,
        'mfd': mfd?.toIso8601String(),
        'seq': seq,
        'zcmExtra': zcmExtra,
        'chargeCounter': chargeCounter,
        'level': level,
        'baseCumulativeCharge': baseCumulativeCharge,
        'scale': scale,
        'partNumber': partNumber,
        'criticalLevel': criticalLevel,
        'minDischargeTempShutdownLevel': minDischargeTempShutdownLevel,
        'batteryLow': batteryLow,
        'serialNumber': serialNumber,
        'invalidCharger': invalidCharger,
        'batteryDecommissionStatus': batteryDecommissionStatus?.value,
        'createdAt': createdAt.toIso8601String(),
      };

  @override
  String toString() =>
      'ExtendedBatteryStatus{batteryPercentage: $batteryPercentage, status: $status, health: $health, temperature: $temperature, voltage: $voltage, cycleCount: $cycleCount, chargingStatus: $chargingStatus, capacity: $capacityLevel, batteryUsageNumber: $batteryUsageNumber, batteryErrorStatus: $batteryErrorStatus, batteryType: $batteryType, technology: $technology, batteryUsageDecommissionThreshold: $batteryUsageDecommissionThreshold, iconSmall: $iconSmall, maxChargingVoltage: $maxChargingVoltage, zcmMode: $zcmMode, shutdownLevel: $shutdownLevel, zcmEnabled: $zcmEnabled, maxDischargeTempShutdownLevel: $maxDischargeTempShutdownLevel, maxChargingCurrent: $maxChargingCurrent, adjustShutdownLevel: $adjustShutdownLevel, lowLevel: $lowLevel, ratedCapacity: $ratedCapacity, plugged: $plugged, present: $present, mfd: $mfd, seq: $seq, zcmExtra: $zcmExtra, chargeCounter: $chargeCounter, level: $level, baseCumulativeCharge: $baseCumulativeCharge, scale: $scale, partNumber: $partNumber, criticalLevel: $criticalLevel, minDischargeTempShutdownLevel: $minDischargeTempShutdownLevel, batteryLow: $batteryLow, serialNumber: $serialNumber, invalidCharger: $invalidCharger, batteryDecommission: $batteryDecommission, totalCumulativeCharge: $totalCumulativeCharge, secondsSinceFirstUse: $secondsSinceFirstUse, presentCapacity: $presentCapacity, healthPercentage: $healthPercentage, timeToEmpty: $timeToEmpty, timeToFull: $timeToFull, presentCharge: $presentCharge, bkVoltage: $bkVoltage, createdAt: $createdAt}';
}

enum BatteryStatus {
  charging(2),
  discharging(3),
  full(5),
  notCharging(4),
  unknown(1),
  ;

  final int value;
  const BatteryStatus(this.value);

  static BatteryStatus fromInt(int value) => BatteryStatus.values.firstWhere((element) => element.value == value, orElse: () => BatteryStatus.unknown);
  bool get isCharging => this == BatteryStatus.charging || this == BatteryStatus.full;
}

enum BatteryHealth {
  cold(7),
  dead(4),
  good(2),
  overheat(3),
  overVoltage(5),
  unknown(1),
  unspecifiedFailure(6),
  ;

  final int value;
  const BatteryHealth(this.value);
  static BatteryHealth fromInt(int value) => BatteryHealth.values.firstWhere((element) => element.value == value, orElse: () => BatteryHealth.unknown);
  bool get isOK => this == BatteryHealth.good || this == BatteryHealth.cold || this == BatteryHealth.unknown; // Cold is given if fully charged
  @override
  String toString() => switch (this) {
        good => 'Good',
        cold => 'Cold',
        dead => 'Dead',
        overheat => 'Overheat',
        overVoltage => 'Over Voltage',
        unknown => 'Unknown',
        unspecifiedFailure => 'Unspecified Failure',
      };
}

enum BatteryDecommissionStatus {
  batteryGood(0),
  decommissionedBattery(1),
  unknown(2),
  notSupported(3),
  ;

  final int value;
  const BatteryDecommissionStatus(this.value);
  static BatteryDecommissionStatus fromInt(int value) => BatteryDecommissionStatus.values.firstWhere((element) => element.value == value, orElse: () => BatteryDecommissionStatus.unknown);
  @override
  String toString() => switch (this) {
        batteryGood => 'Battery Good',
        decommissionedBattery => 'Decommissioned Battery',
        unknown => 'Unknown',
        notSupported => 'Not Supported',
      };
}

enum BatteryPluggedStatus {
  ac,
  usb,
  wireless,
  unknown,
  ;

  static BatteryPluggedStatus fromInt(int value) => switch (value) {
        1 => BatteryPluggedStatus.ac,
        2 => BatteryPluggedStatus.usb,
        3 => BatteryPluggedStatus.wireless,
        _ => BatteryPluggedStatus.unknown,
      };
  @override
  String toString() => switch (this) {
        ac => 'AC',
        usb => 'USB',
        wireless => 'Wireless',
        unknown => 'Unknown',
      };
}

// -1: Unsupported, 0: Unknown, 1: Critical, 2: Low, 3: Normal, 4: High, 5: Full
enum BatteryCapacityLevel {
  unsupported(-1),
  unknown(0),
  critical(1),
  low(2),
  normal(3),
  high(4),
  full(5),
  ;

  final int value;
  const BatteryCapacityLevel(this.value);
  static BatteryCapacityLevel fromInt(int value) => BatteryCapacityLevel.values.firstWhere((element) => element.value == value, orElse: () => BatteryCapacityLevel.unsupported);
  @override
  String toString() => switch (this) {
        unknown => 'Unknown',
        critical => 'Critical',
        low => 'Low',
        normal => 'Normal',
        high => 'High',
        full => 'Full',
        unsupported => 'Unsupported',
      };
}
