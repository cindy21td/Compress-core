#!/usr/bin/env python3
"""Synthesizes the jump and stomp sound effects (replacements for the
original Jump Sound / Hit Sound). Writes 16-bit mono WAVs into assets/sound.
Usage: python3 tools/make_sounds.py"""
import math
import random
import struct
import wave
from pathlib import Path

RATE = 44100
OUT = Path(__file__).resolve().parent.parent / "assets" / "sound"


def write(name, samples):
    OUT.mkdir(parents=True, exist_ok=True)
    peak = max(abs(s) for s in samples) or 1.0
    with wave.open(str(OUT / name), "wb") as w:
        w.setnchannels(1)
        w.setsampwidth(2)
        w.setframerate(RATE)
        w.writeframes(b"".join(struct.pack("<h", int(s / peak * 0.9 * 32767)) for s in samples))


def sweep(duration, f_start, f_end, shape, envelope):
    """Exponential pitch sweep; shape(phase) -> sample, envelope(t01) -> gain."""
    n = int(duration * RATE)
    phase, out = 0.0, []
    for i in range(n):
        t = i / n
        freq = f_start * (f_end / f_start) ** t
        phase += 2 * math.pi * freq / RATE
        out.append(shape(phase) * envelope(t))
    return out


def soft_square(phase):
    return math.sin(phase) + 0.33 * math.sin(3 * phase) + 0.2 * math.sin(5 * phase)


def attack_decay(attack):
    return lambda t: min(1.0, t / attack) * (1.0 - t) ** 1.5


# Jump: quick rising "bwip".
write("Jump Sound.wav", sweep(0.16, 320, 900, soft_square, attack_decay(0.03)))

# Stomp: falling "boing" plus a short noise burst for the impact.
random.seed(7)
tone = sweep(0.18, 620, 140, math.sin, lambda t: math.exp(-5 * t))
noise_len = int(0.03 * RATE)
stomp = [s + (random.uniform(-1, 1) * 0.6 * (1 - i / noise_len) if i < noise_len else 0.0)
         for i, s in enumerate(tone)]
write("Hit Sound.wav", stomp)
print("wrote", *sorted(p.name for p in OUT.glob("*.wav")))
