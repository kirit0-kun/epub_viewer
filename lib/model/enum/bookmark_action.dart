/// enum from scrollDirection to make it easier for users
enum BookmarkAction { NEW, DELETE, MODIFY }

BookmarkAction getBookmarkAction(String action) {
  if (action == 'new') {
    return BookmarkAction.NEW;
  } else if (action == 'delete') {
    return BookmarkAction.DELETE;
  } else if (action == 'modify') {
    return BookmarkAction.MODIFY;
  }
  return null;
}
