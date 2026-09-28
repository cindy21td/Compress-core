#!/usr/bin/env python3
"""Builds parallax layers from the original background art in ../art-src
(recovered from the 2016 Drive backup) into assets/art.

  sky.png          clear sky (clouds and hills removed and filled in)
  cloud_N.png      each cloud as its own sprite
  hills.png        hills only, transparent elsewhere
  ground.png       the ground strip the hero runs on
  stage_N.png      stage backdrops (bg_N + bg_N_r side by side), ground removed

Needs Pillow and numpy. Usage: python3 tools/build_art.py
"""
from collections import deque
from pathlib import Path

import numpy as np
from PIL import Image

ROOT = Path(__file__).resolve().parent.parent
SRC = ROOT.parent / "art-src"
OUT = ROOT / "assets" / "art"
GROUND_TOP = 482  # first row of the ground strip in every background


def load(name):
    return np.asarray(Image.open(SRC / name).convert("RGBA")).copy()


def save(arr, name):
    Image.fromarray(arr).save(OUT / name, optimize=True)


def flood(passable, seeds):
    h, w = passable.shape
    seen = np.zeros_like(passable)
    q = deque()
    for y, x in seeds:
        if passable[y, x] and not seen[y, x]:
            seen[y, x] = True
            q.append((y, x))
    while q:
        y, x = q.popleft()
        for ny, nx in ((y + 1, x), (y - 1, x), (y, x + 1), (y, x - 1)):
            if 0 <= ny < h and 0 <= nx < w and passable[ny, nx] and not seen[ny, nx]:
                seen[ny, nx] = True
                q.append((ny, nx))
    return seen


def components(mask):
    """Labels 4-connected regions of mask. Returns list of boolean masks."""
    remaining = mask.copy()
    out = []
    ys, xs = np.nonzero(remaining)
    for y, x in zip(ys, xs):
        if remaining[y, x]:
            comp = flood(remaining, [(y, x)])
            remaining &= ~comp
            out.append(comp)
    return out


def _clean_patch(sky, rgbf, row_mean, size=64):
    """Finds a size x size block that is all sky and returns its grain
    (pixel minus its row's mean colour)."""
    h, w = sky.shape
    for y in range(0, h - size, 8):
        for x in range(0, w - size, 8):
            if sky[y:y + size, x:x + size].all():
                return rgbf[y:y + size, x:x + size] - row_mean[y:y + size, None, :]
    raise RuntimeError("no clean sky patch found")


def build_sky():
    img = load("bg.png")
    top = img[:GROUND_TOP]
    rgb = top[..., :3].astype(int)
    lum = rgb.mean(axis=2)
    # Sky is light and bluish; outlines are dark; clouds are near-white.
    skyish = (rgb[..., 2] > rgb[..., 0] + 25) & (lum > 150)
    w = top.shape[1]
    sky = flood(skyish, [(0, x) for x in range(w)])

    solid = ~sky
    comps = components(solid)
    hills = np.zeros_like(solid)
    clouds = []
    for c in comps:
        ys, xs = np.nonzero(c)
        if ys.max() >= GROUND_TOP - 2:
            hills |= c
        elif c.sum() > 400:
            clouds.append(c)
        else:
            hills |= False  # specks of noise stay in the sky layer
            sky |= c

    # Sky: fill clouds/hills with a tiled patch of real sky grain, shifted to
    # each row's average sky colour so the vertical gradient is kept.
    rgbf = top[..., :3].astype(float)
    row_mean = np.array([rgbf[y][sky[y]].mean(axis=0) if sky[y].any() else rgbf[y].mean(axis=0)
                         for y in range(top.shape[0])])
    for y in range(1, len(row_mean)):  # rows fully covered by hills: carry colour down
        if not sky[y].any():
            row_mean[y] = row_mean[y - 1]
    patch = _clean_patch(sky, rgbf, row_mean)
    ph, pw = patch.shape[:2]
    yy, xx = np.nonzero(~sky)
    fill = patch[yy % ph, xx % pw] + row_mean[yy]
    sky_img = top.copy()
    sky_img[yy, xx, :3] = np.clip(fill, 0, 255).astype(np.uint8)
    sky_img[..., 3] = 255
    save(sky_img, "sky.png")

    hills_img = top.copy()
    hills_img[~hills] = 0
    save(hills_img, "hills.png")

    for i, c in enumerate(sorted(clouds, key=lambda m: np.nonzero(m)[1].min())):
        ys, xs = np.nonzero(c)
        y0, y1, x0, x1 = ys.min(), ys.max() + 1, xs.min(), xs.max() + 1
        crop = top[y0:y1, x0:x1].copy()
        crop[~c[y0:y1, x0:x1]] = 0
        save(crop, f"cloud_{i}.png")
    save(img[GROUND_TOP:, 3:-3], "ground.png")  # edge columns have a dark border
    return len(clouds)


def build_stages():
    for n in range(1, 5):
        a = load(f"bg_{n}.png")[:GROUND_TOP]
        b = load(f"bg_{n}_r.png")[:GROUND_TOP]
        save(np.concatenate([a, b], axis=1), f"stage_{n}.png")


if __name__ == "__main__":
    OUT.mkdir(parents=True, exist_ok=True)
    n = build_sky()
    build_stages()
    print(f"built sky, hills, ground, {n} clouds, 4 stages ->", OUT)
