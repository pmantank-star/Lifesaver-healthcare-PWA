import subprocess
from collections import deque

subprocess.run(["convert", "canny.png", "canny.ppm"], check=True)

with open("canny.ppm", "rb") as f:
    f.readline()
    line = f.readline().decode().strip()
    while line.startswith('#'):
        line = f.readline().decode().strip()
    w, h = map(int, line.split())
    f.readline()
    raw = f.read()

# 1 channel (since canny is b&w)
edges = bytearray(w * h)
for i in range(w * h):
    edges[i] = 1 if raw[i*3] > 128 else 0

print(f"Edges count: {sum(edges)}")
print(f"Edge at (230, 72): {edges[72*w + 230]}")
print(f"Edge at (230, 126): {edges[126*w + 230]}")
