package ui

abstract class OnVisibilityChangeListener {
  open fun onVisible() {}
  open fun onHidden() {}
  open fun onGone() {}
}