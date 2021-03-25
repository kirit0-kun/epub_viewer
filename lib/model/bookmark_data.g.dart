// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'bookmark_data.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

BookmarkData _$BookmarkDataFromJson(Map<String, dynamic> json) {
  return BookmarkData(
    bookId: json['bookId'] as String,
    date: fromFolioDate(json['date'] as int),
    name: json['name'] as String,
    location: json['location'] as String,
    uuid: json['uuid'] as String,
  );
}

Map<String, dynamic> _$BookmarkDataToJson(BookmarkData instance) =>
    <String, dynamic>{
      'bookId': instance.bookId,
      'date': toFolioDateFormat(instance.date),
      'name': instance.name,
      'location': instance.location,
      'uuid': instance.uuid,
    };
