/*{batteryPercentage=100, status=5, health=2, temperature=330, voltage=4326, cycleCount=0, chargingStatus=-1, capacity=0, batteryUsageNumber=18, batteryErrorStatus=0, batteryType=206, technology=Li-ion, batteryUsageDecommissionThreshold=400, iconSmall=17303637,
maxChargingVoltage=5000000, zcmMode=, shutdownLevel=4, zcmEnabled=false, maxDischargeTempShutdownLevel=600, maxChargingCurrent=475000, adjustShutdownLevel=100, lowLevel=18, ratedCapacity=3300, plugged=2, present=true, mfd=2023-02-02, seq=2074, zcmExtra=,
chargeCounter=3395080, level=100, baseCumulativeCharge=61149, scale=100, partNumber=BT-000409-50 R.B, criticalLevel=10, minDischargeTempShutdownLevel=-200, serialNumber=T5362, invalidCharger=0, batteryLow=false, batteryDecommission=0}*/

import 'dart:convert';
import 'dart:developer';

class ExtendedBatteryStatus {
  final BatteryStatus status;
  final BatteryHealth health;
  final BatteryCapacityLevel capacityLevel;
  final double temperature, voltage;
  final String technology, deviceManufacturer;
  final bool present, batteryLow;
  final int cycleCount, chargingStatus, plugged, chargeCounter, level, scale, seq, maxChargingVoltage, maxChargingCurrent, iconSmall, invalidCharger;
  final Map<String, dynamic>? extraMap;
  final String? intentLog;
  final DateTime createdAt = DateTime.now();
  // Zebra specific, null if not supported
  final int? batteryErrorStatus,
      batteryUsageDecommissionThreshold,
      shutdownLevel,
      batteryPercentDecommissionThreshold,
      chargingDuration,
      timeSinceLastCharge,
      lastChargeLength,
      timeRemaining,
      rebootCount,
      deviceOffDuration,
      deviceOnDuration,
      timeSinceLastFullCharge;
  final double? maxDischargeTempShutdownLevel, minDischargeTempShutdownLevel, minBatteryTemp, maxBatteryTemp;
  final int? adjustShutdownLevel, lowLevel;
  final int? criticalLevel;
  // Below are only supported on Zebra devices
  final String? zcmMode, zcmExtra; // '' if Zebra device but not supported
  final bool? zcmEnabled;
  final int? batteryType;
  // Below are only supported on Power Precision devices
  final int? batteryUsageNumber, ratedCapacity, baseCumulativeCharge, batteryDecommission;
  final String? partNumber, serialNumber; // '' if Zebra device but not supported
  DateTime? mfd;
  // TC27
  final int? backupPowerType;
  final bool? usbLiquidStatus;
  // Below are only supported on Power Precision Plus devices
  final int? totalCumulativeCharge, secondsSinceFirstUse, healthPercentage, timeToEmpty, timeToFull, presentCharge, presentCapacity;
  // Below are only supported on devices with backup battery (Honeywell & Zebra)
  final int? backupBatteryVoltage;
  // Honeywell supported
  final bool? batterySwapping;
  // TC57
  final int? currentEvent, miscEvent, online, pogoPlugged, capacity, currentNow, chargeType, chargerType;
  final bool? hvCharger, powerSharing;
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
    required this.batteryLow,
    required this.seq,
    required this.iconSmall,
    required this.maxChargingVoltage,
    required this.maxChargingCurrent,
    required this.invalidCharger,
    required this.deviceManufacturer,
    // Zebra specific
    this.batteryUsageNumber,
    this.batteryErrorStatus,
    this.batteryType,
    this.batteryUsageDecommissionThreshold,
    this.zcmMode,
    this.shutdownLevel,
    this.zcmEnabled,
    this.maxDischargeTempShutdownLevel,
    this.adjustShutdownLevel,
    this.lowLevel,
    this.ratedCapacity,
    this.mfd,
    this.zcmExtra,
    this.baseCumulativeCharge,
    this.partNumber,
    this.criticalLevel,
    this.minDischargeTempShutdownLevel,
    this.serialNumber,
    this.batteryDecommission,
    this.totalCumulativeCharge,
    this.secondsSinceFirstUse,
    this.presentCapacity,
    this.healthPercentage,
    this.timeToEmpty,
    this.timeToFull,
    this.presentCharge,
    this.currentEvent,
    this.miscEvent,
    this.online,
    this.pogoPlugged,
    this.capacity,
    this.currentNow,
    this.chargeType,
    this.hvCharger,
    this.powerSharing,
    this.chargerType,
    this.minBatteryTemp,
    this.maxBatteryTemp,
    this.batteryPercentDecommissionThreshold,
    this.chargingDuration,
    this.timeSinceLastCharge,
    this.lastChargeLength,
    this.timeRemaining,
    this.rebootCount,
    this.deviceOffDuration,
    this.deviceOnDuration,
    this.timeSinceLastFullCharge,
    this.backupPowerType,
    this.usbLiquidStatus,

    // Honeywell & Zebra specific
    this.backupBatteryVoltage,
    // Honeywell specific
    this.batterySwapping,
    this.extraMap,
    this.intentLog,
  });
  BatteryDecommissionStatus? get batteryDecommissionStatus => batteryDecommission == null ? null : BatteryDecommissionStatus.fromInt(batteryDecommission!);
  bool get isZebra => deviceManufacturer.isEmpty || deviceManufacturer == 'ZEBRA';
  bool get isHoneywell => deviceManufacturer == 'HONEYWELL';
  int? get calculatedBatteryUsage => batteryUsageNumber ?? ((totalCumulativeCharge == null || (ratedCapacity ?? 0) == 0) ? null : totalCumulativeCharge! ~/ ratedCapacity!);
  int? get batteryDecommissionPercentageLeft {
    if (healthPercentage != null && batteryPercentDecommissionThreshold != null && batteryPercentDecommissionThreshold! != 100) {
      return (((healthPercentage! - batteryPercentDecommissionThreshold!) / (100 - batteryPercentDecommissionThreshold!)) * 100).toInt();
    }
    final calculated = calculatedBatteryUsage;
    if (calculated == null || (batteryUsageDecommissionThreshold ?? -1) < 1) return batteryPercentDecommissionThreshold;
    return 100 - (calculated * 100 ~/ batteryUsageDecommissionThreshold!);
  }

  static int? tryToCalculateBatteryDecommissionThreshold(Map<String, dynamic> json) =>
      switch (json['deviceManufacturer']) { 'ZEBRA' => switch (json['batteryType']) {  200 || 201 || 206 => 400, _ => null}, _ => null };

  int get batteryPercentage => scale <= 0 ? -1 : ((level / scale) * 100).toInt();

  BatteryPluggedStatus get pluggedStatus => BatteryPluggedStatus.fromInt(plugged);
  factory ExtendedBatteryStatus.fromJson(Map<String, dynamic> json) {
    try {
      // debugPrint('ExtendedBatteryStatus.fromJson: $json');
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
        batteryLow: json['batteryLow'] == true,
        seq: json['seq'] ?? -1,
        iconSmall: json['iconSmall'] ?? -1,
        maxChargingVoltage: json['maxChargingVoltage'] ?? -1,
        maxChargingCurrent: json['maxChargingCurrent'] ?? -1,
        invalidCharger: json['invalidCharger'] ?? -1,
        deviceManufacturer: json['deviceManufacturer'] ?? '',

        // Zebra specific
        batteryUsageNumber: json['batteryUsageNumber'],
        batteryErrorStatus: json['batteryErrorStatus'],
        batteryType: json['batteryType'],
        batteryUsageDecommissionThreshold: json['batteryUsageDecommissionThreshold'] ?? tryToCalculateBatteryDecommissionThreshold(json),
        zcmMode: json['zcmMode'],
        shutdownLevel: json['shutdownLevel'],
        zcmEnabled: json['zcmEnabled'],
        maxDischargeTempShutdownLevel: json['maxDischargeTempShutdownLevel'],
        adjustShutdownLevel: json['adjustShutdownLevel'],
        lowLevel: json['lowLevel'],
        ratedCapacity: json['ratedCapacity'],
        mfd: json['mfd'] == null ? null : DateTime.tryParse(json['mfd']),
        zcmExtra: json['zcmExtra'],
        baseCumulativeCharge: json['baseCumulativeCharge'],
        partNumber: json['partNumber'],
        criticalLevel: json['criticalLevel'],
        minDischargeTempShutdownLevel: json['minDischargeTempShutdownLevel'],
        serialNumber: json['serialNumber'],
        batteryDecommission: json['batteryDecommission'],
        totalCumulativeCharge: json['totalCumulativeCharge'],
        secondsSinceFirstUse: json['secondsSinceFirstUse'],
        presentCapacity: json['presentCapacity'],
        healthPercentage: json['healthPercentage'],
        timeToEmpty: json['timeToEmpty'],
        timeToFull: json['timeToFull'],
        presentCharge: json['presentCharge'],
        minBatteryTemp: json['minBatteryTemp']?.toDouble(),
        maxBatteryTemp: json['maxBatteryTemp']?.toDouble(),
        batteryPercentDecommissionThreshold: json['batteryPercentDecommissionThreshold'],
        chargingDuration: json['chargingDuration'],
        timeSinceLastCharge: json['timeSinceLastCharge'],
        lastChargeLength: json['lastChargeLength'],
        timeRemaining: json['timeRemaining'],
        rebootCount: json['rebootCount'],
        deviceOffDuration: json['deviceOffDuration'],
        deviceOnDuration: json['deviceOnDuration'],
        timeSinceLastFullCharge: json['timeSinceLastFullCharge'],
        backupPowerType: json['backupPowerType'],
        usbLiquidStatus: json['usbLiquidStatus'],

        // Honeywell & Zebra specific
        backupBatteryVoltage: json['backupBatteryVoltage'],

        // Samsung specific
        currentEvent: json['currentEvent'],
        miscEvent: json['miscEvent'],
        online: json['online'],
        pogoPlugged: json['pogoPlugged'],
        capacity: json['capacity'],
        currentNow: json['currentNow'],
        chargeType: json['chargeType'],
        hvCharger: json['hvCharger'],
        powerSharing: json['powerSharing'],
        chargerType: json['chargerType'],

        //Honeywell specific
        batterySwapping: json['batterySwapping'],

        extraMap: json['extraMap'] == null ? null : Map<String, dynamic>.from(json['extraMap']),
        intentLog: json['intentLog'],
      );
    } catch (e, s) {
      log('ExtendedBatteryStatus.fromJson, $json', error: e, stackTrace: s);
      rethrow;
    }
  }
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
        other.batterySwapping == batterySwapping &&
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
      batterySwapping.hashCode ^
      invalidCharger.hashCode;

  Map<String, dynamic> toJson({bool sendIntentLog = true}) => {
        'batteryPercentage': batteryPercentage,
        'status': status.value,
        'health': health.value,
        'temperature': temperature,
        'voltage': voltage,
        'cycleCount': cycleCount,
        'chargingStatus': chargingStatus,
        'capacityLevel': capacityLevel.value,
        'technology': technology,
        'deviceManufacturer': deviceManufacturer,
        if (batteryUsageNumber != null) 'batteryUsageNumber': batteryUsageNumber,
        if (batteryErrorStatus != null) 'batteryErrorStatus': batteryErrorStatus,
        if (batteryType != null) 'batteryType': batteryType,
        if (batteryUsageDecommissionThreshold != null) 'batteryUsageDecommissionThreshold': batteryUsageDecommissionThreshold,
        'iconSmall': iconSmall,
        'maxChargingVoltage': maxChargingVoltage,
        if (zcmMode != null) 'zcmMode': zcmMode,
        if (shutdownLevel != null) 'shutdownLevel': shutdownLevel,
        if (zcmEnabled != null) 'zcmEnabled': zcmEnabled,
        if (maxDischargeTempShutdownLevel != null) 'maxDischargeTempShutdownLevel': maxDischargeTempShutdownLevel,
        'maxChargingCurrent': maxChargingCurrent,
        if (adjustShutdownLevel != null) 'adjustShutdownLevel': adjustShutdownLevel,
        if (lowLevel != null) 'lowLevel': lowLevel,
        if (ratedCapacity != null) 'ratedCapacity': ratedCapacity,
        'plugged': plugged,
        'present': present,
        if (mfd != null) 'mfd': mfd?.toIso8601String(),
        'seq': seq,
        if (zcmExtra != null) 'zcmExtra': zcmExtra,
        'chargeCounter': chargeCounter,
        'level': level,
        if (baseCumulativeCharge != null) 'baseCumulativeCharge': baseCumulativeCharge,
        if (totalCumulativeCharge != null) 'totalCumulativeCharge': totalCumulativeCharge,
        'scale': scale,
        if (partNumber != null) 'partNumber': partNumber,
        if (criticalLevel != null) 'criticalLevel': criticalLevel,
        if (minDischargeTempShutdownLevel != null) 'minDischargeTempShutdownLevel': minDischargeTempShutdownLevel,
        'batteryLow': batteryLow,
        if (serialNumber != null) 'serialNumber': serialNumber,
        'invalidCharger': invalidCharger,
        if (batteryDecommissionStatus != null) 'batteryDecommissionStatus': batteryDecommissionStatus?.value,
        if (batterySwapping != null) 'batterySwapping': batterySwapping,
        if (backupBatteryVoltage != null) 'backupBatteryVoltage': backupBatteryVoltage,
        if (currentEvent != null) 'currentEvent': currentEvent,
        if (miscEvent != null) 'miscEvent': miscEvent,
        if (online != null) 'online': online,
        if (pogoPlugged != null) 'pogoPlugged': pogoPlugged,
        if (capacity != null) 'capacity': capacity,
        if (currentNow != null) 'currentNow': currentNow,
        if (chargeType != null) 'chargeType': chargeType,
        if (hvCharger != null) 'hvCharger': hvCharger,
        if (powerSharing != null) 'powerSharing': powerSharing,
        if (chargerType != null) 'chargerType': chargerType,
        if (extraMap?.isNotEmpty == true) 'extraMap': extraMap,
        if (minBatteryTemp != null) 'minBatteryTemp': minBatteryTemp,
        if (maxBatteryTemp != null) 'maxBatteryTemp': maxBatteryTemp,
        if (batteryPercentDecommissionThreshold != null) 'batteryPercentDecommissionThreshold': batteryPercentDecommissionThreshold,
        if (chargingDuration != null) 'chargingDuration': chargingDuration,
        if (timeSinceLastCharge != null) 'timeSinceLastCharge': timeSinceLastCharge,
        if (lastChargeLength != null) 'lastChargeLength': lastChargeLength,
        if (timeRemaining != null) 'timeRemaining': timeRemaining,
        if (rebootCount != null) 'rebootCount': rebootCount,
        if (deviceOffDuration != null) 'deviceOffDuration': deviceOffDuration,
        if (deviceOnDuration != null) 'deviceOnDuration': deviceOnDuration,
        if (timeSinceLastFullCharge != null) 'timeSinceLastFullCharge': timeSinceLastFullCharge,
        if (backupPowerType != null) 'backupPowerType': backupPowerType,
        if (usbLiquidStatus != null) 'usbLiquidStatus': usbLiquidStatus,
        if (presentCapacity != null) 'presentCapacity': presentCapacity,
        if (healthPercentage != null) 'healthPercentage': healthPercentage,
        if (timeToEmpty != null) 'timeToEmpty': timeToEmpty,
        if (timeToFull != null) 'timeToFull': timeToFull,
        if (presentCharge != null) 'presentCharge': presentCharge,
        'createdAt': createdAt.toIso8601String(),
        if (sendIntentLog && intentLog != null) 'intentLog': intentLog,
      };
  String jsonEncoded({bool sendIntentLog = false}) => jsonEncode(toJson(sendIntentLog: sendIntentLog), toEncodable: (dynamic value) {
        if (value is DateTime) return value.toIso8601String();
        return value.toString();
      });
  @override
  String toString() =>
      'ExtendedBatteryStatus{deviceManufacturer: $deviceManufacturer, batteryPercentage: $batteryPercentage, status: $status, health: $health, temperature: $temperature, voltage: $voltage, cycleCount: $cycleCount, chargingStatus: $chargingStatus, capacity: $capacityLevel, batteryUsageNumber: $batteryUsageNumber, batteryErrorStatus: $batteryErrorStatus, batteryType: $batteryType, technology: $technology, batteryUsageDecommissionThreshold: $batteryUsageDecommissionThreshold, iconSmall: $iconSmall, maxChargingVoltage: $maxChargingVoltage, zcmMode: $zcmMode, shutdownLevel: $shutdownLevel, zcmEnabled: $zcmEnabled, maxDischargeTempShutdownLevel: $maxDischargeTempShutdownLevel, maxChargingCurrent: $maxChargingCurrent, adjustShutdownLevel: $adjustShutdownLevel, lowLevel: $lowLevel, ratedCapacity: $ratedCapacity, plugged: $plugged, present: $present, mfd: $mfd, seq: $seq, zcmExtra: $zcmExtra, chargeCounter: $chargeCounter, level: $level, baseCumulativeCharge: $baseCumulativeCharge, scale: $scale, partNumber: $partNumber, criticalLevel: $criticalLevel, minDischargeTempShutdownLevel: $minDischargeTempShutdownLevel, batteryLow: $batteryLow, serialNumber: $serialNumber, invalidCharger: $invalidCharger, batteryDecommission: $batteryDecommission, totalCumulativeCharge: $totalCumulativeCharge, secondsSinceFirstUse: $secondsSinceFirstUse, presentCapacity: $presentCapacity, healthPercentage: $healthPercentage, timeToEmpty: $timeToEmpty, timeToFull: $timeToFull, presentCharge: $presentCharge, backupBatteryVoltage: $backupBatteryVoltage, batterySwapping: $batterySwapping, createdAt: $createdAt, extraMap: $extraMap}';
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
  @override
  String toString() => switch (this) {
        charging => 'Charging',
        discharging => 'Discharging',
        full => 'Full',
        notCharging => 'Not Charging',
        unknown => 'Unknown',
      };
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
  bool get isDecommissioned => this == BatteryDecommissionStatus.decommissionedBattery;
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
