import 'package:json_annotation/json_annotation.dart';

import 'highlight_data.dart';

part 'bookmark_data.g.dart';

@JsonSerializable(fieldRename: FieldRename.none)
class BookmarkData {
  final String bookId;
  @JsonKey(toJson: toFolioDateFormat, fromJson: fromFolioDate)
  final DateTime date;
  final String name;
  final String location;
  final String uuid;

  const BookmarkData(
      {this.bookId, this.date, this.name, this.location, this.uuid});

  factory BookmarkData.fromJson(Map<String, dynamic> json) =>
      _$BookmarkDataFromJson(json);
  Map<String, dynamic> toJson() => _$BookmarkDataToJson(this);
}
