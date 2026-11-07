import math
import subprocess
from collections import deque

def clamp(v):
    return max(0, min(255, int(v)))

with open("test_crop.ppm", "rb") as f:
    f.readline()
    line = f.readline().decode().strip()
    while line.startswith('#'):
        line = f.readline().decode().strip()
    w, h = map(int, line.split())
    f.readline()
    orig_raw = f.read()

with open("canny.ppm", "rb") as f:
    f.readline()
    line = f.readline().decode().strip()
    while line.startswith('#'):
        line = f.readline().decode().strip()
    w, h = map(int, line.split())
    f.readline()
    canny_raw = f.read()

dist = [9999] * (w * h)
q = deque()
for y in range(h):
    for x in range(w):
        idx = y * w + x
        if canny_raw[idx*3] > 128:
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

out = bytearray(w * h * 4)

cx, cy = 230, 230
R_disc = 216

cross_cx, cross_cy = 230, 126
head_cx, head_cy = 230, 72
head_r = 23

for y in range(h):
    for x in range(w):
        idx = y * w + x
        out_idx = idx * 4
        
        dx = x - cx
        dy = y - cy
        d_center = math.sqrt(dx*dx + dy*dy)
        
        if d_center > R_disc + 1.5:
            out[out_idx] = 0
            out[out_idx+1] = 0
            out[out_idx+2] = 0
            out[out_idx+3] = 0
            continue
            
        edge_alpha = 1.0
        if d_center > R_disc - 1.0:
            edge_alpha = max(0.0, min(1.0, (R_disc + 1.5 - d_center) / 2.5))
            
        is_outer_ring = (d_center >= R_disc - 4.0 and d_center <= R_disc)
        
        orig_r = orig_raw[idx*3]
        orig_g = orig_raw[idx*3 + 1]
        orig_b = orig_raw[idx*3 + 2]
        lum = (orig_r * 0.299 + orig_g * 0.587 + orig_b * 0.114) / 255.0
        lum_mod = (lum - 0.70) / 0.30
        lum_mod = max(0.65, min(1.25, lum_mod))

        d_head = math.sqrt((x - head_cx)**2 + (y - head_cy)**2)
        is_head = (d_head <= head_r)
        
        in_cross_v = (abs(x - cross_cx) <= 6 and abs(y - cross_cy) <= 18)
        in_cross_h = (abs(x - cross_cx) <= 18 and abs(y - cross_cy) <= 6)
        is_cross = (in_cross_v or in_cross_h)
        
        d_edge = dist[idx]
        is_ribbon = False
        if 75 <= y <= 295 and 80 <= x <= 380:
            if d_edge <= 7 and not is_cross:
                is_ribbon = True
                
        is_neck = (abs(x - 230) <= 9 and 92 <= y <= 108)

        if is_cross:
            cr, cg, cb = 239, 68, 68
            r = clamp(cr * lum_mod)
            g = clamp(cg * (lum_mod * 0.85))
            b = clamp(cb * (lum_mod * 0.85))
            out[out_idx] = r
            out[out_idx+1] = g
            out[out_idx+2] = b
            out[out_idx+3] = clamp(255 * edge_alpha)
        elif is_head or is_neck:
            base_r, base_g, base_b = 2, 132, 199
            r = clamp(base_r + (255 - base_r) * (lum_mod - 0.9) * 0.5)
            g = clamp(base_g * lum_mod)
            b = clamp(base_b * lum_mod)
            out[out_idx] = r
            out[out_idx+1] = g
            out[out_idx+2] = b
            out[out_idx+3] = clamp(255 * edge_alpha)
        elif is_ribbon:
            t = (x - 80) / 300.0
            t = max(0.0, min(1.0, t))
            if t < 0.5:
                sub_t = t * 2.0
                base_r = int(2 + (13 - 2) * sub_t)
                base_g = int(132 + (148 - 132) * sub_t)
                base_b = int(199 + (136 - 199) * sub_t)
            else:
                sub_t = (t - 0.5) * 2.0
                base_r = int(13 + (3 - 13) * sub_t)
                base_g = int(148 + (105 - 148) * sub_t)
                base_b = int(136 + (161 - 136) * sub_t)
                
            if lum_mod > 1.0:
                hi = (lum_mod - 1.0) * 0.7
                r = clamp(base_r + (255 - base_r) * hi)
                g = clamp(base_g + (255 - base_g) * hi)
                b = clamp(base_b + (255 - base_b) * hi)
            else:
                sh = lum_mod
                r = clamp(base_r * sh)
                g = clamp(base_g * sh)
                b = clamp(base_b * sh)
                
            out[out_idx] = r
            out[out_idx+1] = g
            out[out_idx+2] = b
            out[out_idx+3] = clamp(255 * edge_alpha)
        elif is_outer_ring:
            out[out_idx] = 2
            out[out_idx+1] = 132
            out[out_idx+2] = 199
            out[out_idx+3] = clamp(220 * edge_alpha)
        else:
            disc_tint = clamp(255 - (d_center / R_disc) * 4)
            out[out_idx] = disc_tint
            out[out_idx+1] = disc_tint
            out[out_idx+2] = disc_tint
            out[out_idx+3] = clamp(255 * edge_alpha)

with open("expert_base.rgba", "wb") as f:
    f.write(out)

subprocess.run(["convert", "-size", f"{w}x{h}", "-depth", "8", "expert_base.rgba", "expert_base.png"], check=True)
print("Base emblem rendered successfully!")
