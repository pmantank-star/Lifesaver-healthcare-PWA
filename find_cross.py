with open("canny.ppm", "rb") as f:
    f.readline()
    line = f.readline().decode().strip()
    while line.startswith('#'):
        line = f.readline().decode().strip()
    w, h = map(int, line.split())
    f.readline()
    canny_raw = f.read()

# Find edge pixels in the cross area: x in [210, 250], y in [105, 150]
cross_edges = []
for y in range(105, 150):
    for x in range(210, 250):
        if canny_raw[(y*w + x)*3] > 128:
            cross_edges.append((x, y))

xs = [p[0] for p in cross_edges]
ys = [p[1] for p in cross_edges]
print(f"Cross edges count: {len(cross_edges)}")
print(f"X range: {min(xs)} .. {max(xs)}, center: {(min(xs)+max(xs))/2}")
print(f"Y range: {min(ys)} .. {max(ys)}, center: {(min(ys)+max(ys))/2}")

# Also head edges: x in [200, 260], y in [45, 100]
head_edges = []
for y in range(45, 100):
    for x in range(200, 260):
        if canny_raw[(y*w + x)*3] > 128:
            head_edges.append((x, y))

h_xs = [p[0] for p in head_edges]
h_ys = [p[1] for p in head_edges]
print(f"Head edges count: {len(head_edges)}")
print(f"Head X range: {min(h_xs)} .. {max(h_xs)}, center: {(min(h_xs)+max(h_xs))/2}")
print(f"Head Y range: {min(h_ys)} .. {max(h_ys)}, center: {(min(h_ys)+max(h_ys))/2}")
