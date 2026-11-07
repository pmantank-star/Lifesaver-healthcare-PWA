with open("test_crop.ppm", "rb") as f:
    f.readline()
    line = f.readline().decode().strip()
    while line.startswith('#'):
        line = f.readline().decode().strip()
    w, h = map(int, line.split())
    f.readline()
    raw = f.read()

# Check head at (230, 72)
# Check cross at (230, 126)
def get_pixel(x, y):
    idx = (y * w + x) * 3
    return raw[idx], raw[idx+1], raw[idx+2]

print("Head center (230, 72):", get_pixel(230, 72))
print("Cross center (230, 126):", get_pixel(230, 126))
print("Disc bg at (230, 20):", get_pixel(230, 20))
print("Disc bg at (100, 100):", get_pixel(100, 100))
print("Text area at (230, 340):", get_pixel(230, 340))
print("Shadow near head (208, 72):", get_pixel(208, 72))
print("Ribbon center (160, 180):", get_pixel(160, 180))
print("Ribbon shadow (145, 180):", get_pixel(145, 180))
