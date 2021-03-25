// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'highlight_data.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

HighlightData _$HighlightDataFromJson(Map<String, dynamic> json) {
  return HighlightData(
    bookId: json['bookId'] as String,
    content: json['content'] as String,
    date: fromFolioDate(json['date'] as int),
    type: json['type'] as String,
    pageNumber: json['pageNumber'] as int,
    pageId: json['pageId'] as String,
    rangy: json['rangy'] as String,
    uuid: json['uuid'] as String,
    note: json['note'] as String,
  );
}

Map<String, dynamic> _$HighlightDataToJson(HighlightData instance) =>
    <String, dynamic>{
      'bookId': instance.bookId,
      'content': instance.content,
      'date': toFolioDateFormat(instance.date),
      'type': instance.type,
      'pageNumber': instance.pageNumber,
      'pageId': instance.pageId,
      'rangy': instance.rangy,
      'uuid': instance.uuid,
      'note': instance.note,
    };
