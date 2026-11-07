import math
import subprocess

# Crop high-res square from original: cx=847, cy=440, radius=342 (to include full outer ring edge)
# x = 847 - 342 = 505, y = 440 - 342 = 98, size = 684x684
subprocess.run([
    "convert", "public/assets/aistudio/Lifesavers_Healthcare_Logo_Emblem_ec7ba02a.png",
    "-crop", "684x684+505+98", "+repage",
    "crop_hires.ppm"
], check=True)

with open("crop_hires.ppm", "rb") as f:
    f.readline()
    line = f.readline().decode().strip()
    while line.startswith('#'):
        line = f.readline().decode().strip()
    w, h = map(int, line.split())
    f.readline()
    raw = f.read()

print(f"Loaded cropped PPM: {w}x{h}")

# Now make outside of the circle transparent with smooth anti-aliasing
cx = w / 2.0
cy = h / 2.0
# The outer edge of the navy ring:
# Let's inspect where the outer edge ends
R_outer = 337.5

out = bytearray(w * h * 4)

for y in range(h):
    for x in range(w):
        idx = y * w + x
        out_idx = idx * 4
        
        dx = x - cx
        dy = y - cy
        dist = math.sqrt(dx*dx + dy*dy)
        
        r = raw[idx*3]
        g = raw[idx*3 + 1]
        b = raw[idx*3 + 2]
        
        if dist > R_outer + 1.5:
            # Fully transparent outside
            out[out_idx] = 0
            out[out_idx+1] = 0
            out[out_idx+2] = 0
            out[out_idx+3] = 0
        elif dist > R_outer - 1.0:
            # Smooth anti-aliased edge
            alpha = max(0.0, min(1.0, (R_outer + 1.5 - dist) / 2.5))
            out[out_idx] = r
            out[out_idx+1] = g
            out[out_idx+2] = b
            out[out_idx+3] = int(255 * alpha)
        else:
            out[out_idx] = r
            out[out_idx+1] = g
            out[out_idx+2] = b
            out[out_idx+3] = 255

with open("hires_logo_transparent.rgba", "wb") as f:
    f.write(out)

# Convert to 512x512 PNG
subprocess.run([
    "convert", "-size", f"{w}x{h}", "-depth", "8", "hires_logo_transparent.rgba",
    "-resize", "512x512",
    "official_lifesavers_logo.png"
], check=True)

print("Created official_lifesavers_logo.png successfully!")
