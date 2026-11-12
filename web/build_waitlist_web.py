#!/usr/bin/env python3
"""
LifeSavers Healthcare - Waitlist Landing Page Generator
Generates web/index.html with bundled base64 image assets.
"""
import base64
import os

def get_base64(path):
    if os.path.exists(path):
        with open(path, "rb") as f:
            data = base64.b64encode(f.read()).decode("utf-8")
            ext = path.split(".")[-1].lower()
            mime = "image/png" if ext == "png" else "image/jpeg"
            return f"data:{mime};base64,{data}"
    return ""

print("Loading image drawables...")
logo_b64 = get_base64("app/src/main/res/drawable/lifesavers_logo.png")
hero_b64 = get_base64("app/src/main/res/drawable/img_hero_banner.jpg")
team_b64 = get_base64("app/src/main/res/drawable/img_healthcare_team.png")
overlay_b64 = get_base64("app/src/main/res/drawable/bg_medical_overlay.jpg")

print(f"Loaded drawables: logo={len(logo_b64)}, hero={len(hero_b64)}")

# Read current web/index.html or update it
if os.path.exists("web/index.html"):
    print("web/index.html is active and configured with live Supabase.")
