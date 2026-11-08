#!/usr/bin/env python3
import base64
import os
import sys

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

print(f"Loaded logo ({len(logo_b64)} chars), hero ({len(hero_b64)} chars), team ({len(team_b64)} chars), overlay ({len(overlay_b64)} chars)")

html_content = f"""<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no, viewport-fit=cover">
  <title>LifeSavers Healthcare — Doorstep Emergency & Specialist Network</title>
  <meta name="description" content="LifeSavers Healthcare on-demand location-based healthcare marketplace connecting patients directly with verified local nurses, doctors, and specialists at your doorstep.">
  <link rel="manifest" href="./manifest.json">
  <meta name="theme-color" content="#01514B">
  <meta name="apple-mobile-web-app-capable" content="yes">
  <meta name="apple-mobile-web-app-status-bar-style" content="black-translucent">
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@300;400;500;600;700;800;900&display=swap" rel="stylesheet">
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

  <style>
    :root {{
      --color-primary: #01514B;
      --color-primary-dark: #003732;
      --color-primary-light: #0B6B63;
      --color-teal: #01514B;
      --color-teal-container: #DBE9EB;
      --color-teal-dark: #003732;
      --color-amber: #F59E0B;
      --color-amber-dark: #B45309;
      --color-ice-mint: #DBE9EB;
      --color-ice-mint-dark: #BCD4D7;
      --color-light-mint: #F4F8F8;
      --color-pure-white: #FFFFFF;
      --color-warm-beige: #ECDDCF;
      --color-dark-charcoal: #1A2B29;
      --color-coral: #E0533C;
      --color-emergency: #DC2626;
      --color-success: #16A34A;
      --color-verified-blue: #2563EB;
      --radius-sm: 8px;
      --radius-md: 14px;
      --radius-lg: 20px;
      --radius-full: 9999px;
      --shadow-subtle: 0 4px 14px rgba(1, 81, 75, 0.08);
      --shadow-float: 0 10px 28px rgba(1, 81, 75, 0.16);
      --font-body: 'Plus Jakarta Sans', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
    }}

    * {{
      box-sizing: border-box;
      margin: 0;
      padding: 0;
      -webkit-tap-highlight-color: transparent;
      user-select: none;
    }}

    input, textarea, select {{
      user-select: text;
    }}

    body {{
      font-family: var(--font-body);
      background: #081110;
      color: var(--color-dark-charcoal);
      min-height: 100vh;
      display: flex;
      justify-content: center;
      align-items: center;
      overflow-x: hidden;
    }}

    /* Device Viewport Frame */
    .app-viewport {{
      width: 100%;
      max-width: 440px;
      height: 100vh;
      max-height: 940px;
      background-color: var(--color-light-mint);
      position: relative;
      display: flex;
      flex-direction: column;
      overflow: hidden;
      box-shadow: 0 25px 75px rgba(0,0,0,0.75);
    }}

    @media (min-width: 600px) {{
      .app-viewport {{
        border-radius: 42px;
        border: 9px solid #1a2826;
        margin: 20px auto;
        height: 94vh;
      }}
    }}

    /* Background Medical Cross Texture Overlay */
    .medical-bg-overlay {{
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      background-image: url('{overlay_b64}');
      background-size: cover;
      background-position: center;
      opacity: 0.12;
      pointer-events: none;
      z-index: 1;
    }}

    /* Heads-Up Notification Banner */
    .notification-banner {{
      position: absolute;
      top: 10px;
      left: 12px;
      right: 12px;
      background: var(--color-primary-dark);
      color: var(--color-pure-white);
      padding: 12px 14px;
      border-radius: var(--radius-md);
      box-shadow: 0 10px 25px rgba(0,0,0,0.35);
      border: 1px solid rgba(245, 158, 11, 0.4);
      display: flex;
      align-items: center;
      justify-content: space-between;
      z-index: 9999;
      transform: translateY(-120%);
      transition: transform 0.4s cubic-bezier(0.16, 1, 0.3, 1);
      cursor: pointer;
    }}
    .notification-banner.show {{
      transform: translateY(0);
    }}
    .notification-banner .icon-box {{
      width: 36px;
      height: 36px;
      border-radius: 50%;
      background: rgba(245, 158, 11, 0.2);
      color: var(--color-amber);
      display: flex;
      align-items: center;
      justify-content: center;
      margin-right: 10px;
      flex-shrink: 0;
    }}

    /* Top App Bar */
    .top-app-bar {{
      height: 64px;
      background: var(--color-pure-white);
      border-bottom: 1px solid rgba(1, 81, 75, 0.1);
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 0 16px;
      z-index: 100;
      box-shadow: 0 2px 8px rgba(1,81,75,0.05);
      position: relative;
    }}
    .brand-section {{
      display: flex;
      align-items: center;
      cursor: pointer;
    }}
    .brand-logo-img {{
      width: 36px;
      height: 36px;
      border-radius: 10px;
      object-fit: cover;
      margin-right: 8px;
    }}
    .brand-title {{
      font-weight: 800;
      font-size: 15px;
      color: var(--color-primary);
      line-height: 1.1;
    }}
    .brand-subtitle {{
      font-size: 10px;
      font-weight: 600;
      color: var(--color-amber-dark);
      text-transform: uppercase;
      letter-spacing: 0.5px;
    }}

    .top-actions {{
      display: flex;
      align-items: center;
      gap: 6px;
    }}

    .btn-role-toggle {{
      background: var(--color-teal-container);
      color: var(--color-primary);
      border: none;
      padding: 5px 9px;
      border-radius: 20px;
      font-size: 11px;
      font-weight: 700;
      display: flex;
      align-items: center;
      gap: 4px;
      cursor: pointer;
      transition: all 0.2s ease;
    }}
    .btn-role-toggle.provider {{
      background: var(--color-warm-beige);
      color: var(--color-dark-charcoal);
    }}

    .btn-icon-pill {{
      width: 34px;
      height: 34px;
      border-radius: 50%;
      background: var(--color-light-mint);
      border: 1px solid rgba(1, 81, 75, 0.15);
      color: var(--color-primary);
      display: flex;
      align-items: center;
      justify-content: center;
      cursor: pointer;
      position: relative;
      font-size: 13px;
      transition: background 0.2s;
    }}
    .btn-icon-pill:hover {{
      background: var(--color-teal-container);
    }}
    .badge-count {{
      position: absolute;
      top: -3px;
      right: -3px;
      background: var(--color-coral);
      color: white;
      font-size: 9px;
      font-weight: 800;
      width: 16px;
      height: 16px;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      border: 1.5px solid white;
    }}
    .sync-indicator {{
      position: absolute;
      bottom: 2px;
      right: 2px;
      width: 7px;
      height: 7px;
      border-radius: 50%;
      background: var(--color-success);
      border: 1px solid white;
    }}

    /* Main Scroll Area */
    .screen-container {{
      flex: 1;
      overflow-y: auto;
      overflow-x: hidden;
      position: relative;
      z-index: 10;
      padding-bottom: 70px;
      -webkit-overflow-scrolling: touch;
    }}

    /* Bottom Navigation Bar */
    .bottom-nav {{
      position: absolute;
      bottom: 0;
      left: 0;
      right: 0;
      height: 64px;
      background: var(--color-pure-white);
      border-top: 1px solid rgba(1, 81, 75, 0.12);
      display: flex;
      align-items: center;
      justify-content: space-around;
      z-index: 200;
      box-shadow: 0 -4px 16px rgba(0,0,0,0.06);
    }}
    .nav-item {{
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      gap: 3px;
      color: #64748B;
      cursor: pointer;
      font-size: 11px;
      font-weight: 600;
      flex: 1;
      height: 100%;
      transition: color 0.2s;
    }}
    .nav-item i {{
      font-size: 16px;
      transition: transform 0.2s;
    }}
    .nav-item.active {{
      color: var(--color-primary);
      font-weight: 800;
    }}
    .nav-item.active i {{
      transform: scale(1.15);
    }}
    .nav-item.provider-mode.active {{
      color: var(--color-coral);
    }}

    /* Universal Modal & Sheet Styles */
    .modal-overlay {{
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: rgba(8, 17, 16, 0.72);
      backdrop-filter: blur(4px);
      z-index: 1000;
      display: none;
      align-items: flex-end;
      justify-content: center;
      animation: fadeIn 0.25s ease forwards;
    }}
    .modal-overlay.open {{
      display: flex;
    }}
    .bottom-sheet {{
      width: 100%;
      max-height: 90%;
      background: var(--color-pure-white);
      border-radius: 26px 26px 0 0;
      padding: 20px;
      overflow-y: auto;
      box-shadow: 0 -10px 40px rgba(0,0,0,0.3);
      animation: slideUp 0.3s cubic-bezier(0.16, 1, 0.3, 1) forwards;
      display: flex;
      flex-direction: column;
      position: relative;
    }}
    .sheet-handle {{
      width: 44px;
      height: 5px;
      background: #CBD5E1;
      border-radius: 3px;
      margin: 0 auto 16px auto;
      flex-shrink: 0;
    }}

    @keyframes fadeIn {{
      from {{ opacity: 0; }}
      to {{ opacity: 1; }}
    }}
    @keyframes slideUp {{
      from {{ transform: translateY(100%); }}
      to {{ transform: translateY(0); }}
    }}

    /* Common Card Styles */
    .card {{
      background: var(--color-pure-white);
      border-radius: var(--radius-lg);
      padding: 16px;
      box-shadow: var(--shadow-subtle);
      border: 1px solid rgba(1, 81, 75, 0.08);
      margin-bottom: 12px;
    }}
    .card-teal {{
      background: var(--color-primary);
      color: var(--color-pure-white);
    }}
    .card-beige {{
      background: var(--color-warm-beige);
      color: var(--color-dark-charcoal);
    }}

    .badge-pill {{
      display: inline-flex;
      align-items: center;
      gap: 4px;
      padding: 3px 8px;
      border-radius: var(--radius-full);
      font-size: 10px;
      font-weight: 700;
    }}
    .badge-amber {{
      background: var(--color-amber);
      color: var(--color-dark-charcoal);
    }}
    .badge-mint {{
      background: var(--color-teal-container);
      color: var(--color-primary);
    }}
    .badge-coral {{
      background: rgba(224, 83, 60, 0.15);
      color: var(--color-coral);
    }}
    .badge-success {{
      background: rgba(22, 163, 74, 0.15);
      color: var(--color-success);
    }}

    .btn-primary {{
      background: var(--color-primary);
      color: var(--color-pure-white);
      border: none;
      padding: 12px 18px;
      border-radius: var(--radius-md);
      font-size: 13px;
      font-weight: 700;
      cursor: pointer;
      display: inline-flex;
      align-items: center;
      justify-content: center;
      gap: 6px;
      transition: background 0.2s;
    }}
    .btn-primary:hover {{
      background: var(--color-primary-dark);
    }}
    .btn-amber {{
      background: var(--color-amber);
      color: var(--color-dark-charcoal);
    }}
    .btn-amber:hover {{
      background: var(--color-amber-dark);
      color: white;
    }}
    .btn-coral {{
      background: var(--color-coral);
      color: white;
    }}
    .btn-outlined {{
      background: transparent;
      border: 1.5px solid var(--color-primary);
      color: var(--color-primary);
      padding: 10px 16px;
      border-radius: var(--radius-md);
      font-size: 13px;
      font-weight: 700;
      cursor: pointer;
    }}

    /* Radar Canvas */
    .radar-box {{
      width: 100%;
      height: 190px;
      background: #002522;
      border-radius: var(--radius-lg);
      position: relative;
      overflow: hidden;
      border: 1.5px solid rgba(219, 233, 235, 0.25);
    }}
    .radar-blip {{
      position: absolute;
      width: 24px;
      height: 24px;
      border-radius: 50%;
      background: rgba(245, 158, 11, 0.35);
      border: 2px solid var(--color-amber);
      display: flex;
      align-items: center;
      justify-content: center;
      color: white;
      font-size: 9px;
      font-weight: 800;
      cursor: pointer;
      transform: translate(-50%, -50%);
      transition: transform 0.2s;
    }}
    .radar-blip:hover {{
      transform: translate(-50%, -50%) scale(1.3);
    }}
    .radar-pulse {{
      position: absolute;
      top: 50%;
      left: 50%;
      width: 180px;
      height: 180px;
      border-radius: 50%;
      border: 1px dashed rgba(219, 233, 235, 0.2);
      transform: translate(-50%, -50%);
      pointer-events: none;
    }}

    /* Hero Banner */
    .hero-banner {{
      position: relative;
      width: 100%;
      height: 200px;
      border-radius: var(--radius-lg);
      overflow: hidden;
      margin-bottom: 16px;
    }}
    .hero-banner img {{
      width: 100%;
      height: 100%;
      object-fit: cover;
    }}
    .hero-gradient {{
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: linear-gradient(to bottom, rgba(1,81,75,0.05) 0%, rgba(1,81,75,0.45) 50%, rgba(0,55,50,0.95) 100%);
      display: flex;
      flex-direction: column;
      justify-content: flex-end;
      padding: 16px;
      color: white;
    }}

    /* Chips Horizontal Carousel */
    .chip-scroll {{
      display: flex;
      gap: 8px;
      overflow-x: auto;
      padding-bottom: 6px;
      margin-bottom: 12px;
      -webkit-overflow-scrolling: touch;
    }}
    .chip-scroll::-webkit-scrollbar {{
      display: none;
    }}
    .filter-chip {{
      padding: 6px 14px;
      background: var(--color-teal-container);
      color: var(--color-primary);
      border-radius: var(--radius-full);
      font-size: 12px;
      font-weight: 700;
      white-space: nowrap;
      cursor: pointer;
      border: 1px solid transparent;
      transition: all 0.2s;
    }}
    .filter-chip.active {{
      background: var(--color-primary);
      color: white;
    }}

    /* Screen Views */
    .screen-view {{
      display: none;
      padding: 16px;
    }}
    .screen-view.active {{
      display: block;
    }}

    /* Form Fields */
    .form-group {{
      margin-bottom: 12px;
    }}
    .form-label {{
      display: block;
      font-size: 11px;
      font-weight: 700;
      color: #475569;
      margin-bottom: 4px;
      text-transform: uppercase;
      letter-spacing: 0.5px;
    }}
    .form-input, .form-select, .form-textarea {{
      width: 100%;
      padding: 10px 12px;
      border-radius: var(--radius-md);
      border: 1.5px solid #CBD5E1;
      font-family: var(--font-body);
      font-size: 13px;
      color: var(--color-dark-charcoal);
      outline: none;
      transition: border-color 0.2s;
    }}
    .form-input:focus, .form-select:focus, .form-textarea:focus {{
      border-color: var(--color-primary);
    }}

    /* Consultation Chat Bubbles */
    .chat-bubble {{
      max-width: 80%;
      padding: 10px 14px;
      border-radius: 18px;
      font-size: 13px;
      line-height: 1.4;
      margin-bottom: 10px;
      position: relative;
    }}
    .chat-bubble.patient {{
      align-self: flex-end;
      background: var(--color-primary);
      color: white;
      border-bottom-right-radius: 4px;
      margin-left: auto;
    }}
    .chat-bubble.provider {{
      align-self: flex-start;
      background: var(--color-pure-white);
      color: var(--color-dark-charcoal);
      border-bottom-left-radius: 4px;
      box-shadow: var(--shadow-subtle);
    }}
    .chat-bubble.emergency {{
      background: rgba(220, 38, 38, 0.12);
      border: 1.5px solid var(--color-emergency);
      color: var(--color-emergency);
      font-weight: 700;
    }}
  </style>
</head>
<body>

  <div class="app-viewport">
    <div class="medical-bg-overlay"></div>

    <!-- Heads-Up Notification Banner -->
    <div id="headsUpBanner" class="notification-banner" onclick="handleBannerClick()">
      <div style="display:flex; align-items:center;">
        <div class="icon-box"><i class="fa-solid fa-bolt"></i></div>
        <div>
          <div id="bannerTitle" style="font-size:12px; font-weight:800; color:var(--color-amber);">DISPATCH UPDATE</div>
          <div id="bannerText" style="font-size:11px; opacity:0.9;">Nurse Amara is en-route (~7m)</div>
        </div>
      </div>
      <i class="fa-solid fa-chevron-right" style="font-size:11px; opacity:0.6;"></i>
    </div>

    <!-- Top App Bar -->
    <div class="top-app-bar">
      <div class="brand-section" onclick="openLogoReveal()">
        <img class="brand-logo-img" src="{logo_b64}" alt="LifeSavers Logo">
        <div>
          <div class="brand-title">LifeSavers</div>
          <div class="brand-subtitle">Healthcare</div>
        </div>
      </div>

      <div class="top-actions">
        <!-- Role Toggle -->
        <button id="roleToggleBtn" class="btn-role-toggle" onclick="toggleUserRole()">
          <i class="fa-solid fa-user-doctor"></i>
          <span id="roleLabel">Patient</span>
        </button>

        <!-- Location Picker -->
        <div class="btn-icon-pill" onclick="openAddressDialog()" title="Change Address">
          <i class="fa-solid fa-location-dot" style="color:var(--color-coral);"></i>
        </div>

        <!-- Supabase Cloud Sync -->
        <div class="btn-icon-pill" onclick="openSupabaseSheet()" title="Cloud Sync">
          <i class="fa-solid fa-cloud"></i>
          <span class="sync-indicator"></span>
        </div>

        <!-- Medical Supplies Cart -->
        <div class="btn-icon-pill" onclick="openCartDrawer()" title="Medical Supplies Cart">
          <i class="fa-solid fa-kit-medical"></i>
          <span id="cartBadge" class="badge-count">2</span>
        </div>

        <!-- User Profile -->
        <div class="btn-icon-pill" onclick="openProfileSheet()" title="My Profile & History">
          <i class="fa-solid fa-user"></i>
        </div>
      </div>
    </div>

    <!-- Main Screens Container -->
    <div class="screen-container">

      <!-- ================= 1. HOME SCREEN ================= -->
      <div id="screen-home" class="screen-view active">
        <!-- Active Booking Banner -->
        <div id="activeBookingBanner" class="card card-teal" style="display:flex; justify-content:space-between; align-items:center; cursor:pointer;" onclick="navigateToScreen('screen-tracker')">
          <div style="display:flex; align-items:center; gap:10px;">
            <div style="width:36px; height:36px; border-radius:50%; background:rgba(255,255,255,0.2); display:flex; align-items:center; justify-content:center;">
              <i class="fa-solid fa-bolt" style="color:var(--color-amber);"></i>
            </div>
            <div>
              <div style="font-size:9px; font-weight:800; letter-spacing:0.5px; color:var(--color-ice-mint);">DISPATCH IN PROGRESS</div>
              <div id="activeBookingTitle" style="font-size:13px; font-weight:800;">Nurse Amara Okonkwo • En Route</div>
              <div style="font-size:11px; opacity:0.85;">ETA ~7 mins • Tap to track on map & chat</div>
            </div>
          </div>
          <i class="fa-solid fa-arrow-right" style="color:var(--color-amber);"></i>
        </div>

        <!-- Hero Section -->
        <div class="hero-banner">
          <img src="{hero_b64}" alt="Healthcare at your doorstep">
          <div class="hero-gradient">
            <span class="badge-pill badge-amber" style="width:fit-content; margin-bottom:4px;">THE BOLT FOR HEALTHCARE</span>
            <div style="font-size:18px; font-weight:900; line-height:1.2;">Healthcare at your doorstep.</div>
            <div style="font-size:11px; opacity:0.9; margin:4px 0 10px 0;">Verified nurses, doctors & therapists dispatched to your home in minutes.</div>
            <button class="btn-primary btn-amber" onclick="quickBookDefaultService()" style="width:fit-content; font-size:12px; padding:8px 14px;">
              <i class="fa-solid fa-bolt"></i> Request LifeSavers Now
            </button>
          </div>
        </div>

        <!-- Personalized Health Suite Entry Card with 1-Tap Gender Switcher -->
        <div class="card card-teal" style="background:#01514B;">
          <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:8px;">
            <span id="suiteBadgeLabel" class="badge-pill badge-amber">UNICEF FEMTECH CALL</span>
            <!-- 1-Tap Gender Switcher -->
            <div style="background:rgba(255,255,255,0.15); padding:2px; border-radius:20px; display:flex;">
              <button id="genderFemaleBtn" class="filter-chip active" style="padding:3px 10px; font-size:10px;" onclick="switchGender('FEMALE')">
                <i class="fa-solid fa-venus"></i> Female
              </button>
              <button id="genderMaleBtn" class="filter-chip" style="padding:3px 10px; font-size:10px; background:transparent; color:white;" onclick="switchGender('MALE')">
                <i class="fa-solid fa-mars"></i> Male
              </button>
            </div>
          </div>
          <div id="suiteHeadline" style="font-size:15px; font-weight:800; color:white; margin-bottom:2px;">Maternal & Reproductive Health Suite</div>
          <div id="suiteDescription" style="font-size:11px; color:var(--color-ice-mint); margin-bottom:10px;">
            Menstrual cycle calculator, 1,000 Days maternal milestones, point-of-care vitals triage, and encrypted privacy shield.
          </div>
          <button class="btn-primary" style="background:white; color:var(--color-primary); font-size:12px; padding:8px 14px;" onclick="navigateToScreen('screen-femtech')">
            <i class="fa-solid fa-heart-pulse"></i> Open Health Suite
          </button>
        </div>

        <!-- Urgent Triage Callout -->
        <div class="card" style="background:rgba(220,38,38,0.06); border:1px solid rgba(220,38,38,0.25); display:flex; justify-content:space-between; align-items:center;">
          <div style="display:flex; align-items:center; gap:10px;">
            <div style="width:36px; height:36px; border-radius:50%; background:rgba(220,38,38,0.15); color:var(--color-emergency); display:flex; align-items:center; justify-content:center;">
              <i class="fa-solid fa-truck-medical"></i>
            </div>
            <div>
              <div style="font-size:13px; font-weight:800; color:var(--color-emergency);">Need Urgent In-Home Care?</div>
              <div style="font-size:11px; color:#64748B;">Fast wound dressing, vitals triage, or acute medications</div>
            </div>
          </div>
          <button class="btn-primary btn-coral" style="padding:6px 12px; font-size:11px;" onclick="quickBookUrgentTriage()">Triage</button>
        </div>

        <!-- Live Healthcare Radar -->
        <div style="margin:16px 0 8px 0;">
          <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:6px;">
            <div>
              <div style="font-size:14px; font-weight:800; color:var(--color-primary);"><i class="fa-solid fa-radar"></i> Live Healthcare Radar</div>
              <div style="font-size:11px; color:#64748B;">Tap any nearby verified health worker on the radar map</div>
            </div>
            <span class="badge-pill badge-mint"><i class="fa-solid fa-circle-dot" style="color:var(--color-success); font-size:8px;"></i> 8 Online</span>
          </div>

          <div class="radar-box">
            <div class="radar-pulse"></div>
            <div style="position:absolute; top:50%; left:50%; width:100px; height:100px; border-radius:50%; border:1px dashed rgba(219, 233, 235, 0.25); transform:translate(-50%, -50%);"></div>
            <!-- Center Patient Marker -->
            <div style="position:absolute; top:50%; left:50%; transform:translate(-50%, -50%); width:18px; height:18px; border-radius:50%; background:var(--color-coral); border:2px solid white; box-shadow:0 0 10px var(--color-coral); display:flex; align-items:center; justify-content:center; color:white; font-size:8px;">
              <i class="fa-solid fa-house"></i>
            </div>
            <!-- Dynamic Provider Blips -->
            <div class="radar-blip" style="top:32%; left:62%;" onclick="openProviderDetail('prov-01')" title="Nurse Amara Okonkwo (0.8km)">AO</div>
            <div class="radar-blip" style="top:65%; left:28%;" onclick="openProviderDetail('prov-02')" title="Dr. Tunde Balogun (1.4km)">TB</div>
            <div class="radar-blip" style="top:74%; left:78%;" onclick="openProviderDetail('prov-03')" title="Blessing Eze, PT (1.9km)">BE</div>
            <div class="radar-blip" style="top:24%; left:35%;" onclick="openProviderDetail('prov-05')" title="Halima Musa, CHEW (0.5km)">HM</div>
            <div class="radar-blip" style="top:42%; left:85%;" onclick="openProviderDetail('prov-00')" title="Funmi Adewale, Child Care (0.7km)">FA</div>
          </div>
        </div>

        <!-- Care Categories Chips -->
        <div style="margin:16px 0 6px 0;">
          <div style="font-size:14px; font-weight:800; color:var(--color-dark-charcoal); margin-bottom:8px;">Care Categories</div>
          <div class="chip-scroll">
            <div class="filter-chip active" onclick="filterCareCategory('ALL', this)">All Categories</div>
            <div class="filter-chip" onclick="filterCareCategory('CHILD_CARE_GIVER', this)">👶 Child Caregiver</div>
            <div class="filter-chip" onclick="filterCareCategory('ELDERLY_CARE_GIVER', this)">👵 Elderly Caregiver</div>
            <div class="filter-chip" onclick="filterCareCategory('NURSE', this)">🩺 Registered Nurse</div>
            <div class="filter-chip" onclick="filterCareCategory('DOCTOR', this)">👨‍⚕️ Online Doctor</div>
            <div class="filter-chip" onclick="filterCareCategory('PHYSIOTHERAPIST', this)">🏃 Physiotherapy</div>
            <div class="filter-chip" onclick="filterCareCategory('SEXUAL_REPRODUCTIVE_HEALTH', this)">🌸 SRH & Fertility</div>
            <div class="filter-chip" onclick="filterCareCategory('COMMUNITY_HEALTH', this)">🏘️ Community Health</div>
          </div>
        </div>

        <!-- Popular Home Visits Services List -->
        <div style="margin:16px 0 6px 0;">
          <div style="font-size:14px; font-weight:800; color:var(--color-dark-charcoal); margin-bottom:8px;">Popular Home Visits</div>
          <div id="homeServicesContainer"></div>
        </div>

        <!-- Top Verified Providers -->
        <div style="margin:16px 0 6px 0;">
          <div style="font-size:14px; font-weight:800; color:var(--color-dark-charcoal); margin-bottom:8px;">Nearby Care Providers</div>
          <div id="homeProvidersContainer"></div>
        </div>
      </div>

      <!-- ================= 2. PROVIDERS DIRECTORY SCREEN ================= -->
      <div id="screen-providers" class="screen-view">
        <div style="font-size:18px; font-weight:900; color:var(--color-primary); margin-bottom:4px;">Verified Providers Directory</div>
        <div style="font-size:12px; color:#64748B; margin-bottom:12px;">All health workers undergo NMCN/MDCN board verification & criminal background clearance.</div>

        <!-- Search Input -->
        <div class="form-group" style="position:relative;">
          <input type="text" id="providerSearchInput" class="form-input" placeholder="Search by name, specialty, or condition..." oninput="handleProviderSearch()" style="padding-left:36px;">
          <i class="fa-solid fa-magnifying-glass" style="position:absolute; left:12px; top:12px; color:#94A3B8;"></i>
        </div>

        <div id="directoryProvidersContainer"></div>
      </div>

      <!-- ================= 3. PROVIDER DETAIL SCREEN ================= -->
      <div id="screen-provider-detail" class="screen-view">
        <button class="btn-outlined" style="margin-bottom:12px; padding:6px 12px; font-size:11px;" onclick="navigateBack()">
          <i class="fa-solid fa-arrow-left"></i> Back to Providers
        </button>

        <div id="providerDetailContent"></div>
      </div>

      <!-- ================= 4. BOOKING FLOW SCREEN (3-STEP) ================= -->
      <div id="screen-booking-flow" class="screen-view">
        <button class="btn-outlined" style="margin-bottom:12px; padding:6px 12px; font-size:11px;" onclick="navigateBack()">
          <i class="fa-solid fa-arrow-left"></i> Cancel Booking
        </button>

        <div class="card card-teal" style="margin-bottom:14px;">
          <div style="display:flex; align-items:center; gap:12px;">
            <div style="width:44px; height:44px; border-radius:50%; background:var(--color-teal-container); color:var(--color-primary); display:flex; align-items:center; justify-content:center; font-size:18px;">
              <i class="fa-solid fa-stethoscope"></i>
            </div>
            <div style="flex:1;">
              <div id="bookFlowServiceName" style="font-size:15px; font-weight:800;">Wound Dressing & Sterile Care</div>
              <div id="bookFlowSpecialtyDuration" style="font-size:11px; color:var(--color-ice-mint);">Registered Nurse • Est. 30 minutes</div>
            </div>
            <div id="bookFlowBasePrice" style="font-size:16px; font-weight:900; color:var(--color-amber);">₦15,000</div>
          </div>
        </div>

        <!-- Preferred Provider Selected -->
        <div id="bookFlowProviderCard" class="card" style="display:flex; align-items:center; gap:12px; margin-bottom:14px;">
          <div id="bookFlowProvAvatar" style="width:40px; height:40px; border-radius:50%; background:var(--color-teal-container); color:var(--color-primary); display:flex; align-items:center; justify-content:center; font-weight:800;">AO</div>
          <div style="flex:1;">
            <div style="display:flex; align-items:center; gap:4px;">
              <span id="bookFlowProvName" style="font-size:13px; font-weight:800;">Nurse Amara Okonkwo</span>
              <i class="fa-solid fa-circle-check" style="color:var(--color-verified-blue); font-size:12px;"></i>
            </div>
            <div id="bookFlowProvSub" style="font-size:11px; color:#64748B;">Preferred Provider • 0.8 km away (ETA ~9m)</div>
          </div>
        </div>

        <!-- Step 1: Patient Details & Doorstep Address -->
        <div class="card">
          <div style="font-size:13px; font-weight:800; color:var(--color-primary); margin-bottom:10px;">
            <i class="fa-solid fa-house-medical"></i> Patient & Doorstep Address
          </div>

          <div class="form-group">
            <label class="form-label">Patient Full Name</label>
            <input type="text" id="bookingPatientName" class="form-input" value="Chidi Anya">
          </div>

          <div class="form-group">
            <label class="form-label">Doorstep Address / House Number</label>
            <input type="text" id="bookingDoorstepAddress" class="form-input" value="14 Adeola Odeku St, Victoria Island, Lagos">
          </div>

          <div class="form-group">
            <label class="form-label">Contact Phone Number</label>
            <input type="text" id="bookingPhone" class="form-input" value="+234 803 555 0192">
          </div>
        </div>

        <!-- Step 2: Clinical Triage Questionnaire & Supplies Selection -->
        <div class="card">
          <div style="font-size:13px; font-weight:800; color:var(--color-primary); margin-bottom:10px;">
            <i class="fa-solid fa-clipboard-question"></i> Clinical Triage & Supplies
          </div>

          <div class="form-group">
            <label class="form-label">Symptom Notes / Clinical Instructions</label>
            <textarea id="bookingSymptoms" class="form-textarea" rows="2">Post-cesarean incision site cleaning and sterile dressing change required.</textarea>
          </div>

          <div style="font-size:11px; font-weight:700; color:#475569; margin-bottom:6px;">INCLUDE STERILE MEDICAL SUPPLIES BUNDLE:</div>
          <div style="display:flex; flex-direction:column; gap:6px;">
            <label style="display:flex; align-items:center; gap:8px; font-size:12px;">
              <input type="checkbox" id="supplyGauze" checked onchange="updateBookingCost()"> Sterile Gauze & Bandage Pack (+₦3,500)
            </label>
            <label style="display:flex; align-items:center; gap:8px; font-size:12px;">
              <input type="checkbox" id="supplyIodine" checked onchange="updateBookingCost()"> Povidone Iodine Solution 100ml (+₦2,200)
            </label>
            <label style="display:flex; align-items:center; gap:8px; font-size:12px;">
              <input type="checkbox" id="supplyGloves" onchange="updateBookingCost()"> Sterile Nitrile Gloves Box (+₦5,500)
            </label>
          </div>
        </div>

        <!-- Step 3: Transparent Pricing & Confirmation -->
        <div class="card card-beige">
          <div style="font-size:13px; font-weight:800; margin-bottom:8px;">Cost Summary & Invoice</div>
          <div style="display:flex; justify-content:space-between; font-size:12px; margin-bottom:4px;">
            <span>Base Service Fee:</span>
            <span id="summaryBaseRate">₦15,000</span>
          </div>
          <div style="display:flex; justify-content:space-between; font-size:12px; margin-bottom:4px;">
            <span>Medical Supplies Add-on:</span>
            <span id="summarySuppliesRate">₦5,700</span>
          </div>
          <div style="display:flex; justify-content:space-between; font-size:12px; margin-bottom:4px;">
            <span>Emergency Dispatch Surcharge:</span>
            <span>₦1,000</span>
          </div>
          <div style="display:flex; justify-content:space-between; font-size:12px; margin-bottom:8px; color:#64748B;">
            <span>Regulatory & Clinical Insurance:</span>
            <span>Included (0%)</span>
          </div>
          <div style="border-top:1px dashed #CBD5E1; padding-top:8px; display:flex; justify-content:space-between; font-size:16px; font-weight:900; color:var(--color-primary);">
            <span>Total Payable:</span>
            <span id="summaryTotalRate">₦21,700</span>
          </div>
        </div>

        <button class="btn-primary btn-coral" style="width:100%; padding:14px; font-size:14px; font-weight:800; border-radius:var(--radius-md); margin-top:8px;" onclick="confirmAndExecuteDispatch()">
          <i class="fa-solid fa-bolt"></i> Confirm & Dispatch Health Worker Now
        </button>
      </div>

      <!-- ================= 5. LIVE DISPATCH TRACKER SCREEN ================= -->
      <div id="screen-tracker" class="screen-view">
        <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:12px;">
          <div style="font-size:18px; font-weight:900; color:var(--color-primary);">Live Dispatch Tracker</div>
          <span id="trackerBookingId" class="badge-pill badge-mint">LIFESAVERS-89412</span>
        </div>

        <!-- Status Hero Card -->
        <div class="card card-teal" style="text-align:center; padding:20px;">
          <div style="width:68px; height:68px; border-radius:50%; background:rgba(219,233,235,0.25); border:3px solid var(--color-amber); display:flex; align-items:center; justify-content:center; margin:0 auto 12px auto; font-size:24px; color:var(--color-amber);">
            <i id="trackerStatusIcon" class="fa-solid fa-truck-fast"></i>
          </div>
          <div id="trackerStatusLabel" style="font-size:18px; font-weight:900; color:white;">HEALTH WORKER EN ROUTE</div>
          <div id="trackerEtaText" style="font-size:12px; color:var(--color-ice-mint); margin-top:4px;">Estimated Doorstep Arrival: ~7 Minutes</div>
        </div>

        <!-- Stepper Progress Tracker -->
        <div class="card">
          <div style="font-size:13px; font-weight:800; color:var(--color-primary); margin-bottom:12px;">Dispatch Milestones</div>

          <div style="display:flex; flex-direction:column; gap:14px;">
            <div style="display:flex; align-items:center; gap:10px;">
              <i class="fa-solid fa-circle-check" style="color:var(--color-success); font-size:18px;"></i>
              <div>
                <div style="font-size:12px; font-weight:800;">Request Accepted</div>
                <div style="font-size:10px; color:#64748B;">Matched with nearby verified provider</div>
              </div>
            </div>
            <div style="display:flex; align-items:center; gap:10px;">
              <i id="stepEnRouteIcon" class="fa-solid fa-circle-check" style="color:var(--color-success); font-size:18px;"></i>
              <div>
                <div style="font-size:12px; font-weight:800;">Health Worker En Route</div>
                <div style="font-size:10px; color:#64748B;">Transit with sterile clinical supplies bag</div>
              </div>
            </div>
            <div style="display:flex; align-items:center; gap:10px;">
              <i id="stepArrivedIcon" class="fa-regular fa-circle" style="color:#CBD5E1; font-size:18px;"></i>
              <div>
                <div style="font-size:12px; font-weight:800;">Doorstep Arrival & ID Verification</div>
                <div style="font-size:10px; color:#64748B;">Patient check & hygiene protocol</div>
              </div>
            </div>
            <div style="display:flex; align-items:center; gap:10px;">
              <i id="stepCompletedIcon" class="fa-regular fa-circle" style="color:#CBD5E1; font-size:18px;"></i>
              <div>
                <div style="font-size:12px; font-weight:800;">Procedure Completed & Signed Off</div>
                <div style="font-size:10px; color:#64748B;">Vitals summary & feedback logging</div>
              </div>
            </div>
          </div>
        </div>

        <!-- Assigned Provider Card with Fast Chat / Call -->
        <div class="card" style="display:flex; align-items:center; justify-content:space-between;">
          <div style="display:flex; align-items:center; gap:12px;">
            <div style="width:48px; height:48px; border-radius:50%; background:var(--color-teal-container); color:var(--color-primary); display:flex; align-items:center; justify-content:center; font-weight:900;">AO</div>
            <div>
              <div style="font-size:14px; font-weight:800;">Nurse Amara Okonkwo</div>
              <div style="font-size:11px; color:#64748B;">NMCN Licensed RN • 4.96 ★</div>
            </div>
          </div>
          <div style="display:flex; gap:6px;">
            <button class="btn-icon-pill" onclick="triggerAudioCall('Nurse Amara Okonkwo')" title="Audio Call">
              <i class="fa-solid fa-phone" style="color:var(--color-success);"></i>
            </button>
            <button class="btn-icon-pill" onclick="navigateToScreen('screen-chat')" title="Consultation Chat">
              <i class="fa-solid fa-comment-medical" style="color:var(--color-primary);"></i>
            </button>
          </div>
        </div>

        <!-- Simulation Controls for Client Demo -->
        <div class="card card-beige" style="text-align:center;">
          <div style="font-size:11px; font-weight:800; color:var(--color-dark-charcoal); margin-bottom:8px;">TEST DISPATCH PROGRESSION PIPELINE</div>
          <div style="display:flex; gap:8px; justify-content:center;">
            <button class="btn-primary" style="font-size:11px; padding:8px 14px;" onclick="simulateAdvanceDispatch()">
              <i class="fa-solid fa-forward-step"></i> Advance Status Stage
            </button>
            <button class="btn-outlined" style="font-size:11px; padding:8px 14px; border-color:var(--color-coral); color:var(--color-coral);" onclick="cancelActiveDispatch()">
              Cancel
            </button>
          </div>
        </div>
      </div>

      <!-- ================= 6. CONSULTATION CHAT SCREEN ================= -->
      <div id="screen-chat" class="screen-view">
        <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:12px;">
          <div style="display:flex; align-items:center; gap:10px;">
            <div style="width:36px; height:36px; border-radius:50%; background:var(--color-teal-container); color:var(--color-primary); display:flex; align-items:center; justify-content:center; font-weight:800;">AO</div>
            <div>
              <div style="font-size:14px; font-weight:800;">Nurse Amara Okonkwo</div>
              <div style="font-size:10px; color:var(--color-success); font-weight:700;"><i class="fa-solid fa-circle" style="font-size:7px;"></i> Online • Verified RN</div>
            </div>
          </div>

          <div style="display:flex; gap:6px;">
            <button class="btn-icon-pill" onclick="triggerAudioCall('Nurse Amara Okonkwo')"><i class="fa-solid fa-phone"></i></button>
            <button class="btn-icon-pill" onclick="triggerVideoCall('Nurse Amara Okonkwo')"><i class="fa-solid fa-video"></i></button>
          </div>
        </div>

        <!-- Quick Triage Presets -->
        <div class="chip-scroll" style="margin-bottom:10px;">
          <div class="filter-chip" onclick="sendQuickChatMessage(this.innerText)">Gate security code is #402</div>
          <div class="filter-chip" onclick="sendQuickChatMessage(this.innerText)">Wound dressing has slight discharge</div>
          <div class="filter-chip" onclick="sendQuickChatMessage(this.innerText)">Experiencing mild fever (38.1°C)</div>
        </div>

        <!-- Messages Flow -->
        <div id="chatMessagesBox" style="height:320px; overflow-y:auto; padding:10px 4px; display:flex; flex-direction:column; gap:8px;"></div>

        <!-- Emergency Alert Toggle & Input Row -->
        <div style="display:flex; align-items:center; gap:6px; margin-top:8px;">
          <button id="emergencyMsgToggle" class="btn-icon-pill" style="border-color:var(--color-emergency); color:var(--color-emergency);" onclick="toggleEmergencyMessageMode()" title="Toggle High Priority Emergency Message">
            <i class="fa-solid fa-triangle-exclamation"></i>
          </button>
          <input type="text" id="chatInputMessage" class="form-input" placeholder="Type clinical consultation note..." onkeydown="if(event.key==='Enter') sendChatMessage()">
          <button class="btn-primary" style="padding:10px 14px;" onclick="sendChatMessage()">
            <i class="fa-solid fa-paper-plane"></i>
          </button>
        </div>
      </div>

      <!-- ================= 7. UNICEF FEMTECH & MEN'S HEALTH SUITE ================= -->
      <div id="screen-femtech" class="screen-view">
        <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:10px;">
          <div>
            <div id="femtechMainTitle" style="font-size:18px; font-weight:900; color:var(--color-primary);">LifeSavers FemTech Suite</div>
            <div id="femtechSubTitle" style="font-size:11px; color:#64748B;">Maternal & Reproductive Continuum of Care</div>
          </div>
          <span class="badge-pill badge-amber">UNICEF CALL</span>
        </div>

        <!-- Suite Inner Tabs -->
        <div class="chip-scroll" style="margin-bottom:14px;">
          <div id="tabFemtech1" class="filter-chip active" onclick="switchFemtechTab(0)">Cycle & Ovulation</div>
          <div id="tabFemtech2" class="filter-chip" onclick="switchFemtechTab(1)">1,000 Days Maternal</div>
          <div id="tabFemtech3" class="filter-chip" onclick="switchFemtechTab(2)">Point-of-Care Anaemia</div>
          <div id="tabFemtech4" class="filter-chip" onclick="switchFemtechTab(3)">Privacy Shield</div>
        </div>

        <!-- Femtech Tab 0: Menstrual & Ovulation Cycle Calculator -->
        <div id="femtechContent0">
          <div class="card card-teal">
            <div style="font-size:14px; font-weight:800; margin-bottom:8px;">Menstrual & Ovulation Calculator</div>
            <div style="display:flex; justify-content:space-between; margin-bottom:4px; font-size:12px;">
              <span>Next Expected Period:</span>
              <strong id="calcNextPeriod">2026-04-12</strong>
            </div>
            <div style="display:flex; justify-content:space-between; margin-bottom:4px; font-size:12px;">
              <span>Estimated Ovulation Day:</span>
              <strong id="calcOvulation">2026-03-29</strong>
            </div>
            <div style="display:flex; justify-content:space-between; font-size:12px;">
              <span>Fertile Window Span:</span>
              <strong id="calcFertileWindow" style="color:var(--color-amber);">2026-03-25 to 2026-03-31</strong>
            </div>
          </div>

          <div class="card">
            <div style="font-size:13px; font-weight:800; color:var(--color-primary); margin-bottom:10px;">Cycle Parameters (Live Recalculate)</div>
            <div class="form-group">
              <label class="form-label">Last Period Start Date</label>
              <input type="date" id="cycleLastPeriodInput" class="form-input" value="2026-03-15" onchange="recalculateCycleMath()">
            </div>
            <div style="display:flex; gap:10px;">
              <div class="form-group" style="flex:1;">
                <label class="form-label">Cycle Length (Days)</label>
                <input type="number" id="cycleLengthInput" class="form-input" value="28" min="20" max="45" onchange="recalculateCycleMath()">
              </div>
              <div class="form-group" style="flex:1;">
                <label class="form-label">Period Length (Days)</label>
                <input type="number" id="periodLengthInput" class="form-input" value="5" min="2" max="10" onchange="recalculateCycleMath()">
              </div>
            </div>

            <div style="font-size:11px; font-weight:700; color:#475569; margin:8px 0 6px 0;">LOG CURRENT CYCLE SYMPTOMS:</div>
            <div class="chip-scroll">
              <div class="filter-chip" onclick="toggleSymptomChip(this)">Mild Cramps</div>
              <div class="filter-chip" onclick="toggleSymptomChip(this)">Normal Flow</div>
              <div class="filter-chip" onclick="toggleSymptomChip(this)">Headache</div>
              <div class="filter-chip" onclick="toggleSymptomChip(this)">Bloating</div>
              <div class="filter-chip" onclick="toggleSymptomChip(this)">Good Energy</div>
            </div>
            <button class="btn-primary" style="width:100%; font-size:12px; margin-top:6px;" onclick="saveCycleSymptoms()">
              <i class="fa-solid fa-floppy-disk"></i> Save Symptoms Record
            </button>
          </div>
        </div>

        <!-- Femtech Tab 1: Maternal 1,000 Days Milestones -->
        <div id="femtechContent1" style="display:none;">
          <div class="card card-beige">
            <div style="font-size:14px; font-weight:800; margin-bottom:4px;">Maternal Continuum & 1,000 Days Tracker</div>
            <div style="font-size:12px; margin-bottom:8px;">Current Pregnancy Stage: <strong>Week 24 of 40 (Second Trimester)</strong></div>
            <div style="width:100%; height:8px; background:#CBD5E1; border-radius:4px; overflow:hidden;">
              <div style="width:60%; height:100%; background:var(--color-primary);"></div>
            </div>
          </div>

          <div class="card">
            <div style="font-size:13px; font-weight:800; color:var(--color-primary); margin-bottom:10px;">Antenatal Care (ANC) Milestones</div>
            <div style="display:flex; flex-direction:column; gap:10px;">
              <label style="display:flex; align-items:center; gap:8px; font-size:12px;">
                <input type="checkbox" checked> 1st Trimester Ultrasound Scan (Passed)
              </label>
              <label style="display:flex; align-items:center; gap:8px; font-size:12px;">
                <input type="checkbox" checked> Tetanus Toxoid Vaccine Dose 1 & 2
              </label>
              <label style="display:flex; align-items:center; gap:8px; font-size:12px;">
                <input type="checkbox"> Glucose Tolerance Test (Scheduled Week 26)
              </label>
              <label style="display:flex; align-items:center; gap:8px; font-size:12px;">
                <input type="checkbox"> 3rd Trimester Doppler & Growth Scan
              </label>
            </div>

            <button class="btn-primary btn-coral" style="width:100%; margin-top:14px; font-size:12px;" onclick="quickBookMaternalMidwife()">
              <i class="fa-solid fa-person-breastfeeding"></i> Dispatch Community Midwife to Doorstep
            </button>
          </div>
        </div>

        <!-- Femtech Tab 2: Point-of-Care Anaemia & Maternal Vitals Risk -->
        <div id="femtechContent2" style="display:none;">
          <div class="card">
            <div style="font-size:13px; font-weight:800; color:var(--color-primary); margin-bottom:6px;">Point-of-Care Anaemia Screening</div>
            <div style="font-size:11px; color:#64748B; margin-bottom:12px;">Conjunctiva pallor clinical scale and non-invasive Hb indicator</div>

            <div class="form-group">
              <label class="form-label">Hemoglobin Level (Hb g/dL): <span id="hbValueLabel" style="font-weight:900; color:var(--color-primary);">11.8 g/dL</span></label>
              <input type="range" id="hbSlider" min="6.0" max="16.0" step="0.1" value="11.8" oninput="handleHbChange(this.value)" style="width:100%;">
            </div>

            <div id="hbRiskBox" class="badge-pill badge-success" style="width:100%; justify-content:center; padding:8px; font-size:12px; margin-bottom:10px;">
              Risk Assessment: NORMAL / LOW RISK
            </div>

            <div style="font-size:12px; color:#475569; line-height:1.4;">
              Maintain standard prophylactic iron supplementation and routine quarterly checkups.
            </div>
          </div>
        </div>

        <!-- Femtech Tab 3: Safety-by-Design Privacy & Consent Shield -->
        <div id="femtechContent3" style="display:none;">
          <div class="card">
            <div style="font-size:13px; font-weight:800; color:var(--color-primary); margin-bottom:8px;">Safety-by-Design Privacy Shield</div>
            <div style="font-size:11px; color:#64748B; margin-bottom:12px;">Zero-knowledge local on-device encryption prevents unauthorized health data disclosure.</div>

            <div style="display:flex; justify-content:space-between; align-items:center; padding:10px 0; border-bottom:1px solid #E2E8F0;">
              <div>
                <div style="font-size:12px; font-weight:800;">AES-256 On-Device Encryption</div>
                <div style="font-size:10px; color:#64748B;">All reproductive and cycle notes encrypted with hardware key</div>
              </div>
              <input type="checkbox" checked style="transform:scale(1.3);">
            </div>

            <div style="display:flex; justify-content:space-between; align-items:center; padding:10px 0; border-bottom:1px solid #E2E8F0;">
              <div>
                <div style="font-size:12px; font-weight:800;">Clinical Consent Auto-Revoke</div>
                <div style="font-size:10px; color:#64748B;">Revoke doctor record view access 48h after visit completion</div>
              </div>
              <input type="checkbox" checked style="transform:scale(1.3);">
            </div>

            <button class="btn-outlined" style="width:100%; margin-top:14px; border-color:var(--color-emergency); color:var(--color-emergency); font-size:12px;" onclick="wipeLocalHealthData()">
              <i class="fa-solid fa-trash-can"></i> Emergency 1-Tap Erase Local Health Data
            </button>
          </div>
        </div>
      </div>

      <!-- ================= 8. HEALTHCARE PROVIDER DASHBOARD (GIG RADAR) ================= -->
      <div id="screen-provider-dashboard" class="screen-view">
        <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:12px;">
          <div>
            <div style="font-size:18px; font-weight:900; color:var(--color-primary);">Healthcare Provider Mode</div>
            <div style="font-size:11px; color:#64748B;">Nurse Amara Okonkwo, RN (Lic #NMCN/RN/2019/84712)</div>
          </div>
          <span class="badge-pill badge-success"><i class="fa-solid fa-shield-halved"></i> VERIFIED</span>
        </div>

        <!-- Online/Offline Switch Card -->
        <div class="card card-teal" style="display:flex; justify-content:space-between; align-items:center;">
          <div>
            <div style="font-size:10px; font-weight:800; color:var(--color-amber); letter-spacing:0.5px;">AVAILABILITY STATUS</div>
            <div id="providerAvailabilityLabel" style="font-size:16px; font-weight:900;">ONLINE • DISPATCH READY</div>
            <div style="font-size:11px; opacity:0.85;">Receiving live patient dispatch gig requests</div>
          </div>
          <label style="position:relative; display:inline-block; width:52px; height:28px;">
            <input type="checkbox" id="providerOnlineToggle" checked onchange="toggleProviderOnlineStatus(this.checked)" style="opacity:0; width:0; height:0;">
            <span style="position:absolute; cursor:pointer; top:0; left:0; right:0; bottom:0; background-color:#16A34A; transition:.4s; border-radius:34px;"></span>
          </label>
        </div>

        <!-- Incoming Gig Dispatch Alert (Simulated) -->
        <div id="incomingGigAlertBox" class="card card-beige" style="border:2px solid var(--color-coral);">
          <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:8px;">
            <span class="badge-pill badge-coral"><i class="fa-solid fa-bolt"></i> INCOMING GIG DISPATCH</span>
            <span style="font-size:11px; font-weight:800; color:var(--color-coral);">Expires in 28s</span>
          </div>
          <div style="font-size:14px; font-weight:900; margin-bottom:2px;">Wound Dressing & Sterile Care</div>
          <div style="font-size:12px; color:#475569; margin-bottom:6px;"><i class="fa-solid fa-location-dot" style="color:var(--color-coral);"></i> 14 Adeola Odeku St, Victoria Island (0.8 km)</div>
          <div style="font-size:11px; background:rgba(0,0,0,0.05); padding:6px; border-radius:6px; margin-bottom:10px;">
            "Post-cesarean incision site needs antiseptic cleaning and fresh sterile dressing."
          </div>
          <div style="display:flex; gap:8px;">
            <button class="btn-outlined" style="flex:1; padding:8px; font-size:11px;" onclick="declineGigAlert()">Pass</button>
            <button class="btn-primary" style="flex:1.5; padding:8px; font-size:11px; background:var(--color-coral);" onclick="acceptGigAlert()">
              <i class="fa-solid fa-check"></i> Accept Dispatch (₦18,000)
            </button>
          </div>
        </div>

        <!-- Provider Wallet & Earnings -->
        <div class="card">
          <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:10px;">
            <div style="font-size:14px; font-weight:800; color:var(--color-primary);"><i class="fa-solid fa-wallet"></i> Earnings & Wallet</div>
            <button class="btn-outlined" style="padding:4px 10px; font-size:10px;" onclick="requestPayout()">Payout</button>
          </div>

          <div style="display:grid; grid-template-columns:1fr 1fr; gap:10px; margin-bottom:12px;">
            <div style="background:var(--color-light-mint); padding:10px; border-radius:var(--radius-md);">
              <div style="font-size:10px; color:#64748B;">Total Earnings</div>
              <div style="font-size:16px; font-weight:900; color:var(--color-primary);">₦184,500</div>
            </div>
            <div style="background:var(--color-light-mint); padding:10px; border-radius:var(--radius-md);">
              <div style="font-size:10px; color:#64748B;">Available Balance</div>
              <div style="font-size:16px; font-weight:900; color:var(--color-success);">₦52,000</div>
            </div>
          </div>

          <div style="font-size:11px; color:#64748B; margin-bottom:4px;">14 Completed Gigs • 15% Platform & Regulatory Levy Deducted</div>
        </div>
      </div>

      <!-- ================= 9. CREDENTIALS & AUDIT SCREEN ================= -->
      <div id="screen-credentials" class="screen-view">
        <div style="font-size:18px; font-weight:900; color:var(--color-primary); margin-bottom:4px;">Credentialing & Regulatory Audit</div>
        <div style="font-size:11px; color:#64748B; margin-bottom:12px;">LifeSavers Clinical Safety & Healthcare Worker Registry Audit</div>

        <!-- 2 Tabs: Provider Dossier vs Admin Audit Portal -->
        <div class="chip-scroll" style="margin-bottom:12px;">
          <div id="tabCred0" class="filter-chip active" onclick="switchCredTab(0)">Provider Dossier</div>
          <div id="tabCred1" class="filter-chip" onclick="switchCredTab(1)">Admin Audit Portal</div>
        </div>

        <div id="credContent0">
          <div class="card card-teal">
            <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:6px;">
              <span class="badge-pill badge-amber">VERIFICATION STATUS</span>
              <span style="font-weight:800; font-size:12px;">GOVERNMENT VERIFIED</span>
            </div>
            <div style="font-size:15px; font-weight:900;">Nurse Amara Okonkwo, RN</div>
            <div style="font-size:11px; color:var(--color-ice-mint);">Nursing & Midwifery Council of Nigeria (NMCN)</div>
            <div style="font-size:11px; margin-top:4px;">License #: <strong>NMCN/RN/2019/84712</strong> (Exp: 2027-11-30)</div>
          </div>

          <div class="card">
            <div style="font-size:13px; font-weight:800; color:var(--color-primary); margin-bottom:10px;">Update Practicing License Dossier</div>

            <div class="form-group">
              <label class="form-label">Medical Council / Regulatory Board</label>
              <select id="credCouncilSelect" class="form-select">
                <option value="NMCN">Nursing and Midwifery Council of Nigeria (NMCN)</option>
                <option value="MDCN">Medical and Dental Council of Nigeria (MDCN)</option>
                <option value="MRTB">Medical Rehabilitation Therapists Board (MRTB)</option>
                <option value="CASSON">Counselling Association of Nigeria (CASSON)</option>
                <option value="CHPRBN">Community Health Practitioners Board (CHPRBN)</option>
              </select>
            </div>

            <div class="form-group">
              <label class="form-label">Practicing License Folio Number</label>
              <input type="text" id="credLicenseNum" class="form-input" value="NMCN/RN/2019/84712">
            </div>

            <div class="form-group">
              <label class="form-label">National Identity (NIN / BVN)</label>
              <input type="text" id="credNationalId" class="form-input" value="****-****-3821">
            </div>

            <div class="form-group">
              <label class="form-label">License Certificate Document Upload</label>
              <input type="file" id="credDocFile" class="form-input" onchange="simulateSha256Hash(this)">
              <div id="credDocSha256" style="font-size:10px; color:#64748B; margin-top:4px; word-break:break-all;">
                SHA-256 Hash: c5d2b7e192a5482b4510b001a1154c5e3176191c496a79851722883f3ba9e782
              </div>
            </div>

            <button class="btn-primary" style="width:100%; font-size:12px;" onclick="submitCredentialDossier()">
              <i class="fa-solid fa-cloud-arrow-up"></i> Submit for Regulatory Verification
            </button>
          </div>
        </div>

        <div id="credContent1" style="display:none;">
          <div class="card">
            <div style="font-size:13px; font-weight:800; color:var(--color-primary); margin-bottom:10px;">Clinical Audit Review Portal</div>
            <div id="adminCredReviewList"></div>
          </div>
        </div>
      </div>

    </div>

    <!-- Bottom Navigation Bar -->
    <div id="bottomNavBar" class="bottom-nav">
      <!-- Generated dynamically based on Role (Patient vs Provider) -->
    </div>

    <!-- ================= MODALS & SHEETS ================= -->

    <!-- Doorstep Address Picker Dialog -->
    <div id="modalAddress" class="modal-overlay" onclick="if(event.target===this) closeAddressDialog()">
      <div class="bottom-sheet">
        <div class="sheet-handle"></div>
        <div style="font-size:16px; font-weight:900; color:var(--color-primary); margin-bottom:8px;">Change Doorstep Location</div>
        <div style="font-size:11px; color:#64748B; margin-bottom:12px;">Match with the closest verified healthcare worker within 2km:</div>

        <button class="btn-primary" style="width:100%; margin-bottom:12px; font-size:12px;" onclick="useGpsCurrentLocation()">
          <i class="fa-solid fa-location-crosshairs"></i> Use Current GPS Coordinates (VI Lagos)
        </button>

        <div class="form-group">
          <label class="form-label">Doorstep Address</label>
          <input type="text" id="addressInputModal" class="form-input" value="14 Adeola Odeku St, Victoria Island, Lagos">
        </div>

        <div style="font-size:11px; font-weight:700; color:#475569; margin-bottom:6px;">QUICK NIGERIAN PRESETS:</div>
        <div style="display:flex; flex-direction:column; gap:6px; margin-bottom:16px;">
          <button class="filter-chip" style="text-align:left; border-radius:8px;" onclick="selectAddressPreset('14 Adeola Odeku St, Victoria Island, Lagos')">14 Adeola Odeku St, Victoria Island, Lagos</button>
          <button class="filter-chip" style="text-align:left; border-radius:8px;" onclick="selectAddressPreset('Block 4, Admiralty Way, Lekki Phase 1, Lagos')">Block 4, Admiralty Way, Lekki Phase 1, Lagos</button>
          <button class="filter-chip" style="text-align:left; border-radius:8px;" onclick="selectAddressPreset('28 Isaac John St, Ikeja GRA, Lagos')">28 Isaac John St, Ikeja GRA, Lagos</button>
          <button class="filter-chip" style="text-align:left; border-radius:8px;" onclick="selectAddressPreset('12 Aminu Kano Crescent, Wuse II, Abuja')">12 Aminu Kano Crescent, Wuse II, Abuja</button>
        </div>

        <button class="btn-primary" style="width:100%;" onclick="saveAddressDialog()">Update Doorstep Location</button>
      </div>
    </div>

    <!-- Medical Supplies Cart Drawer -->
    <div id="modalCart" class="modal-overlay" onclick="if(event.target===this) closeCartDrawer()">
      <div class="bottom-sheet">
        <div class="sheet-handle"></div>
        <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:12px;">
          <div style="font-size:16px; font-weight:900; color:var(--color-primary);"><i class="fa-solid fa-kit-medical"></i> Medical Supplies Cart</div>
          <span id="cartDrawerCount" class="badge-pill badge-mint">2 Items</span>
        </div>

        <div id="cartItemsList" style="max-height:240px; overflow-y:auto; margin-bottom:14px;"></div>

        <div class="card card-beige" style="margin-bottom:12px;">
          <div style="display:flex; justify-content:space-between; font-size:12px; margin-bottom:4px;">
            <span>Supplies Subtotal:</span>
            <strong id="cartSubtotal">₦5,700</strong>
          </div>
          <div style="display:flex; justify-content:space-between; font-size:12px; margin-bottom:4px;">
            <span>Doorstep Courier Delivery:</span>
            <span>₦1,000</span>
          </div>
          <div style="border-top:1px dashed #CBD5E1; padding-top:6px; display:flex; justify-content:space-between; font-size:15px; font-weight:900; color:var(--color-primary);">
            <span>Cart Total:</span>
            <strong id="cartTotal">₦6,700</strong>
          </div>
        </div>

        <button class="btn-primary" style="width:100%;" onclick="checkoutSuppliesOrder()">
          <i class="fa-solid fa-check"></i> Dispatch Supplies with Nurse Visit
        </button>
      </div>
    </div>

    <!-- Supabase Cloud Sync Sheet -->
    <div id="modalSupabase" class="modal-overlay" onclick="if(event.target===this) closeSupabaseSheet()">
      <div class="bottom-sheet">
        <div class="sheet-handle"></div>
        <div style="display:flex; align-items:center; gap:10px; margin-bottom:12px;">
          <div style="width:40px; height:40px; border-radius:50%; background:var(--color-teal-container); color:var(--color-primary); display:flex; align-items:center; justify-content:center; font-size:18px;">
            <i class="fa-solid fa-cloud"></i>
          </div>
          <div>
            <div style="font-size:16px; font-weight:900; color:var(--color-primary);">Supabase Cloud Sync</div>
            <div style="font-size:11px; color:#64748B;">Realtime WebSocket Sync & Offline Backup</div>
          </div>
        </div>

        <div class="card" style="margin-bottom:12px;">
          <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:8px;">
            <span style="font-size:12px; font-weight:700;">Backend Status:</span>
            <span class="badge-pill badge-success"><i class="fa-solid fa-circle" style="font-size:7px;"></i> CONNECTED (200 OK)</span>
          </div>
          <div style="font-size:11px; color:#64748B;">Project URL: <code>https://npx...supabase.co</code></div>
          <div style="font-size:11px; color:#64748B;">Channels: <code>dispatches</code>, <code>consult_chat</code>, <code>credentials</code></div>
        </div>

        <div style="display:flex; gap:8px;">
          <button class="btn-primary" style="flex:1; font-size:12px;" onclick="triggerCloudBackup()">
            <i class="fa-solid fa-upload"></i> Backup to Cloud
          </button>
          <button class="btn-outlined" style="flex:1; font-size:12px;" onclick="triggerCloudPull()">
            <i class="fa-solid fa-download"></i> Pull Cloud Updates
          </button>
        </div>
      </div>
    </div>

    <!-- User Profile Sheet (4 Tabs) -->
    <div id="modalProfile" class="modal-overlay" onclick="if(event.target===this) closeProfileSheet()">
      <div class="bottom-sheet" style="max-height:94%;">
        <div class="sheet-handle"></div>
        <div style="display:flex; align-items:center; gap:12px; margin-bottom:12px;">
          <div style="width:48px; height:48px; border-radius:50%; background:var(--color-primary); color:white; display:flex; align-items:center; justify-content:center; font-size:18px; font-weight:900;">CA</div>
          <div>
            <div style="font-size:16px; font-weight:900;">Chidi Anya</div>
            <div style="font-size:11px; color:#64748B;">chidi.anya@lifesavershealth.org • +234 803 555 0192</div>
          </div>
        </div>

        <!-- 4 Profile Tabs -->
        <div class="chip-scroll" style="margin-bottom:12px;">
          <div id="tabProf0" class="filter-chip active" onclick="switchProfileTab(0)">Bookings</div>
          <div id="tabProf1" class="filter-chip" onclick="switchProfileTab(1)">Reviews</div>
          <div id="tabProf2" class="filter-chip" onclick="switchProfileTab(2)">Personal & Medical</div>
          <div id="tabProf3" class="filter-chip" onclick="switchProfileTab(3)">Favorites</div>
        </div>

        <div id="profContent0">
          <div id="profileBookingsList"></div>
        </div>
        <div id="profContent1" style="display:none;">
          <div id="profileReviewsList"></div>
        </div>
        <div id="profContent2" style="display:none;">
          <div class="card">
            <div class="form-group">
              <label class="form-label">Blood Group</label>
              <input type="text" class="form-input" value="O+">
            </div>
            <div class="form-group">
              <label class="form-label">Genotype</label>
              <input type="text" class="form-input" value="AA">
            </div>
            <div class="form-group">
              <label class="form-label">Known Allergies</label>
              <input type="text" class="form-input" value="Penicillin, Sulfa antibiotics">
            </div>
            <div class="form-group">
              <label class="form-label">Emergency Contact</label>
              <input type="text" class="form-input" value="Ngozi Anya (Spouse) • +234 802 991 4422">
            </div>
            <button class="btn-primary" style="width:100%; font-size:12px;" onclick="savePersonalProfile()">Save Health Profile</button>
          </div>
        </div>
        <div id="profContent3" style="display:none;">
          <div id="profileFavoritesList"></div>
        </div>
      </div>
    </div>

    <!-- Audio Call Overlay -->
    <div id="modalAudioCall" class="modal-overlay" style="align-items:center;">
      <div class="card card-teal" style="width:90%; max-width:340px; text-align:center; padding:30px 20px;">
        <div style="width:80px; height:80px; border-radius:50%; background:var(--color-teal-container); color:var(--color-primary); display:flex; align-items:center; justify-content:center; font-size:32px; margin:0 auto 16px auto;">
          <i class="fa-solid fa-phone-volume"></i>
        </div>
        <div id="audioCallDoctorName" style="font-size:18px; font-weight:900;">Nurse Amara Okonkwo</div>
        <div style="font-size:12px; color:var(--color-ice-mint); margin-bottom:24px;">Encrypted Clinical Voice Consultation • 00:42</div>
        <div style="display:flex; justify-content:center; gap:16px;">
          <button class="btn-icon-pill" style="width:48px; height:48px; font-size:18px;"><i class="fa-solid fa-microphone-slash"></i></button>
          <button class="btn-icon-pill" style="width:48px; height:48px; font-size:18px; background:var(--color-emergency); color:white;" onclick="endAudioCall()"><i class="fa-solid fa-phone-slash"></i></button>
        </div>
      </div>
    </div>

    <!-- Video Call Overlay -->
    <div id="modalVideoCall" class="modal-overlay" style="align-items:center;">
      <div class="card" style="width:94%; max-width:380px; text-align:center; padding:16px; background:#002522; color:white;">
        <div style="width:100%; height:260px; background:#113b37; border-radius:var(--radius-md); position:relative; overflow:hidden; margin-bottom:14px; display:flex; align-items:center; justify-content:center;">
          <img src="{team_b64}" style="width:100%; height:100%; object-fit:cover; opacity:0.85;">
          <div style="position:absolute; bottom:10px; left:10px; font-size:11px; background:rgba(0,0,0,0.6); padding:4px 8px; border-radius:4px;">
            Dr. Tunde Balogun (Online GP)
          </div>
          <!-- Self PiP -->
          <div style="position:absolute; top:10px; right:10px; width:65px; height:85px; border-radius:8px; background:#222; border:1.5px solid white; overflow:hidden;">
            <div style="font-size:9px; margin-top:30px;">Patient</div>
          </div>
        </div>
        <div style="display:flex; justify-content:center; gap:16px;">
          <button class="btn-icon-pill" style="width:44px; height:44px;"><i class="fa-solid fa-video-slash"></i></button>
          <button class="btn-icon-pill" style="width:44px; height:44px;"><i class="fa-solid fa-microphone-slash"></i></button>
          <button class="btn-icon-pill" style="width:44px; height:44px; background:var(--color-emergency); color:white;" onclick="endVideoCall()"><i class="fa-solid fa-phone-slash"></i></button>
        </div>
      </div>
    </div>

  </div>

  <!-- Application Logic & Data -->
  <script>
    // State management mirroring LifeSaversRepository
    const state = {{
      currentRole: 'PATIENT', // 'PATIENT' or 'PROVIDER'
      userGender: 'FEMALE',
      userAddress: '14 Adeola Odeku St, Victoria Island, Lagos',
      activeScreen: 'screen-home',
      screenHistory: ['screen-home'],
      cart: [
        {{ id: 'sup-01', name: 'Sterile Gauze & Dressing Pack', price: 3500, qty: 1 }},
        {{ id: 'sup-02', name: 'Povidone-Iodine Antiseptic (100ml)', price: 2200, qty: 1 }}
      ],
      activeBooking: {{
        id: 'LIFESAVERS-89412',
        serviceName: 'Wound Dressing & Sterile Care',
        providerName: 'Nurse Amara Okonkwo',
        providerSpecialty: 'Registered Nurse',
        status: 'EN_ROUTE', // SEARCHING, MATCHED, EN_ROUTE, ARRIVED, IN_PROGRESS, COMPLETED
        etaMinutes: 7,
        totalRate: 21700
      }},
      selectedProvider: null,
      selectedService: null,
      isEmergencyChat: false,
      chatMessages: [
        {{ id: 'm1', sender: 'Nurse Amara', text: 'Hello Chidi, I have accepted your wound care dispatch and I am en route with the sterile kit.', time: '14:20', isFromPatient: false, isEmergency: false }},
        {{ id: 'm2', sender: 'Chidi Anya', text: 'Thank you nurse! Gate security code is #402 when you arrive at Adeola Odeku.', time: '14:22', isFromPatient: true, isEmergency: false }}
      ],
      providers: [
        {{
          id: 'prov-00',
          name: 'Funmi Adewale',
          title: 'Certified Child Caregiver',
          specialty: 'CHILD_CARE_GIVER',
          rating: 4.99,
          reviews: 186,
          distance: 0.7,
          eta: 6,
          rate: 8000,
          license: 'CCG-LAG-90001',
          initials: 'FA',
          experience: 8,
          gigs: 245,
          bio: 'Experienced child caregiver providing nurturing care, feeding, hygiene, monitoring and developmental support at home for infants and toddlers.',
          services: ['Infant Care', 'Toddler Supervision', 'Child Hygiene & Feeding', 'Night-time Child Care']
        }},
        {{
          id: 'prov-00b',
          name: 'Grace Okoye',
          title: 'Certified Elderly Caregiver',
          specialty: 'ELDERLY_CARE_GIVER',
          rating: 4.99,
          reviews: 214,
          distance: 0.8,
          eta: 7,
          rate: 9000,
          license: 'ECG-LAG-90002',
          initials: 'GO',
          experience: 10,
          gigs: 298,
          bio: 'Compassionate elderly caregiver offering mobility assistance, medication reminders, companionship, hygiene, dementia care and fall prevention.',
          services: ['Elderly Companionship', 'Mobility Assistance', 'Medication Reminders', 'Dementia Care Support']
        }},
        {{
          id: 'prov-01',
          name: 'Nurse Amara Okonkwo',
          title: 'RN, Critical & Post-Op Wound Care Specialist',
          specialty: 'NURSE',
          rating: 4.96,
          reviews: 138,
          distance: 0.8,
          eta: 9,
          rate: 18000,
          license: 'NMCN/RN/2019/84712',
          initials: 'AO',
          experience: 7,
          gigs: 320,
          bio: 'Certified emergency and community nurse with over 7 years in acute patient care, wound debridement, and post-cesarean recovery support.',
          services: ['Wound Dressing & Debridement', 'IV Cannulation & Infusion', 'Post-Natal Maternal Check', 'Vitals Monitoring']
        }},
        {{
          id: 'prov-02',
          name: 'Dr. Tunde Balogun',
          title: 'MD, Online Doctor & Teleconsultant GP',
          specialty: 'DOCTOR',
          rating: 4.92,
          reviews: 94,
          distance: 1.4,
          eta: 14,
          rate: 32000,
          license: 'MDCN/49102/2017',
          initials: 'TB',
          experience: 9,
          gigs: 210,
          bio: 'Consultant family physician offering online video/audio teleconsultations, home clinical assessment, e-prescriptions, and emergency triage.',
          services: ['Online Doctor Video Consultation', 'E-Prescription & Lab Ordering', 'Pediatric Fever Assessment', 'Elderly Health Review']
        }},
        {{
          id: 'prov-03',
          name: 'Blessing Eze',
          title: 'B.Physio, Orthopedic & Stroke Rehab Specialist',
          specialty: 'PHYSIOTHERAPIST',
          rating: 4.98,
          reviews: 76,
          distance: 1.9,
          eta: 18,
          rate: 22000,
          license: 'MRTB/PT/2021/3391',
          initials: 'BE',
          experience: 5,
          gigs: 145,
          bio: 'Licensed physiotherapist focusing on mobility recovery, post-fracture joint therapy, stroke motor retraining, and ergonomic posture relief.',
          services: ['Post-Stroke Mobility Rehab', 'Chronic Lower Back Therapy', 'Post-Op Joint Mobilization', 'Elderly Fall Prevention']
        }},
        {{
          id: 'prov-05',
          name: 'Halima Musa',
          title: 'CHEW, Certified Community Health Worker',
          specialty: 'COMMUNITY_HEALTH',
          rating: 4.94,
          reviews: 112,
          distance: 0.5,
          eta: 6,
          rate: 9500,
          license: 'CHPRBN/CHW/2022/881',
          initials: 'HM',
          experience: 4,
          gigs: 260,
          bio: 'Community health officer dedicated to accessible neighborhood healthcare, rapid malaria/typhoid test screening, immunization tracking, and routine vitals.',
          services: ['Rapid Malaria RDT & Vitals', 'Blood Pressure & Sugar Check', 'Childhood Immunization Audit', 'Medication Adherence Support']
        }},
        {{
          id: 'prov-06',
          name: 'Nurse Blessing Ejiofor',
          title: 'RN, RM, Sexual & Reproductive Health Specialist',
          specialty: 'SEXUAL_REPRODUCTIVE_HEALTH',
          rating: 4.98,
          reviews: 142,
          distance: 1.1,
          eta: 8,
          rate: 18000,
          license: 'NMCN/RM/2018/4412',
          initials: 'BE',
          experience: 8,
          gigs: 280,
          bio: 'Certified Nurse Midwife and Reproductive Health Officer specializing in confidential family planning, cervical HPV screening, lactation, and female wellness.',
          services: ['Cervical HPV & Pap Smear Screening', 'Contraceptive & Family Planning', 'Confidential STI Care', 'Post-Natal Care']
        }}
      ],
      services: [
        {{ id: 'srv-00', name: 'Child Care & Supervision', specialty: 'CHILD_CARE_GIVER', duration: 240, rate: 8000, desc: 'Nurturing in-home childcare: feeding, hygiene, play, naps and safety monitoring.' }},
        {{ id: 'srv-00b', name: 'Elderly Care & Companionship', specialty: 'ELDERLY_CARE_GIVER', duration: 240, rate: 9000, desc: 'Mobility assistance, medication reminders, companionship, hygiene and fall prevention for seniors.' }},
        {{ id: 'srv-01', name: 'Wound Dressing & Debridement', specialty: 'NURSE', duration: 30, rate: 15000, desc: 'Aseptic wound cleaning, dressing change and infection monitoring at home.' }},
        {{ id: 'srv-02', name: 'IV Fluid Cannulation & Infusion', specialty: 'NURSE', duration: 60, rate: 18000, desc: 'Safe intravenous line placement and prescribed infusion monitoring.' }},
        {{ id: 'srv-03', name: 'Online Doctor Video Consultation', specialty: 'DOCTOR', duration: 45, rate: 30000, desc: 'Private online video/audio medical consultation, electronic prescription, and diagnosis.' }},
        {{ id: 'srv-05', name: 'In-Home Physiotherapy & Mobility', specialty: 'PHYSIOTHERAPIST', duration: 50, rate: 22000, desc: 'Manual therapy, stroke recovery gait retraining, and joint decompression.' }},
        {{ id: 'srv-07', name: 'Rapid Vitals & Diagnostic Screening', specialty: 'COMMUNITY_HEALTH', duration: 30, rate: 9500, desc: 'Blood pressure, oxygen saturation, random blood glucose, and malaria RDT.' }}
      ]
    }};

    // Initialization
    window.addEventListener('DOMContentLoaded', () => {{
      renderHomeServices();
      renderHomeProviders();
      renderDirectoryProviders();
      renderBottomNav();
      renderChatMessages();
      renderCartDrawer();
      renderProfileHistory();
    }});

    // Navigation System
    function navigateToScreen(screenId) {{
      document.querySelectorAll('.screen-view').forEach(el => el.classList.remove('active'));
      const target = document.getElementById(screenId);
      if (target) {{
        target.classList.add('active');
        state.activeScreen = screenId;
        state.screenHistory.push(screenId);
      }}
      renderBottomNav();
      window.scrollTo(0, 0);
    }}

    function navigateBack() {{
      if (state.screenHistory.length > 1) {{
        state.screenHistory.pop();
        const prev = state.screenHistory[state.screenHistory.length - 1];
        document.querySelectorAll('.screen-view').forEach(el => el.classList.remove('active'));
        document.getElementById(prev).classList.add('active');
        state.activeScreen = prev;
        renderBottomNav();
      }} else {{
        navigateToScreen('screen-home');
      }}
    }}

    // Render Bottom Nav Dynamically
    function renderBottomNav() {{
      const nav = document.getElementById('bottomNavBar');
      if (state.currentRole === 'PATIENT') {{
        nav.innerHTML = `
          <div class="nav-item ${{state.activeScreen === 'screen-home' ? 'active' : ''}}" onclick="navigateToScreen('screen-home')">
            <i class="fa-solid fa-house"></i>
            <span>Home</span>
          </div>
          <div class="nav-item ${{state.activeScreen === 'screen-providers' ? 'active' : ''}}" onclick="navigateToScreen('screen-providers')">
            <i class="fa-solid fa-user-doctor"></i>
            <span>Providers</span>
          </div>
          <div class="nav-item ${{state.activeScreen === 'screen-femtech' ? 'active' : ''}}" onclick="navigateToScreen('screen-femtech')">
            <i class="fa-solid fa-venus"></i>
            <span>FemTech</span>
          </div>
          <div class="nav-item ${{state.activeScreen === 'screen-tracker' ? 'active' : ''}}" onclick="navigateToScreen('screen-tracker')">
            <i class="fa-solid fa-bolt"></i>
            <span>Tracker</span>
          </div>
          <div class="nav-item ${{state.activeScreen === 'screen-chat' ? 'active' : ''}}" onclick="navigateToScreen('screen-chat')">
            <i class="fa-solid fa-comments"></i>
            <span>Consult</span>
          </div>
        `;
      }} else {{
        // Provider Navigation
        nav.innerHTML = `
          <div class="nav-item provider-mode ${{state.activeScreen === 'screen-provider-dashboard' ? 'active' : ''}}" onclick="navigateToScreen('screen-provider-dashboard')">
            <i class="fa-solid fa-radar"></i>
            <span>Gig Radar</span>
          </div>
          <div class="nav-item provider-mode ${{state.activeScreen === 'screen-credentials' ? 'active' : ''}}" onclick="navigateToScreen('screen-credentials')">
            <i class="fa-solid fa-certificate"></i>
            <span>Credentials</span>
          </div>
          <div class="nav-item provider-mode ${{state.activeScreen === 'screen-chat' ? 'active' : ''}}" onclick="navigateToScreen('screen-chat')">
            <i class="fa-solid fa-comments"></i>
            <span>Consult</span>
          </div>
        `;
      }}
    }}

    // Toggle Role (Patient <-> Provider)
    function toggleUserRole() {{
      if (state.currentRole === 'PATIENT') {{
        state.currentRole = 'PROVIDER';
        document.getElementById('roleLabel').innerText = 'Provider';
        document.getElementById('roleToggleBtn').classList.add('provider');
        showToast('Switched to Healthcare Provider Mode (Radar & Earnings)');
        navigateToScreen('screen-provider-dashboard');
      }} else {{
        state.currentRole = 'PATIENT';
        document.getElementById('roleLabel').innerText = 'Patient';
        document.getElementById('roleToggleBtn').classList.remove('provider');
        showToast('Switched to Patient Doorstep Mode');
        navigateToScreen('screen-home');
      }}
      renderBottomNav();
    }}

    // Render Services on Home
    function renderHomeServices() {{
      const container = document.getElementById('homeServicesContainer');
      container.innerHTML = state.services.map(s => `
        <div class="card" style="display:flex; justify-content:space-between; align-items:center; cursor:pointer;" onclick="startBookingFlow('${{s.id}}')">
          <div style="flex:1;">
            <div style="font-size:14px; font-weight:800; color:var(--color-primary);">${{s.name}}</div>
            <div style="font-size:11px; color:#64748B; margin:2px 0 4px 0;">~${{s.duration}} mins • Rate set by provider</div>
            <div style="font-size:11px; opacity:0.8; line-height:1.3;">${{s.desc}}</div>
          </div>
          <button class="btn-primary" style="padding:7px 12px; font-size:11px; margin-left:12px;">Book</button>
        </div>
      `).join('');
    }}

    // Render Providers on Home
    function renderHomeProviders() {{
      const container = document.getElementById('homeProvidersContainer');
      container.innerHTML = state.providers.slice(0, 3).map(p => `
        <div class="card" style="display:flex; justify-content:space-between; align-items:center; cursor:pointer;" onclick="openProviderDetail('${{p.id}}')">
          <div style="display:flex; align-items:center; gap:12px;">
            <div style="width:44px; height:44px; border-radius:50%; background:var(--color-teal-container); color:var(--color-primary); display:flex; align-items:center; justify-content:center; font-weight:900; font-size:15px;">
              ${{p.initials}}
            </div>
            <div>
              <div style="display:flex; align-items:center; gap:4px;">
                <span style="font-size:13px; font-weight:800;">${{p.name}}</span>
                <i class="fa-solid fa-circle-check" style="color:var(--color-verified-blue); font-size:12px;"></i>
              </div>
              <div style="font-size:11px; color:#64748B;">${{p.title}}</div>
              <div style="font-size:11px; margin-top:2px;">
                <span style="color:var(--color-amber); font-weight:700;"><i class="fa-solid fa-star"></i> ${{p.rating}} (${{p.reviews}})</span>
                <span style="color:var(--color-primary); font-weight:600;"> • ${{p.distance}} km • ETA ~${{p.eta}}m</span>
              </div>
            </div>
          </div>
          <i class="fa-solid fa-chevron-right" style="color:#94A3B8; font-size:12px;"></i>
        </div>
      `).join('');
    }}

    // Render Directory Providers
    function renderDirectoryProviders(list = state.providers) {{
      const container = document.getElementById('directoryProvidersContainer');
      container.innerHTML = list.map(p => `
        <div class="card" style="margin-bottom:12px; cursor:pointer;" onclick="openProviderDetail('${{p.id}}')">
          <div style="display:flex; justify-content:space-between; align-items:flex-start;">
            <div style="display:flex; align-items:center; gap:12px;">
              <div style="width:46px; height:46px; border-radius:50%; background:var(--color-teal-container); color:var(--color-primary); display:flex; align-items:center; justify-content:center; font-weight:900;">
                ${{p.initials}}
              </div>
              <div>
                <div style="display:flex; align-items:center; gap:4px;">
                  <span style="font-size:14px; font-weight:800;">${{p.name}}</span>
                  <i class="fa-solid fa-circle-check" style="color:var(--color-verified-blue); font-size:12px;"></i>
                </div>
                <div style="font-size:11px; color:#64748B;">${{p.title}}</div>
              </div>
            </div>
            <span class="badge-pill badge-mint">Available</span>
          </div>

          <div style="margin:10px 0 6px 0; font-size:11px; color:#475569; line-height:1.4;">${{p.bio}}</div>

          <div style="display:flex; justify-content:space-between; align-items:center; border-top:1px solid #F1F5F9; padding-top:8px; margin-top:8px;">
            <div style="font-size:11px;">
              <span style="color:var(--color-amber); font-weight:700;"><i class="fa-solid fa-star"></i> ${{p.rating}}</span>
              <span style="color:#64748B;"> • ${{p.distance}} km away</span>
              <strong style="color:var(--color-primary);"> • ₦${{p.rate.toLocaleString()}} / visit</strong>
            </div>
            <button class="btn-primary" style="padding:6px 12px; font-size:11px;" onclick="event.stopPropagation(); startBookingWithProvider('${{p.id}}')">
              Book Visit
            </button>
          </div>
        </div>
      `).join('');
    }}

    // Filter Care Category
    function filterCareCategory(cat, el) {{
      document.querySelectorAll('.chip-scroll .filter-chip').forEach(c => c.classList.remove('active'));
      el.classList.add('active');
      if (cat === 'ALL') {{
        renderDirectoryProviders(state.providers);
      }} else {{
        const filtered = state.providers.filter(p => p.specialty === cat);
        renderDirectoryProviders(filtered.length ? filtered : state.providers);
      }}
    }}

    // Search Providers
    function handleProviderSearch() {{
      const q = document.getElementById('providerSearchInput').value.toLowerCase().trim();
      if (!q) {{
        renderDirectoryProviders(state.providers);
        return;
      }}
      const res = state.providers.filter(p =>
        p.name.toLowerCase().includes(q) ||
        p.title.toLowerCase().includes(q) ||
        p.specialty.toLowerCase().includes(q) ||
        p.services.some(s => s.toLowerCase().includes(q))
      );
      renderDirectoryProviders(res);
    }}

    // Open Provider Detail Screen
    function openProviderDetail(providerId) {{
      const p = state.providers.find(x => x.id === providerId) || state.providers[0];
      state.selectedProvider = p;

      const container = document.getElementById('providerDetailContent');
      container.innerHTML = `
        <!-- Header Profile Card -->
        <div class="card card-teal">
          <div style="display:flex; align-items:center; gap:14px; margin-bottom:14px;">
            <div style="width:62px; height:62px; border-radius:50%; background:var(--color-teal-container); color:var(--color-primary); display:flex; align-items:center; justify-content:center; font-size:24px; font-weight:900;">
              ${{p.initials}}
            </div>
            <div>
              <div style="display:flex; align-items:center; gap:6px;">
                <span style="font-size:17px; font-weight:900; color:white;">${{p.name}}</span>
                <i class="fa-solid fa-circle-check" style="color:var(--color-amber); font-size:16px;"></i>
              </div>
              <div style="font-size:12px; color:var(--color-ice-mint);">${{p.title}}</div>
              <span class="badge-pill" style="background:rgba(255,255,255,0.2); color:white; margin-top:4px;">
                ${{p.specialty}} • Verified License
              </span>
            </div>
          </div>

          <!-- Stats Grid -->
          <div style="display:grid; grid-template-columns:repeat(4, 1fr); gap:6px; background:rgba(0,0,0,0.15); padding:10px; border-radius:var(--radius-md); text-align:center;">
            <div>
              <div style="font-size:9px; color:var(--color-ice-mint);">Rating</div>
              <div style="font-size:12px; font-weight:800; color:var(--color-amber);">${{p.rating}} ★</div>
            </div>
            <div>
              <div style="font-size:9px; color:var(--color-ice-mint);">Distance</div>
              <div style="font-size:12px; font-weight:800; color:white;">${{p.distance}} km</div>
            </div>
            <div>
              <div style="font-size:9px; color:var(--color-ice-mint);">Experience</div>
              <div style="font-size:12px; font-weight:800; color:white;">${{p.experience}} Yrs</div>
            </div>
            <div>
              <div style="font-size:9px; color:var(--color-ice-mint);">Completed</div>
              <div style="font-size:12px; font-weight:800; color:var(--color-ice-mint);">${{p.gigs}} Gigs</div>
            </div>
          </div>
        </div>

        <!-- Credential Verification Card -->
        <div class="card">
          <div style="display:flex; align-items:center; gap:8px; margin-bottom:8px;">
            <i class="fa-solid fa-certificate" style="color:var(--color-primary); font-size:18px;"></i>
            <span style="font-size:13px; font-weight:800; color:var(--color-primary);">Medical Board Credential Verification</span>
          </div>
          <div style="display:flex; justify-content:space-between; font-size:12px; margin-bottom:4px;">
            <span style="color:#64748B;">License Folio:</span>
            <strong>${{p.license}}</strong>
          </div>
          <div style="display:flex; justify-content:space-between; font-size:12px; margin-bottom:4px;">
            <span style="color:#64748B;">Verification Status:</span>
            <span style="color:var(--color-verified-blue); font-weight:700;"><i class="fa-solid fa-circle-check"></i> Government & Board Verified</span>
          </div>
          <div style="display:flex; justify-content:space-between; font-size:12px;">
            <span style="color:#64748B;">Background Check:</span>
            <span style="color:var(--color-success); font-weight:700;">Passed (Clean CID Record)</span>
          </div>
        </div>

        <!-- About Bio -->
        <div class="card">
          <div style="font-size:13px; font-weight:800; color:var(--color-primary); margin-bottom:6px;">About Healthcare Specialist</div>
          <div style="font-size:12px; color:#475569; line-height:1.5;">${{p.bio}}</div>
        </div>

        <!-- Clinical Services List -->
        <div class="card">
          <div style="font-size:13px; font-weight:800; color:var(--color-primary); margin-bottom:8px;">Clinical Procedures Offered at Doorstep</div>
          <div style="display:flex; flex-direction:column; gap:6px;">
            ${{p.services.map(s => `
              <div style="display:flex; align-items:center; gap:8px; font-size:12px;">
                <i class="fa-solid fa-check" style="color:var(--color-primary);"></i> ${{s}}
              </div>
            `).join('')}}
          </div>
        </div>

        <!-- Bottom Action Bar -->
        <div style="display:flex; gap:10px; margin-top:14px;">
          <button class="btn-outlined" style="flex:1;" onclick="navigateToScreen('screen-chat')">
            <i class="fa-solid fa-comments"></i> Chat / Consult
          </button>
          <button class="btn-primary btn-coral" style="flex:1.4;" onclick="startBookingWithProvider('${{p.id}}')">
            <i class="fa-solid fa-bolt"></i> Book Home Visit (₦${{p.rate.toLocaleString()}})
          </button>
        </div>
      `;

      navigateToScreen('screen-provider-detail');
    }}

    // Start Booking Flow
    function startBookingFlow(serviceId) {{
      const s = state.services.find(x => x.id === serviceId) || state.services[0];
      state.selectedService = s;

      document.getElementById('bookFlowServiceName').innerText = s.name;
      document.getElementById('bookFlowSpecialtyDuration').innerText = `${{s.specialty}} • Est. ${{s.duration}} minutes`;
      document.getElementById('bookFlowBasePrice').innerText = `₦${{s.rate.toLocaleString()}}`;
      document.getElementById('summaryBaseRate').innerText = `₦${{s.rate.toLocaleString()}}`;

      updateBookingCost();
      navigateToScreen('screen-booking-flow');
    }}

    function startBookingWithProvider(providerId) {{
      const p = state.providers.find(x => x.id === providerId);
      state.selectedProvider = p;

      // Select matching service or default
      const s = state.services.find(x => x.specialty === p.specialty) || state.services[0];
      state.selectedService = s;

      document.getElementById('bookFlowProvName').innerText = p.name;
      document.getElementById('bookFlowProvAvatar').innerText = p.initials;
      document.getElementById('bookFlowProvSub').innerText = `${{p.title}} • ${{p.distance}} km away`;

      startBookingFlow(s.id);
    }}

    function quickBookDefaultService() {{
      startBookingFlow('srv-01');
    }}

    function quickBookUrgentTriage() {{
      startBookingFlow('srv-07');
    }}

    function quickBookMaternalMidwife() {{
      startBookingFlow('srv-01');
    }}

    // Calculate Dynamic Booking Costs
    function updateBookingCost() {{
      const base = state.selectedService ? state.selectedService.rate : 15000;
      let supplies = 0;
      if (document.getElementById('supplyGauze')?.checked) supplies += 3500;
      if (document.getElementById('supplyIodine')?.checked) supplies += 2200;
      if (document.getElementById('supplyGloves')?.checked) supplies += 5500;

      const surcharge = 1000;
      const total = base + supplies + surcharge;

      if (document.getElementById('summaryBaseRate')) document.getElementById('summaryBaseRate').innerText = `₦${{base.toLocaleString()}}`;
      if (document.getElementById('summarySuppliesRate')) document.getElementById('summarySuppliesRate').innerText = `₦${{supplies.toLocaleString()}}`;
      if (document.getElementById('summaryTotalRate')) document.getElementById('summaryTotalRate').innerText = `₦${{total.toLocaleString()}}`;
    }}

    // Confirm & Execute Dispatch
    function confirmAndExecuteDispatch() {{
      const patient = document.getElementById('bookingPatientName').value || 'Chidi Anya';
      const address = document.getElementById('bookingDoorstepAddress').value || state.userAddress;

      state.activeBooking = {{
        id: 'LIFESAVERS-' + Math.floor(10000 + Math.random() * 90000),
        serviceName: state.selectedService ? state.selectedService.name : 'Wound Dressing & Sterile Care',
        providerName: state.selectedProvider ? state.selectedProvider.name : 'Nurse Amara Okonkwo',
        providerSpecialty: state.selectedProvider ? state.selectedProvider.title : 'Registered Nurse',
        status: 'EN_ROUTE',
        etaMinutes: 7,
        totalRate: 21700
      }};

      document.getElementById('trackerBookingId').innerText = state.activeBooking.id;
      showToast('Dispatch Confirmed! Health Worker matched and en-route.');
      navigateToScreen('screen-tracker');
    }}

    // Tracker Progression Simulation
    function simulateAdvanceDispatch() {{
      if (!state.activeBooking) return;
      const statuses = ['SEARCHING', 'MATCHED', 'EN_ROUTE', 'ARRIVED', 'IN_PROGRESS', 'COMPLETED'];
      const curIdx = statuses.indexOf(state.activeBooking.status);
      const nextStatus = statuses[Math.min(curIdx + 1, statuses.length - 1)];
      state.activeBooking.status = nextStatus;

      const labelEl = document.getElementById('trackerStatusLabel');
      const etaEl = document.getElementById('trackerEtaText');

      if (nextStatus === 'ARRIVED') {{
        labelEl.innerText = 'HEALTH WORKER ARRIVED AT DOORSTEP';
        etaEl.innerText = 'Awaiting patient door opening & identity check';
        document.getElementById('stepArrivedIcon').className = 'fa-solid fa-circle-check';
        document.getElementById('stepArrivedIcon').style.color = 'var(--color-success)';
        showBannerNotification('Nurse Amara has arrived at 14 Adeola Odeku St.');
      }} else if (nextStatus === 'IN_PROGRESS') {{
        labelEl.innerText = 'PROCEDURE IN PROGRESS';
        etaEl.innerText = 'Administering clinical treatment and dressing';
      }} else if (nextStatus === 'COMPLETED') {{
        labelEl.innerText = 'VISIT COMPLETED SUCCESSFULLY';
        etaEl.innerText = 'Signed off by Nurse Amara • Invoice cleared';
        document.getElementById('stepCompletedIcon').className = 'fa-solid fa-circle-check';
        document.getElementById('stepCompletedIcon').style.color = 'var(--color-success)';
        showBannerNotification('Visit completed. Thank you for using LifeSavers!');
      }}
    }}

    function cancelActiveDispatch() {{
      if (confirm('Cancel active dispatch request?')) {{
        showToast('Dispatch cancelled.');
        navigateToScreen('screen-home');
      }}
    }}

    // Chat System
    function renderChatMessages() {{
      const box = document.getElementById('chatMessagesBox');
      box.innerHTML = state.chatMessages.map(m => `
        <div class="chat-bubble ${{m.isFromPatient ? 'patient' : 'provider'}} ${{m.isEmergency ? 'emergency' : ''}}">
          <div style="font-size:10px; opacity:0.75; margin-bottom:2px;">${{m.sender}} • ${{m.time}}</div>
          <div>${{m.text}}</div>
        </div>
      `).join('');
      box.scrollTop = box.scrollHeight;
    }}

    function sendChatMessage() {{
      const input = document.getElementById('chatInputMessage');
      const text = input.value.trim();
      if (!text) return;

      const newMsg = {{
        id: 'msg-' + Date.now(),
        sender: state.currentRole === 'PATIENT' ? 'Chidi Anya' : 'Nurse Amara',
        text: text,
        time: new Date().toLocaleTimeString([], {{ hour: '2-digit', minute: '2-digit' }}),
        isFromPatient: state.currentRole === 'PATIENT',
        isEmergency: state.isEmergencyChat
      }};

      state.chatMessages.push(newMsg);
      input.value = '';
      state.isEmergencyChat = false;
      document.getElementById('emergencyMsgToggle').style.background = '';
      renderChatMessages();

      // Simulated auto-reply from health worker
      setTimeout(() => {{
        state.chatMessages.push({{
          id: 'reply-' + Date.now(),
          sender: state.currentRole === 'PATIENT' ? 'Nurse Amara' : 'Patient Chidi',
          text: 'Acknowledged. Sterile equipment ready. Arriving in ~5 mins.',
          time: new Date().toLocaleTimeString([], {{ hour: '2-digit', minute: '2-digit' }}),
          isFromPatient: state.currentRole !== 'PATIENT',
          isEmergency: false
        }});
        renderChatMessages();
      }}, 1500);
    }}

    function sendQuickChatMessage(txt) {{
      document.getElementById('chatInputMessage').value = txt;
      sendChatMessage();
    }}

    function toggleEmergencyMessageMode() {{
      state.isEmergencyChat = !state.isEmergencyChat;
      const btn = document.getElementById('emergencyMsgToggle');
      if (state.isEmergencyChat) {{
        btn.style.background = 'rgba(220, 38, 38, 0.2)';
        showToast('Emergency alert mode active (High Priority Tag)');
      }} else {{
        btn.style.background = '';
      }}
    }}

    // Audio & Video Call Handlers
    function triggerAudioCall(name) {{
      document.getElementById('audioCallDoctorName').innerText = name;
      document.getElementById('modalAudioCall').classList.add('open');
    }}
    function endAudioCall() {{
      document.getElementById('modalAudioCall').classList.remove('open');
      showToast('Call ended. Duration: 00:52');
    }}
    function triggerVideoCall(name) {{
      document.getElementById('modalVideoCall').classList.add('open');
    }}
    function endVideoCall() {{
      document.getElementById('modalVideoCall').classList.remove('open');
      showToast('Video consultation ended.');
    }}

    // FemTech Suite Tabs
    function switchFemtechTab(idx) {{
      for (let i = 0; i < 4; i++) {{
        document.getElementById('femtechContent' + i).style.display = i === idx ? 'block' : 'none';
        document.getElementById('tabFemtech' + (i + 1)).classList.toggle('active', i === idx);
      }}
    }}

    function recalculateCycleMath() {{
      const last = new Date(document.getElementById('cycleLastPeriodInput').value || '2026-03-15');
      const cycleLen = parseInt(document.getElementById('cycleLengthInput').value || '28');

      const next = new Date(last);
      next.setDate(next.getDate() + cycleLen);

      const ovul = new Date(last);
      ovul.setDate(ovul.getDate() + (cycleLen - 14));

      const fertStart = new Date(last);
      fertStart.setDate(fertStart.getDate() + (cycleLen - 18));
      const fertEnd = new Date(last);
      fertEnd.setDate(fertEnd.getDate() + (cycleLen - 11));

      const fmt = d => d.toISOString().split('T')[0];

      document.getElementById('calcNextPeriod').innerText = fmt(next);
      document.getElementById('calcOvulation').innerText = fmt(ovul);
      document.getElementById('calcFertileWindow').innerText = `${{fmt(fertStart)}} to ${{fmt(fertEnd)}}`;
      showToast('Cycle calculations updated in real-time');
    }}

    function toggleSymptomChip(el) {{
      el.classList.toggle('active');
    }}

    function saveCycleSymptoms() {{
      showToast('Cycle symptoms saved and encrypted to local storage.');
    }}

    function handleHbChange(val) {{
      document.getElementById('hbValueLabel').innerText = val + ' g/dL';
      const num = parseFloat(val);
      const box = document.getElementById('hbRiskBox');
      if (num >= 11.5) {{
        box.className = 'badge-pill badge-success';
        box.innerText = 'Risk Assessment: NORMAL / LOW RISK';
      }} else if (num >= 9.0) {{
        box.className = 'badge-pill badge-amber';
        box.innerText = 'Risk Assessment: MILD TO MODERATE ANAEMIA';
      }} else {{
        box.className = 'badge-pill badge-coral';
        box.innerText = 'Risk Assessment: SEVERE ANAEMIA (Referral Advised)';
      }}
    }}

    function wipeLocalHealthData() {{
      if (confirm('Permanently wipe all cycle, symptom, and maternal data from this device?')) {{
        showToast('All on-device health data wiped successfully.');
      }}
    }}

    // Gender Switcher (Female Suite <-> Male Suite)
    function switchGender(gender) {{
      state.userGender = gender;
      const femaleBtn = document.getElementById('genderFemaleBtn');
      const maleBtn = document.getElementById('genderMaleBtn');

      if (gender === 'FEMALE') {{
        femaleBtn.classList.add('active');
        femaleBtn.style.background = 'var(--color-primary)';
        femaleBtn.style.color = 'white';
        maleBtn.classList.remove('active');
        maleBtn.style.background = 'transparent';
        maleBtn.style.color = 'white';

        document.getElementById('suiteBadgeLabel').innerText = 'UNICEF FEMTECH CALL';
        document.getElementById('suiteHeadline').innerText = 'Maternal & Reproductive Health Suite';
        document.getElementById('suiteDescription').innerText = 'Menstrual cycle calculator, 1,000 Days maternal milestones, point-of-care vitals triage, and encrypted privacy shield.';
        document.getElementById('femtechMainTitle').innerText = 'LifeSavers FemTech Suite';
        document.getElementById('femtechSubTitle').innerText = 'Maternal & Reproductive Continuum of Care';
      }} else {{
        maleBtn.classList.add('active');
        maleBtn.style.background = 'var(--color-primary)';
        maleBtn.style.color = 'white';
        femaleBtn.classList.remove('active');
        femaleBtn.style.background = 'transparent';
        femaleBtn.style.color = 'white';

        document.getElementById('suiteBadgeLabel').innerText = "MEN'S HEALTH SUITE";
        document.getElementById('suiteHeadline').innerText = 'Prostate, Fertility & SRH Suite';
        document.getElementById('suiteDescription').innerText = 'International Prostate Symptom Score (IPSS), male reproductive health, sperm vitality parameters, and confidential andrologist care.';
        document.getElementById('femtechMainTitle').innerText = "LifeSavers Men's Health Suite";
        document.getElementById('femtechSubTitle').innerText = 'Prostate Health, Hormonal Vitality & Reproductive Care';
      }}
      showToast(`Personalized Health Suite set to ${{gender}}`);
    }}

    // Provider Mode Gig Alert Controls
    function declineGigAlert() {{
      document.getElementById('incomingGigAlertBox').style.display = 'none';
      showToast('Gig passed. Waiting for next nearby dispatch...');
    }}

    function acceptGigAlert() {{
      document.getElementById('incomingGigAlertBox').style.display = 'none';
      showToast('Gig accepted! Doorstep navigation launched.');
      navigateToScreen('screen-tracker');
    }}

    function toggleProviderOnlineStatus(isOnline) {{
      const lbl = document.getElementById('providerAvailabilityLabel');
      if (isOnline) {{
        lbl.innerText = 'ONLINE • DISPATCH READY';
        lbl.style.color = 'white';
        showToast('You are now Online and visible on Patient Gig Radar.');
      }} else {{
        lbl.innerText = 'OFFLINE • UNAVAILABLE';
        lbl.style.color = '#CBD5E1';
        showToast('You are Offline. Not receiving dispatch requests.');
      }}
    }}

    function requestPayout() {{
      alert('Payout of ₦52,000 initiated to registered Guaranty Trust Bank account (0123***891). Funds arrive within 5 minutes.');
    }}

    // Credentials Tabs
    function switchCredTab(idx) {{
      document.getElementById('credContent0').style.display = idx === 0 ? 'block' : 'none';
      document.getElementById('credContent1').style.display = idx === 1 ? 'block' : 'none';
      document.getElementById('tabCred0').classList.toggle('active', idx === 0);
      document.getElementById('tabCred1').classList.toggle('active', idx === 1);
      if (idx === 1) renderAdminCredReviewList();
    }}

    function simulateSha256Hash(input) {{
      if (input.files && input.files[0]) {{
        const fakeSha = 'sha256-' + Array.from(crypto.getRandomValues(new Uint8Array(20))).map(b => b.toString(16).padStart(2,'0')).join('');
        document.getElementById('credDocSha256').innerText = 'Computed Document SHA-256: ' + fakeSha;
      }}
    }}

    function submitCredentialDossier() {{
      showToast('Practicing License & ID submitted for Clinical Regulatory Review.');
    }}

    function renderAdminCredReviewList() {{
      const container = document.getElementById('adminCredReviewList');
      container.innerHTML = `
        <div style="padding:10px; background:var(--color-light-mint); border-radius:8px; margin-bottom:8px;">
          <div style="display:flex; justify-content:space-between; font-size:12px; font-weight:800;">
            <span>Nurse Amara Okonkwo (RN)</span>
            <span class="badge-pill badge-success">VERIFIED</span>
          </div>
          <div style="font-size:10px; color:#64748B; margin:2px 0 6px 0;">NMCN Registry Folio #RN/2019/84712 • Background: Passed</div>
          <div style="font-size:11px; color:var(--color-primary);">Reviewed by: ADM-LEAD-HEALTH • Status: Fully Cleared</div>
        </div>
        <div style="padding:10px; background:var(--color-light-mint); border-radius:8px;">
          <div style="display:flex; justify-content:space-between; font-size:12px; font-weight:800;">
            <span>Chioma Nwosu (B.Physio)</span>
            <span class="badge-pill badge-coral">PENDING AUDIT</span>
          </div>
          <div style="font-size:10px; color:#64748B; margin:2px 0 6px 0;">MRTB Registry Certificate #PT/2024/9182</div>
          <div style="display:flex; gap:6px; margin-top:6px;">
            <button class="btn-primary" style="padding:4px 10px; font-size:10px;" onclick="approveAuditLicense()">Approve License</button>
            <button class="btn-outlined" style="padding:4px 10px; font-size:10px; color:var(--color-coral); border-color:var(--color-coral);" onclick="flagAuditLicense()">Flag</button>
          </div>
        </div>
      `;
    }}

    function approveAuditLicense() {{
      showToast('License approved! Verification seal issued.');
      renderAdminCredReviewList();
    }}
    function flagAuditLicense() {{
      showToast('Dossier flagged for manual phone verification with registry.');
    }}

    // Cart Drawer
    function openCartDrawer() {{
      renderCartDrawer();
      document.getElementById('modalCart').classList.add('open');
    }}
    function closeCartDrawer() {{
      document.getElementById('modalCart').classList.remove('open');
    }}

    function renderCartDrawer() {{
      const container = document.getElementById('cartItemsList');
      container.innerHTML = state.cart.map((item, idx) => `
        <div style="display:flex; justify-content:space-between; align-items:center; padding:8px 0; border-bottom:1px solid #F1F5F9;">
          <div>
            <div style="font-size:13px; font-weight:700;">${{item.name}}</div>
            <div style="font-size:11px; color:#64748B;">₦${{item.price.toLocaleString()}} each</div>
          </div>
          <div style="display:flex; align-items:center; gap:8px;">
            <button class="btn-icon-pill" style="width:26px; height:26px;" onclick="changeCartQty(${{idx}}, -1)">-</button>
            <span style="font-size:13px; font-weight:800;">${{item.qty}}</span>
            <button class="btn-icon-pill" style="width:26px; height:26px;" onclick="changeCartQty(${{idx}}, 1)">+</button>
          </div>
        </div>
      `).join('');

      const subtotal = state.cart.reduce((sum, i) => sum + (i.price * i.qty), 0);
      document.getElementById('cartSubtotal').innerText = `₦${{subtotal.toLocaleString()}}`;
      document.getElementById('cartTotal').innerText = `₦${{(subtotal + 1000).toLocaleString()}}`;
      document.getElementById('cartBadge').innerText = state.cart.reduce((sum, i) => sum + i.qty, 0);
    }}

    function changeCartQty(idx, delta) {{
      state.cart[idx].qty += delta;
      if (state.cart[idx].qty <= 0) {{
        state.cart.splice(idx, 1);
      }}
      renderCartDrawer();
    }}

    function checkoutSuppliesOrder() {{
      closeCartDrawer();
      startBookingFlow('srv-01');
    }}

    // Address Dialog
    function openAddressDialog() {{
      document.getElementById('modalAddress').classList.add('open');
    }}
    function closeAddressDialog() {{
      document.getElementById('modalAddress').classList.remove('open');
    }}
    function selectAddressPreset(addr) {{
      document.getElementById('addressInputModal').value = addr;
    }}
    function useGpsCurrentLocation() {{
      document.getElementById('addressInputModal').value = 'GPS: Lat 6.4320, Lon 3.4265 (Adeola Odeku, VI Lagos)';
      showToast('Acquired high-accuracy GPS coordinates');
    }}
    function saveAddressDialog() {{
      state.userAddress = document.getElementById('addressInputModal').value;
      closeAddressDialog();
      showToast('Doorstep location updated.');
    }}

    // Supabase Sheet
    function openSupabaseSheet() {{
      document.getElementById('modalSupabase').classList.add('open');
    }}
    function closeSupabaseSheet() {{
      document.getElementById('modalSupabase').classList.remove('open');
    }}
    function triggerCloudBackup() {{
      showToast('Local database backed up to Supabase Cloud.');
      closeSupabaseSheet();
    }}
    function triggerCloudPull() {{
      showToast('Pulled latest cloud dispatches and real-time messages.');
      closeSupabaseSheet();
    }}

    // Profile Sheet
    function openProfileSheet() {{
      document.getElementById('modalProfile').classList.add('open');
    }}
    function closeProfileSheet() {{
      document.getElementById('modalProfile').classList.remove('open');
    }}
    function switchProfileTab(idx) {{
      for (let i = 0; i < 4; i++) {{
        document.getElementById('profContent' + i).style.display = i === idx ? 'block' : 'none';
        document.getElementById('tabProf' + i).classList.toggle('active', i === idx);
      }}
    }}
    function renderProfileHistory() {{
      document.getElementById('profileBookingsList').innerHTML = `
        <div class="card" style="margin-bottom:8px;">
          <div style="display:flex; justify-content:space-between; font-size:12px; font-weight:800;">
            <span>Wound Dressing & Sterile Care</span>
            <span class="badge-pill badge-coral">EN ROUTE</span>
          </div>
          <div style="font-size:11px; color:#64748B; margin:2px 0 6px 0;">Nurse Amara Okonkwo • ₦21,700</div>
          <button class="btn-primary" style="padding:4px 10px; font-size:10px;" onclick="closeProfileSheet(); navigateToScreen('screen-tracker');">Track Live</button>
        </div>
        <div class="card">
          <div style="display:flex; justify-content:space-between; font-size:12px; font-weight:800;">
            <span>Doctor Home Consultation</span>
            <span class="badge-pill badge-success">COMPLETED</span>
          </div>
          <div style="font-size:11px; color:#64748B; margin:2px 0 6px 0;">Dr. Tunde Balogun • ₦30,000 • Paid</div>
          <span style="font-size:10px; color:#64748B;">Invoice #INV-99210 • 3 days ago</span>
        </div>
      `;

      document.getElementById('profileReviewsList').innerHTML = `
        <div class="card">
          <div style="display:flex; justify-content:space-between; font-size:12px; font-weight:800; margin-bottom:4px;">
            <span>Nurse Amara Okonkwo</span>
            <span style="color:var(--color-amber);">5.0 ★</span>
          </div>
          <div style="font-size:11px; color:#475569; line-height:1.4;">"Arrived within 8 minutes. Very gentle and hospital-grade sterile dressing on my incision."</div>
        </div>
      `;

      document.getElementById('profileFavoritesList').innerHTML = `
        <div class="card" style="display:flex; justify-content:space-between; align-items:center;">
          <div>
            <div style="font-size:13px; font-weight:800;">Nurse Amara Okonkwo</div>
            <div style="font-size:11px; color:#64748B;">Critical & Post-Op Wound Care Specialist</div>
          </div>
          <button class="btn-primary" style="padding:6px 10px; font-size:11px;" onclick="closeProfileSheet(); openProviderDetail('prov-01');">Re-Book</button>
        </div>
      `;
    }}

    function savePersonalProfile() {{
      showToast('Personal and medical info updated.');
      closeProfileSheet();
    }}

    // Logo Reveal Screen
    function openLogoReveal() {{
      showToast('LifeSavers Healthcare: The Bolt for Healthcare');
    }}

    // Heads-up In-App Notification Banner
    function showBannerNotification(text) {{
      const banner = document.getElementById('headsUpBanner');
      document.getElementById('bannerText').innerText = text;
      banner.classList.add('show');
      setTimeout(() => banner.classList.remove('show'), 6000);
    }}

    function handleBannerClick() {{
      document.getElementById('headsUpBanner').classList.remove('show');
      navigateToScreen('screen-tracker');
    }}

    // Toast Message
    function showToast(msg) {{
      const banner = document.getElementById('headsUpBanner');
      document.getElementById('bannerTitle').innerText = 'LIFESAVERS ALERT';
      document.getElementById('bannerText').innerText = msg;
      banner.classList.add('show');
      setTimeout(() => banner.classList.remove('show'), 3500);
    }}
  </script>
</body>
</html>
"""

print(f"Writing complete web/index.html ({len(html_content)} chars)...")
with open("web/index.html", "w") as f:
    f.write(html_content)

print("web/index.html generated successfully!")
