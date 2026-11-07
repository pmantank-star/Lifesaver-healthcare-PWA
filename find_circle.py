import subprocess

# Convert a downscaled copy to PPM
subprocess.run(["convert", "public/assets/aistudio/Lifesavers_Healthcare_Logo_Emblem_ec7ba02a.png", "-resize", "850x463", "temp_scaled.ppm"], check=True)

with open("temp_scaled.ppm", "rb") as f:
    f.readline()
    line = f.readline().decode().strip()
    while line.startswith('#'):
        line = f.readline().decode().strip()
    w, h = map(int, line.split())
    f.readline()
    raw = f.read()

# Scale factor from 850x463 back to 1699x926
scale = 1699.0 / w

# Search for the dark blue ring in temp_scaled
# Ring color is dark navy: r < 20, g < 60, b < 120
ring_pixels = []
for y in range(h):
    for x in range(w):
        idx = (y * w + x) * 3
        r = raw[idx]
        g = raw[idx+1]
        b = raw[idx+2]
        if r < 25 and g < 60 and 40 < b < 120:
            ring_pixels.append((x * scale, y * scale))

xs = [p[0] for p in ring_pixels]
ys = [p[1] for p in ring_pixels]

min_x, max_x = min(xs), max(xs)
min_y, max_y = min(ys), max(ys)
cx = (min_x + max_x) / 2.0
cy = (min_y + max_y) / 2.0
radius_x = (max_x - min_x) / 2.0
radius_y = (max_y - min_y) / 2.0

print(f"Ring center: ({cx:.1f}, {cy:.1f}), radius_x: {radius_x:.1f}, radius_y: {radius_y:.1f}")
print(f"Crop box: width={max_x - min_x:.0f}, height={max_y - min_y:.0f}, min_x={min_x:.0f}, min_y={min_y:.0f}")
