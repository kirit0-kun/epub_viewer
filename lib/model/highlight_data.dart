import 'package:intl/intl.dart';
import 'package:json_annotation/json_annotation.dart';

part 'highlight_data.g.dart';

@JsonSerializable(fieldRename: FieldRename.none)
class HighlightData {
  final String bookId;
  final String content;
  @JsonKey(toJson: toFolioDateFormat, fromJson: fromFolioDate)
  final DateTime date;
  final String type;
  final int pageNumber;
  final String pageId;
  final String rangy;
  final String uuid;
  final String note;

  HighlightData(
      {this.bookId,
      this.content,
      this.date,
      this.type,
      this.pageNumber,
      this.pageId,
      this.rangy,
      this.uuid,
      this.note});

  factory HighlightData.fromJson(Map<String, dynamic> json) =>
      _$HighlightDataFromJson(json);
  Map<String, dynamic> toJson() => _$HighlightDataToJson(this);
}

String toFolioDateFormat(DateTime dateTime) {
  if (dateTime == null) return null;
  final DateFormat formatter = DateFormat('MMM dd, yyyy | HH:mm');
  final String formatted = formatter.format(dateTime);
  return formatted;
}

DateTime fromFolioDate(String json) {
  if (json?.isNotEmpty != true) return null;
  final DateFormat formatter = DateFormat('MMM dd, yyyy | HH:mm');
  final DateTime formatted = formatter.parseLoose(json);
  return formatted;
}
