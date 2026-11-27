# Run Hero Run! 1.2: listing and App Review notes (build 6)

Status: version 1.2 (iOS) created in App Store Connect on 2026-11-26 via the API (appStoreVersion 75d518b9-3625-419f-afd7-2d84086d5b1a). The text below (description, keywords, promotional text, What's New, subtitle, review notes) is pushed to it. Left for Crane: age rating, App Privacy, privacy and support URLs (the version still carries the 1.1 support URL), and submitting. Builds on RELEASE-PLAN-2026-11.md §6–7, updated for build 6 (first-run lessons with a why on every miss, the grey ghost, game over names what hit you).

## Store fields (en-US)
- **Name:** Run Hero Run! (unchanged)
- **Subtitle (30):** Stomp Knights, Outrun a Dragon
- **Promotional text:** Ten years later, the hero runs again: rebuilt for today's iPhones, with the dragon boss that was cut in 2015.
- **Keywords (Astro 2026-11-27, pushed to 1.2):** runner,endless,jump,quest,medieval,wizard,8bit,boss,ninja,arcade,retro,one tap,offline,platformer (97/100). stomp, knight, dragon moved to the subtitle; freed space: quest (pop 58 / diff 47), medieval (43/54), 8bit (30/39). Passed on castle (59/70), pixel (60/81), dungeon (47/51).
- **Description:**
  ```
  One tap to jump, two to leap higher. Land on a knight or a wizard and they're gone; touch one any other way and the run is over.

  Your first run teaches itself: a ring and a few words show you when to jump and where to land, every miss tells you why, and a ghost shows the double jump before you try it. After that, it's just you and the road.

  Chain stomps without touching the ground for up to x4 points. Grab orbs for a shield, slow motion, double points or a soul magnet. Let the grey ghost escape and it calls the Devourer: feed its jaw the souls of the enemies you stomp before it drops on you.

  Run through four stages, from open sky to the dragon's lands, and beat your best distance.

  • Rebuilt from the ground up for current iPhones, full screen in landscape
  • The Devourer boss, cut from the original release, is finally in
  • No ads, no accounts, no tracking. Plays offline
  ```
- **What's New (1.2):** Rebuilt for today's iPhones: full-screen play, a first run that teaches itself, the Devourer boss, combos, power-ups, a new enemy and four stages. No more ads.
- **Category:** Games > Action (primary), Games > Arcade (secondary)
- **Support / privacy:** https://curiouscrane.dev/runherorun/ and /privacy/ (pages not published yet; manual app-sites push of the new folder only)

## App Review notes
```
Run Hero Run! is an offline one-tap side-scrolling runner, first released in 2015. This update rebuilds it for current iPhones (full-screen landscape on both sides, safe areas respected) and adds the Devourer boss, combos, power-ups and new stages.

How to play: tap anywhere on the title to start. The first run teaches the controls in place: a ring and a short line ("Tap to jump", "Land on it", "Tap again in the air", "Stop the ghost"), every miss says why ("Tap sooner", "Land on top"), and the first enemies are harmless until you land a stomp. The "?" button on the title replays these lessons. If the grey floating ghost leaves the screen alive, the Devourer boss appears (usually within 1–2 minutes). Game over names what hit you. Pause is at the top right; the game also pauses when the app goes to the background.

No accounts, ads, in-app purchases or network use; no data is collected. It is the only action game on the account; the account's other apps (for example Tellday, Pour Plan, Endcard) are trackers and tools.
```
demoAccountRequired = false

## Screenshots (6.9" landscape, 2868x1320)
Five plain captures from the windowed playtest (build 6 code) in `shots-69/`, in upload order: 01_title, 02_rush, 03_stage2, 04_boss, 05_stomp. Sonnet shot-check: no cut-off or placeholder problems; 05 is the weakest (a lesson frame); a later pass could swap in a pig or combo moment. Upload set: APP_IPHONE_67 on the 1.2 en-US localization (the orchestrator runs the replace command).
