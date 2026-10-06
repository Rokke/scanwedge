/// When the Newland scanner decodes. `continuous` and `pulse` are confirmed on a device; `level` and
/// `delay` are the handbook's values for the same setting.
enum NewlandTriggerMode {
  level, // = 0
  continuous, // = 1
  pulse, // = 2, the device default
  delay, // = 4
}
