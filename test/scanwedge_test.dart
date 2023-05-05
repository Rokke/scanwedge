void main() {
  final lst = List.generate(5, (index) => 'list$index');
  final map = {'1': '2'};
  print('map: $map');
  final tst = {for (var element in lst) element: 'false'};
  final map2 = lst.map((e) => {e: 'true'}).toList();
  print('map2: $map2');
  print('map2: ${lst.toSet()}');
  final mapny = {'test': '2', ...tst};
  print('mapny: $mapny');
}
