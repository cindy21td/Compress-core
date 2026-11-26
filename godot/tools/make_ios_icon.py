"""Builds the iOS app icon (ios/icon_1024.png) from the original 2015 art:
the hero (menu_texture.png, bottom-left frame) on the menu's maroon sunburst
over linen. No alpha, as App Store icons require.

    python3 tools/make_ios_icon.py
"""
import math
from pathlib import Path
from PIL import Image, ImageDraw, ImageFilter

ROOT = Path(__file__).resolve().parent.parent
SIZE = 1024
menu = Image.open(ROOT / "assets/art/menu_texture.png").convert("RGBA")

linen = (232, 229, 224)   # the menu's linen
maroon = (122, 21, 23)    # the menu's rays
icon = Image.new("RGB", (SIZE, SIZE), linen)
draw = ImageDraw.Draw(icon)
cx, cy, rays = SIZE * 0.5, SIZE * 0.56, 12
for i in range(rays):
    a0 = (i / rays) * math.tau
    a1 = a0 + math.tau / rays * 0.5
    r = SIZE * 1.2
    draw.polygon([(cx, cy), (cx + r * math.cos(a0), cy + r * math.sin(a0)),
                  (cx + r * math.cos(a1), cy + r * math.sin(a1))], fill=maroon)

hero = menu.crop((0, 500, 340, 790))  # first hero frame only
hero = hero.crop(hero.getchannel("A").point(lambda a: 255 if a > 128 else 0).getbbox())
scale = SIZE * 0.80 / max(hero.size)
hero = hero.resize((round(hero.width * scale), round(hero.height * scale)), Image.LANCZOS)
pos = ((SIZE - hero.width) // 2 + 20, SIZE - hero.height - 90)
# A sticker edge (linen, then ink) so the scarf reads against the rays.
alpha = hero.getchannel("A")
for grow, colour in ((41, (20, 20, 20)), (31, linen)):
    edge = alpha.filter(ImageFilter.MaxFilter(grow))
    icon.paste(Image.new("RGB", hero.size, colour), pos, edge)
icon.paste(hero, pos, hero)
out = ROOT / "ios/icon_1024.png"
icon.save(out)
print("icon written", out, icon.size, icon.mode)
