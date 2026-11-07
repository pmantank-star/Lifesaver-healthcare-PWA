import math
import subprocess
from collections import deque

with open("canny.ppm", "rb") as f:
    f.readline()
    line = f.readline().decode().strip()
    while line.startswith('#'):
        line = f.readline().decode().strip()
    w, h = map(int, line.split())
    f.readline()
    raw = f.read()

# BFS multi-source distance transform from all edge pixels
INF = 9999
dist = [INF] * (w * h)
q = deque()

for y in range(h):
    for x in range(w):
        idx = y * w + x
        if raw[idx*3] > 128:
            dist[idx] = 0
            q.append((x, y))

while q:
    x, y = q.popleft()
    d = dist[y * w + x]
    for nx, ny in [(x+1, y), (x-1, y), (x, y+1), (x, y-1)]:
        if 0 <= nx < w and 0 <= ny < h:
            nidx = ny * w + nx
            if dist[nidx] > d + 1:
                dist[nidx] = d + 1
                q.append((nx, ny))

# Now create an image where pixels within distance <= 7 are marked
out = bytearray(w * h * 3)
for y in range(h):
    for x in range(w):
        idx = y * w + x
        # Restrict to ribbon bounding area
        if 65 <= y <= 300 and 80 <= x <= 380:
            d = dist[idx]
            if d <= 7:
                out[idx*3] = 255
                out[idx*3+1] = 255
                out[idx*3+2] = 255

with open("ribbon_dist.ppm", "wb") as f:
    f.write(f"P6\n{w} {h}\n255\n".encode())
    f.write(out)

subprocess.run(["convert", "ribbon_dist.ppm", "ribbon_dist.png"], check=True)
print("Ribbon distance transform done!")
