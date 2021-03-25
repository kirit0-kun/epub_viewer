/// enum from scrollDirection to make it easier for users
enum HighlightAction { NEW, DELETE, MODIFY }

HighlightAction getAction(String action) {
  if (action == 'new') {
    return HighlightAction.NEW;
  } else if (action == 'delete') {
    return HighlightAction.DELETE;
  } else if (action == 'modify') {
    return HighlightAction.MODIFY;
  }
  return null;
}
