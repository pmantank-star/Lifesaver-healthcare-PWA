import subprocess
import os

def recolor_teal_layer(src_path, dst_path, target_rgb):
    # Read PAM via ImageMagick
    p = subprocess.Popen(['convert', src_path, 'pam:-'], stdout=subprocess.PIPE)
    header = []
    while True:
        line = p.stdout.readline().decode().strip()
        header.append(line)
        if line == 'ENDHDR':
            break

    meta = {}
    for line in header:
        if line.startswith('WIDTH'): meta['w'] = int(line.split()[1])
        if line.startswith('HEIGHT'): meta['h'] = int(line.split()[1])

    w, h = meta['w'], meta['h']
    raw = bytearray(p.stdout.read())

    tr, tg, tb = target_rgb
    for y in range(h):
        for x in range(w):
            idx = (y * w + x) * 4
            a = raw[idx+3]
            if a > 0:
                raw[idx] = tr
                raw[idx+1] = tg
                raw[idx+2] = tb

    # Write back
    out_hdr = f'P7\nWIDTH {w}\nHEIGHT {h}\nDEPTH 4\nMAXVAL 255\nTUPLTYPE RGB_ALPHA\nENDHDR\n'.encode()
    proc = subprocess.Popen(['convert', 'pam:-', dst_path], stdin=subprocess.PIPE)
    proc.communicate(out_hdr + raw)
    print(f'Recolored {src_path} -> {dst_path} with RGB({tr},{tg},{tb})')

# 1. Recolor teal layers to #015148 (1, 81, 72)
TEAL_015148 = (1, 81, 72)
PINE_01534D = (1, 83, 77)

layers_to_recolor = [
    ('oma_emblem_ring.png', TEAL_015148),
    ('oma_letter_m.png', TEAL_015148),
    ('oma_letter_a.png', TEAL_015148),
    ('oma_tagline.png', PINE_01534D)
]

for name, rgb in layers_to_recolor:
    recolor_teal_layer(f'web/assets/{name}', f'web/assets/{name}', rgb)
    recolor_teal_layer(f'app/src/main/res/drawable/{name}', f'app/src/main/res/drawable/{name}', rgb)

# 2. Recomposite oma_emblem.png
subprocess.run([
    'convert',
    'app/src/main/res/drawable/oma_emblem_ring.png',
    'app/src/main/res/drawable/oma_swoosh.png', '-composite',
    'app/src/main/res/drawable/oma_cross.png', '-composite',
    '-crop', '390x390+0+0', '+repage',
    'app/src/main/res/drawable/oma_emblem.png'
])
subprocess.run(['cp', 'app/src/main/res/drawable/oma_emblem.png', 'web/assets/oma_emblem.png'])

# 3. Recomposite oma_wordmark.png
subprocess.run([
    'convert',
    'app/src/main/res/drawable/oma_emblem_ring.png',
    'app/src/main/res/drawable/oma_swoosh.png', '-composite',
    'app/src/main/res/drawable/oma_cross.png', '-composite',
    'app/src/main/res/drawable/oma_letter_m.png', '-composite',
    'app/src/main/res/drawable/oma_letter_a.png', '-composite',
    '-crop', '992x390+0+0', '+repage',
    'app/src/main/res/drawable/oma_wordmark.png'
])
subprocess.run(['cp', 'app/src/main/res/drawable/oma_wordmark.png', 'web/assets/oma_wordmark.png'])

# 4. Recomposite oma_logo_transparent.png and oma_logo_complete.png
subprocess.run([
    'convert',
    'app/src/main/res/drawable/oma_emblem_ring.png',
    'app/src/main/res/drawable/oma_swoosh.png', '-composite',
    'app/src/main/res/drawable/oma_cross.png', '-composite',
    'app/src/main/res/drawable/oma_letter_m.png', '-composite',
    'app/src/main/res/drawable/oma_letter_a.png', '-composite',
    'app/src/main/res/drawable/oma_tagline.png', '-composite',
    'app/src/main/res/drawable/oma_logo_transparent.png'
])
subprocess.run(['cp', 'app/src/main/res/drawable/oma_logo_transparent.png', 'app/src/main/res/drawable/oma_logo_complete.png'])
subprocess.run(['cp', 'app/src/main/res/drawable/oma_logo_transparent.png', 'web/assets/oma_logo_transparent.png'])
subprocess.run(['cp', 'app/src/main/res/drawable/oma_logo_transparent.png', 'web/assets/oma_logo_complete.png'])

print('All logo assets successfully updated to #015148 and #01534D!')
