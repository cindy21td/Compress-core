## The App Store rating prompt, through the RatingAsk GDExtension
## (res://addons/rating_ask, source in res://native/rating_ask).
## Does nothing off iOS, in debug builds (the editor, the headless tests, a
## development export) or when the extension is missing. RatingPolicy decides
## when to call it; Apple decides whether the prompt actually appears.
class_name RatingAsk
extends RefCounted

const NATIVE_CLASS := "RatingAskIOS"


## True when the request went to StoreKit (not whether Apple showed it).
static func request() -> bool:
	if not available():
		return false
	ClassDB.class_call_static(NATIVE_CLASS, "request")
	return true


static func available() -> bool:
	return OS.has_feature("ios") and not OS.is_debug_build() and ClassDB.class_exists(NATIVE_CLASS)
