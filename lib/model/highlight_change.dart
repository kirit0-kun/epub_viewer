import 'package:epub_viewer/model/highlight_data.dart';
import 'package:epub_viewer/model/enum/highlight_action.dart';
import 'package:intl/intl.dart';
import 'package:json_annotation/json_annotation.dart';

class HighlightChange {
  final HighlightData highlight;
  final HighlightAction action;

  const HighlightChange(this.highlight, this.action);
}
