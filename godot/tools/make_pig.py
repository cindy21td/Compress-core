#!/usr/bin/env python3
"""Builds assets/art/pig.png: two 100x100 running frames for the Pig enemy.

If the original "Pig Sprite.png" from the 2016 backup (200x100, two frames)
is in ../art-src, it is used as-is. Otherwise a stand-in is drawn in the
game's style: a round black-outlined body, paper grain, angry red eyes.
Usage: python3 tools/make_pig.py
"""
import random
from pathlib import Path

from PIL import Image, ImageDraw, ImageFilter

ROOT = Path(__file__).resolve().parent.parent
SRC = ROOT.parent / "art-src" / "Pig Sprite.png"
OUT = ROOT / "assets" / "art" / "pig.png"
S = 4  # supersampling
INK = (20, 18, 22, 255)
PINK = (238, 150, 160, 255)
PINK_DARK = (205, 105, 122, 255)
SNOUT = (250, 175, 185, 255)


def frame(leg_phase):
    W = 100 * S
    im = Image.new("RGBA", (W, W), (0, 0, 0, 0))
    d = ImageDraw.Draw(im)
    lw = 5 * S

    def ell(box, fill, width=lw):
        d.ellipse([v * S for v in box], fill=fill, outline=INK, width=width)

    # legs (behind body), alternating per frame
    for i, x in enumerate((30, 44, 58, 72)):
        forward = (i % 2 == 0) == leg_phase
        dx = 6 if forward else -6
        d.line([(x * S, 72 * S), ((x + dx) * S, 90 * S)], fill=INK, width=9 * S)
        d.line([(x * S, 72 * S), ((x + dx) * S, 88 * S)], fill=PINK_DARK, width=5 * S)
    # curly tail
    d.arc([6 * S, 38 * S, 20 * S, 52 * S], 90, 400, fill=INK, width=4 * S)
    # body
    ell((12, 22, 86, 82), PINK)
    # belly shading
    d.chord([18 * S, 50 * S, 80 * S, 80 * S], 20, 160, fill=PINK_DARK)
    d.ellipse([12 * S, 22 * S, 86 * S, 82 * S], outline=INK, width=lw)
    # ears
    for pts in (((58, 26), (66, 8), (74, 28)), ((70, 30), (82, 14), (84, 36))):
        d.polygon([(x * S, y * S) for x, y in pts], fill=PINK_DARK, outline=INK)
        d.line([(x * S, y * S) for x, y in pts] + [(pts[0][0] * S, pts[0][1] * S)], fill=INK, width=4 * S)
    # snout (facing left, the direction it runs)
    ell((2, 46, 30, 66), SNOUT)
    d.ellipse([9 * S, 53 * S, 13 * S, 59 * S], fill=INK)
    d.ellipse([19 * S, 53 * S, 23 * S, 59 * S], fill=INK)
    # angry eyes + brow, red like the other enemies
    for cx in (34, 50):
        d.ellipse([(cx - 5) * S, 36 * S, (cx + 5) * S, 45 * S], fill=(220, 40, 30, 255), outline=INK, width=2 * S)
    d.line([(26 * S, 31 * S), (44 * S, 36 * S), (58 * S, 31 * S)], fill=INK, width=4 * S)

    small = im.resize((100, 100), Image.LANCZOS)
    # paper grain, like the hand-textured sprites
    rnd = random.Random(3)
    px = small.load()
    for y in range(100):
        for x in range(100):
            r, g, b, a = px[x, y]
            if a:
                n = rnd.randint(-14, 14)
                px[x, y] = (max(0, min(255, r + n)), max(0, min(255, g + n)), max(0, min(255, b + n)), a)
    return small


def main():
    OUT.parent.mkdir(parents=True, exist_ok=True)
    if SRC.exists():
        Image.open(SRC).convert("RGBA").save(OUT)
        print("using original", SRC)
        return
    sheet = Image.new("RGBA", (200, 100), (0, 0, 0, 0))
    sheet.alpha_composite(frame(True), (0, 0))
    sheet.alpha_composite(frame(False), (100, 0))
    sheet.save(OUT, optimize=True)
    print("drew stand-in pig ->", OUT)


if __name__ == "__main__":
    main()
