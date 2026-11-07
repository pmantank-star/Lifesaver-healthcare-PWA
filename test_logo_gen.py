import sys
import math
import subprocess

# Convert test_crop.png to PPM
subprocess.run(["convert", "test_crop.png", "test_crop.ppm"], check=True)

with open("test_crop.ppm", "rb") as f:
    header = f.readline().decode().strip()
    # handle possible comments
    line = f.readline().decode().strip()
    while line.startswith('#'):
        line = f.readline().decode().strip()
    w, h = map(int, line.split())
    maxval = int(f.readline().decode().strip())
    raw = f.read()

print(f"Loaded PPM: {w}x{h}, maxval={maxval}, bytes={len(raw)}")
