import 'dart:convert';
import 'dart:io';

import 'package:epub_viewer/model/highlight_data.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:path/path.dart';
import 'package:path_provider/path_provider.dart';

import 'model/bookmark_change.dart';
import 'model/bookmark_data.dart';
import 'model/enum/bookmark_action.dart';
import 'model/enum/highlight_action.dart';
import 'model/highlight_change.dart';

part 'model/enum/epub_scroll_direction.dart';
part 'model/epub_locator.dart';
part 'utils/util.dart';

class EpubViewer {
  static const MethodChannel _channel = const MethodChannel('epub_viewer');
  static const EventChannel _pageChannel = const EventChannel('page');
  static const EventChannel _highlightsChannel =
      const EventChannel('highlights');
  static const EventChannel _highlightChangesChannel =
      const EventChannel('highlightsChanges');
  static const EventChannel _bookmarksChangesChannel =
      const EventChannel('bookmarksChanges');

  /// Configure Viewer's with available values
  ///
  /// themeColor is the color of the reader
  /// scrollDirection uses the [EpubScrollDirection] enum
  /// allowSharing
  /// enableTts is an option to enable the inbuilt Text-to-Speech
  static void setConfig(
      {Color themeColor = Colors.blue,
      String identifier = 'book',
      bool nightMode = false,
      EpubScrollDirection scrollDirection = EpubScrollDirection.ALLDIRECTIONS,
      bool allowSharing = false,
      bool enableTts = false}) async {
    Map<String, dynamic> agrs = {
      "identifier": identifier,
      "themeColor": Util.getHexFromColor(themeColor),
      "scrollDirection": Util.getDirection(scrollDirection),
      "allowSharing": allowSharing,
      'enableTts': enableTts,
      'nightMode': nightMode
    };
    await _channel.invokeMethod('setConfig', agrs);
  }

  /// bookPath should be a local file.
  /// Last location is only available for android.
  static void open(String bookPath, String bookId,
      {List<HighlightData> highlights, EpubLocator lastLocation}) async {
    Map<String, dynamic> agrs = {
      "bookPath": bookPath,
      "bookId": bookId,
      'highlights': highlights == null
          ? '[]'
          : jsonEncode(highlights.map((e) => e.toJson()).toList()),
      'lastLocation':
          lastLocation == null ? '' : jsonEncode(lastLocation.toJson()),
    };
    await _channel.invokeMethod('open', agrs);
  }

  /// bookPath should be an asset file path.
  /// Last location is only available for android.
  static Future openAsset(String bookPath, String bookId,
      {List<HighlightData> highlights, EpubLocator lastLocation}) async {
    if (extension(bookPath) == '.epub') {
      Map<String, dynamic> agrs = {
        "bookPath": (await Util.getFileFromAsset(bookPath)).path,
        "bookId": bookId,
        'highlights': highlights == null
            ? '[]'
            : jsonEncode(highlights.map((e) => e.toJson()).toList()),
        'lastLocation':
            lastLocation == null ? '' : jsonEncode(lastLocation.toJson()),
      };
      await _channel.invokeMethod('open', agrs);
    } else {
      throw ('${extension(bookPath)} cannot be opened, use an EPUB File');
    }
  }

  /// Stream to get EpubLocator for android and pageNumber for iOS
  static Stream<EpubLocator> get locatorStream {
    Stream<EpubLocator> pageStream = _pageChannel.receiveBroadcastStream().map(
        (value) => Platform.isAndroid
            ? EpubLocator.fromJson(jsonDecode(value))
            : EpubLocator());
    return pageStream;
  }

  /// Stream to get EpubLocator for android and pageNumber for iOS
  static Stream<List<HighlightData>> get highlightsStream {
    Stream<List<HighlightData>> highlightsStream = _highlightsChannel
        .receiveBroadcastStream()
        .where((event) => event is String)
        .cast<String>()
        .asyncMap((value) async {
      final json = await compute(jsonDecode, value) as List;
      return json.map((e) => HighlightData.fromJson(e)).toList();
    });
    return highlightsStream;
  }

  static Stream<HighlightChange> get highlightChangesChannel {
    Stream<HighlightChange> highlightsStream = _highlightChangesChannel
        .receiveBroadcastStream()
        .where((event) => event is String)
        .cast<String>()
        .asyncMap((value) async {
      final json = await compute(jsonDecode, value);
      final actionString = json['action'];
      final action = getHighlightAction(actionString);
      final highlightMap = json['highlight'];
      final highlight = HighlightData.fromJson(highlightMap);
      return HighlightChange(highlight, action);
    });
    return highlightsStream;
  }

  static Stream<BookmarkChange> get bookmarksChangesChannel {
    Stream<BookmarkChange> highlightsStream = _bookmarksChangesChannel
        .receiveBroadcastStream()
        .where((event) => event is String)
        .cast<String>()
        .asyncMap((value) async {
      final json = await compute(jsonDecode, value);
      final actionString = json['action'];
      final action = getBookmarkAction(actionString);
      final bookmarkMap = json['bookmark'];
      final highlight = BookmarkData.fromJson(bookmarkMap);
      return BookmarkChange(highlight, action);
    });
    return highlightsStream;
  }
}
