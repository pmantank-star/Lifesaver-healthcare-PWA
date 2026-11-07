import math
import subprocess

with open("test_crop.ppm", "rb") as f:
    f.readline()
    line = f.readline().decode().strip()
    while line.startswith('#'):
        line = f.readline().decode().strip()
    w, h = map(int, line.split())
    f.readline()
    raw = bytearray(f.read())

cx, cy = 230, 230
R = 215

mask = bytearray(w * h)

for y in range(h):
    for x in range(w):
        idx = y * w + x
        dx = x - cx
        dy = y - cy
        dist = math.sqrt(dx*dx + dy*dy)
        if dist > R:
            continue
        
        # Don't mask the text area (y > 310) - we will replace it cleanly
        if y > 300:
            continue
            
        r = raw[idx*3]
        g = raw[idx*3 + 1]
        b = raw[idx*3 + 2]
        
        # Baseline disc color is around r=247, g=249, b=247
        # Deviations:
        dev = abs(r - 247) + abs(g - 249) + abs(b - 247)
        if dev > 10:
            mask[idx] = 255

# Morphological dilate mask slightly so it covers the whole ribbon body and bevel
dilated = bytearray(mask)
for _ in range(5):
    temp = bytearray(dilated)
    for y in range(1, h-1):
        for x in range(1, w-1):
            idx = y * w + x
            if (dilated[idx] or dilated[idx-1] or dilated[idx+1] or 
                dilated[idx-w] or dilated[idx+w]):
                temp[idx] = 255
    dilated = temp

# Save mask as PGM
with open("emblem_mask.pgm", "wb") as f:
    f.write(f"P5\n{w} {h}\n255\n".encode())
    f.write(dilated)

subprocess.run(["convert", "emblem_mask.pgm", "emblem_mask.png"], check=True)
print("Mask generated successfully!")
