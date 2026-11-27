// Asks StoreKit for the App Store rating prompt on the active window scene.
// Called from rating_ask_extension.c (RatingAskIOS.request()).
//
// Apple decides whether the prompt actually shows: at most three times in 365
// days per app, never in TestFlight, and never if the person turned
// "In-App Ratings & Reviews" off. Nothing comes back to say whether it showed.

import StoreKit
import UIKit

@_cdecl("rating_ask_request")
public func ratingAskRequest() {
	// Godot calls in on the main thread; hop through the main actor anyway so
	// the UIKit and StoreKit calls are isolated correctly whatever the caller.
	Task { @MainActor in
		let scenes = UIApplication.shared.connectedScenes.compactMap { $0 as? UIWindowScene }
		guard let scene = scenes.first(where: { $0.activationState == .foregroundActive }) else {
			return  // not in the foreground: skip rather than queue a prompt
		}
		AppStore.requestReview(in: scene)
	}
}
