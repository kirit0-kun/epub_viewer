import 'bookmark_data.dart';
import 'enum/bookmark_action.dart';

class BookmarkChange {
  final BookmarkData bookmark;
  final BookmarkAction action;

  const BookmarkChange(this.bookmark, this.action);
}
