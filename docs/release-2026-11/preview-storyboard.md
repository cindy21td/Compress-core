# Run Hero Run 1.2: App Store preview storyboard

Spec: mobile-apps `pipeline/TODO.md` "App preview videos" (2026-11-27). Baseline 2,774 impressions → 42 page views (1.5%); the clip worked if that rises above 3% 14 days after 1.2 goes live.

Rules: 15–20 s, the Devourer (dragon boss) on screen inside the first 2 s, then knight stomps; no logo intro, no device frame or hands, footage of the game only (2.3.4); no added text beyond the game's own banners; poster frame = the signature moment.

## Shots (times in the cut, 15.5 s)

| Time | On screen | Why |
|---|---|---|
| 0.0–2.0 s | Mid-run, the Devourer's head and burning jaw fill the top left, its health bar full; the ninja runs under it, knights and a wizard ahead | In search the opening frames replace the first screenshot; the boss is what no other runner has |
| 2–13.5 s | The chase: the ninja stomps knights and wizards; each stomp's soul flies into the dragon (+2, ring), the bar drains, score climbs 4 → 42, stage 2 backdrop rolls in | The one-tap verb and how it fights the boss |
| 13.5–15.5 s | "DEVOURER DOWN +20", the dragon drops away, a COMBO x2 stomp to close | Payoff; ends on play, not a logo |

The only text is the game's own HUD and banners; nothing added in the edit. Cut before the run's invincible pass-through could show (the take has collisions off).

Poster frame: 4.2 s (time code 00:00:04:06), a stomp under the dragon: star burst, soul rising, score turning gold.

## Takes

Take 3 is the cut (frames 335–799 of the Movie Maker run, marks printed by the script). Take 1 opened with only the dragon's snout at the edge; take 2's pre-roll ran long and the dragon dropped before the cut. The pre-roll keeps souls off the dragon for ~5 s so it has crept in close when the clip opens; from the first frame of the cut every soul lands as in normal play.

File: `preview-6.9/RunHeroRun-preview-6.9.mov`, 1920x886 landscape (Apple's 6.9" app preview size, also used for 6.5"), 30 fps, H.264 High 11 Mb/s, stereo AAC 48 kHz 256 kb/s game audio, 19 MB. Uploaded to App Store Connect 1.2 en-US, preview set IPHONE_67.

## How it is captured

- `godot/tests/preview_take.tscn` plays the real game (`main.tscn`) with scripted taps (the boss tour's stomp rule), lessons marked learned, collisions off so a missed tap doesn't end the take, and the Summoner sent in after a 5 s run-in, souls kept off the Devourer in the pre-roll. `tests/*` is excluded from the iOS export, so none of this ships.
- Recorded with Godot Movie Maker at 1920x886 (Apple's 6.9" landscape preview size), fixed 30 fps, game audio, on the Mobile renderer the device build uses. Not the simulator: the simulator only runs the gl_compatibility export, which draws a white ground strip that devices don't show (build 6 note).
- Cut and encoded with ffmpeg: H.264 High, 30 fps, yuv420p, stereo AAC 48 kHz 256 kb/s, .mov.
