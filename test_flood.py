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

edges = bytearray(w * h)
for i in range(w * h):
    edges[i] = 1 if raw[i*3] > 128 else 0

def flood(start_x, start_y):
    visited = set()
    q = deque([(start_x, start_y)])
    visited.add((start_x, start_y))
    while q:
        x, y = q.popleft()
        for nx, ny in [(x+1, y), (x-1, y), (x, y+1), (x, y-1)]:
            if 0 <= nx < w and 0 <= ny < h:
                if (nx, ny) not in visited and edges[ny*w + nx] == 0:
                    visited.add((nx, ny))
                    q.append((nx, ny))
    return visited

head_pixels = flood(230, 72)
cross_pixels = flood(230, 126)
print(f"Head region size: {len(head_pixels)} pixels")
print(f"Cross region size: {len(cross_pixels)} pixels")
