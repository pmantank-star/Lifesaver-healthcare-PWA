import json
import os

with open('web/assets/assets_b64.json') as f:
    assets = json.load(f)

html = f"""<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
  <title>Oma — Animated Logo Reveal</title>
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700;800&family=Quicksand:wght@500;600;700&display=swap" rel="stylesheet">
  <style>
    :root {{
      --color-teal: #0a4d3c;
      --color-green: #38a649;
      --color-bg: #f8fafc;
      --color-card: #ffffff;
      --color-border: #e2e8f0;
      --color-text: #0f172a;
      --color-muted: #64748b;
    }}

    * {{
      box-sizing: border-box;
      margin: 0;
      padding: 0;
      -webkit-tap-highlight-color: transparent;
    }}

    body {{
      font-family: 'Plus Jakarta Sans', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
      background-color: var(--color-bg);
      color: var(--color-text);
      min-height: 100vh;
      display: flex;
      flex-direction: column;
      align-items: center;
      padding: 16px;
    }}

    header {{
      width: 100%;
      max-width: 1040px;
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin-bottom: 16px;
      padding: 10px 4px;
    }}

    .brand-title {{
      display: flex;
      align-items: center;
      gap: 10px;
    }}

    .brand-badge {{
      display: inline-block;
      width: 10px;
      height: 10px;
      border-radius: 50%;
      background: var(--color-green);
      box-shadow: 0 0 0 3px rgba(56, 166, 73, 0.2);
    }}

    h1 {{
      font-size: 1.15rem;
      font-weight: 700;
      color: var(--color-teal);
      letter-spacing: -0.01em;
    }}

    .subtitle {{
      font-size: 0.8rem;
      color: var(--color-muted);
      font-weight: 500;
    }}

    /* 16:9 Canvas Frame */
    .viewport-container {{
      width: 100%;
      max-width: 1040px;
      position: relative;
      background: #ffffff;
      border-radius: 18px;
      box-shadow: 0 10px 30px -5px rgba(10, 77, 60, 0.08), 0 4px 12px -2px rgba(0, 0, 0, 0.04);
      border: 1px solid rgba(226, 232, 240, 0.8);
      overflow: hidden;
    }}

    .aspect-16-9 {{
      width: 100%;
      padding-top: 56.25%; /* 16:9 Ratio */
      position: relative;
      background: #ffffff;
    }}

    .canvas-stage {{
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      display: flex;
      align-items: center;
      justify-content: center;
      background: #ffffff;
      user-select: none;
    }}

    /* SVG Logo Render */
    .logo-svg {{
      width: 74%;
      max-height: 74%;
      overflow: visible;
    }}

    /* Component layer styles */
    .comp-layer {{
      transform-box: fill-box;
      will-change: transform, opacity;
    }}

    /* Control Deck */
    .controls-deck {{
      width: 100%;
      max-width: 1040px;
      background: var(--color-card);
      border-radius: 16px;
      border: 1px solid var(--color-border);
      padding: 16px 20px;
      margin-top: 16px;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.03);
    }}

    /* Scrubber Timeline */
    .timeline-row {{
      display: flex;
      align-items: center;
      gap: 14px;
      margin-bottom: 14px;
    }}

    .time-display {{
      font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace;
      font-size: 0.85rem;
      font-weight: 600;
      color: var(--color-teal);
      min-width: 95px;
      text-align: right;
    }}

    .scrubber-track-container {{
      flex: 1;
      position: relative;
      display: flex;
      flex-direction: column;
    }}

    .timeline-phases {{
      display: flex;
      justify-content: space-between;
      font-size: 0.68rem;
      color: var(--color-muted);
      margin-bottom: 4px;
      padding: 0 2px;
      font-weight: 600;
      letter-spacing: 0.02em;
    }}

    input[type=range] {{
      -webkit-appearance: none;
      appearance: none;
      width: 100%;
      height: 8px;
      border-radius: 4px;
      background: #e2e8f0;
      outline: none;
      cursor: pointer;
    }}

    input[type=range]::-webkit-slider-thumb {{
      -webkit-appearance: none;
      appearance: none;
      width: 20px;
      height: 20px;
      border-radius: 50%;
      background: var(--color-teal);
      border: 3px solid #ffffff;
      box-shadow: 0 2px 6px rgba(0,0,0,0.25);
      cursor: grab;
      transition: transform 0.1s ease;
    }}

    input[type=range]:active::-webkit-slider-thumb {{
      cursor: grabbing;
      transform: scale(1.15);
      background: var(--color-green);
    }}

    /* Buttons Row */
    .actions-row {{
      display: flex;
      flex-wrap: wrap;
      align-items: center;
      justify-content: space-between;
      gap: 12px;
    }}

    .left-actions, .right-actions {{
      display: flex;
      align-items: center;
      gap: 8px;
    }}

    .btn {{
      display: inline-flex;
      align-items: center;
      justify-content: center;
      gap: 6px;
      height: 40px;
      padding: 0 16px;
      border-radius: 10px;
      font-size: 0.85rem;
      font-weight: 600;
      cursor: pointer;
      border: 1px solid var(--color-border);
      background: #ffffff;
      color: var(--color-text);
      transition: all 0.15s ease;
    }}

    .btn:hover {{
      background: #f1f5f9;
      border-color: #cbd5e1;
    }}

    .btn-primary {{
      background: var(--color-teal);
      border-color: var(--color-teal);
      color: #ffffff;
    }}

    .btn-primary:hover {{
      background: #083e30;
      border-color: #083e30;
    }}

    .segmented-control {{
      display: inline-flex;
      background: #f1f5f9;
      padding: 3px;
      border-radius: 10px;
      border: 1px solid var(--color-border);
    }}

    .segmented-btn {{
      padding: 4px 10px;
      font-size: 0.75rem;
      font-weight: 600;
      border: none;
      background: transparent;
      color: var(--color-muted);
      border-radius: 7px;
      cursor: pointer;
      transition: all 0.15s ease;
    }}

    .segmented-btn.active {{
      background: #ffffff;
      color: var(--color-teal);
      box-shadow: 0 1px 3px rgba(0,0,0,0.1);
    }}

    .toggle-label {{
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 0.82rem;
      font-weight: 600;
      color: var(--color-muted);
      cursor: pointer;
      user-select: none;
    }}

    .toggle-checkbox {{
      width: 16px;
      height: 16px;
      accent-color: var(--color-teal);
      cursor: pointer;
    }}

    /* Tech Info Accordion / Explainer */
    .info-card {{
      width: 100%;
      max-width: 1040px;
      background: var(--color-card);
      border-radius: 16px;
      border: 1px solid var(--color-border);
      padding: 18px 22px;
      margin-top: 16px;
      font-size: 0.85rem;
      color: var(--color-muted);
      line-height: 1.55;
    }}

    .info-header {{
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin-bottom: 10px;
    }}

    .info-title {{
      font-weight: 700;
      color: var(--color-teal);
      font-size: 0.92rem;
    }}

    .badge-tag {{
      background: rgba(10, 77, 60, 0.08);
      color: var(--color-teal);
      padding: 2px 8px;
      border-radius: 6px;
      font-size: 0.72rem;
      font-weight: 700;
    }}

    .asset-grid {{
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(130px, 1fr));
      gap: 10px;
      margin-top: 12px;
    }}

    .asset-pill {{
      background: #f8fafc;
      border: 1px solid var(--color-border);
      border-radius: 10px;
      padding: 8px;
      display: flex;
      flex-direction: column;
      align-items: center;
      text-align: center;
      gap: 4px;
    }}

    .asset-pill img {{
      max-height: 38px;
      object-fit: contain;
    }}

    .asset-pill span {{
      font-size: 0.7rem;
      font-weight: 600;
      color: var(--color-text);
    }}

    @media (max-width: 640px) {{
      .actions-row {{
        flex-direction: column;
        align-items: stretch;
      }}
      .left-actions, .right-actions {{
        justify-content: space-between;
      }}
    }}
  </style>
</head>
<body>

  <header>
    <div class="brand-title">
      <span class="brand-badge"></span>
      <div>
        <h1>Oma — Animated Logo Reveal</h1>
        <div class="subtitle">Healthcare at your doorstep &bull; Brand Motion Preview</div>
      </div>
    </div>
    <div>
      <span class="badge-tag">16:9 Canvas</span>
    </div>
  </header>

  <!-- 16:9 Preview Frame -->
  <main class="viewport-container">
    <div class="aspect-16-9">
      <div class="canvas-stage" id="stage">
        <svg class="logo-svg" viewBox="0 0 992 484" fill="none" xmlns="http://www.w3.org/2000/svg">
          <defs>
            <clipPath id="tagline-clip">
              <rect id="tagline-clip-rect" x="0" y="390" width="992" height="94" />
            </clipPath>
          </defs>

          <!-- 1. Emblem Group -->
          <g id="group-emblem" class="comp-layer">
            <!-- Teal Circle & House Emblem -->
            <g id="layer-ring" class="comp-layer" style="transform-origin: 193px 192px;">
              <image href="{assets['emblem_ring']}" x="0" y="0" width="992" height="484" />
            </g>

            <!-- Green Lower Supportive Swoosh (Hand) -->
            <g id="layer-swoosh" class="comp-layer" style="transform-origin: 157px 314px;">
              <image href="{assets['swoosh']}" x="0" y="0" width="992" height="484" />
            </g>

            <!-- Green Medical Cross -->
            <g id="layer-cross" class="comp-layer" style="transform-origin: 190px 189px;">
              <image href="{assets['cross']}" x="0" y="0" width="992" height="484" />
            </g>
          </g>

          <!-- 2. Wordmark Letter 'm' -->
          <g id="layer-m" class="comp-layer" style="transform-origin: 573px 231px;">
            <image href="{assets['letter_m']}" x="0" y="0" width="992" height="484" />
          </g>

          <!-- 3. Wordmark Letter 'a' -->
          <g id="layer-a" class="comp-layer" style="transform-origin: 872px 230px;">
            <image href="{assets['letter_a']}" x="0" y="0" width="992" height="484" />
          </g>

          <!-- 4. Tagline: 'Healthcare at your doorstep.' -->
          <g id="layer-tagline" class="comp-layer" clip-path="url(#tagline-clip)" style="transform-origin: 500px 447px;">
            <image href="{assets['tagline']}" x="0" y="0" width="992" height="484" />
          </g>

          <!-- Reference Ground-Truth Master Overlay (Toggleable) -->
          <g id="layer-reference" style="opacity: 0; pointer-events: none;">
            <image href="{assets['logo_complete']}" x="0" y="0" width="992" height="484" />
          </g>
        </svg>
      </div>
    </div>
  </main>

  <!-- Interactive Controls Deck -->
  <section class="controls-deck">
    <div class="timeline-row">
      <div class="scrubber-track-container">
        <div class="timeline-phases">
          <span>0.0s Pure White</span>
          <span>1.2s Emblem Assembly</span>
          <span>2.3s Letters Form</span>
          <span>3.4s Tagline</span>
          <span>5.0s Hold</span>
        </div>
        <input type="range" id="timeline-scrubber" min="0" max="5000" value="0" step="10" aria-label="Animation Timeline Scrubber">
      </div>
      <div class="time-display" id="time-display">00.00s / 05.00s</div>
    </div>

    <div class="actions-row">
      <div class="left-actions">
        <button class="btn btn-primary" id="btn-play-pause" title="Play / Pause (Spacebar)">
          <svg id="play-icon" width="16" height="16" viewBox="0 0 24 24" fill="currentColor"><path d="M8 5v14l11-7z"/></svg>
          <span id="play-label">Play</span>
        </button>

        <button class="btn" id="btn-replay" title="Replay from beginning (R)">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><path d="M3 12a9 9 0 1 0 9-9 9.75 9.75 0 0 0-6.74 2.74L3 8"/><path d="M3 3v5h5"/></svg>
          <span>Replay</span>
        </button>

        <div class="segmented-control">
          <button class="segmented-btn" data-speed="0.5">0.5x</button>
          <button class="segmented-btn active" data-speed="1.0">1.0x</button>
          <button class="segmented-btn" data-speed="1.5">1.5x</button>
        </div>
      </div>

      <div class="right-actions">
        <label class="toggle-label">
          <input type="checkbox" id="loop-toggle" class="toggle-checkbox">
          <span>Loop Reveal</span>
        </label>

        <label class="toggle-label" style="margin-left: 10px;">
          <input type="checkbox" id="compare-toggle" class="toggle-checkbox">
          <span>Verify Alignment</span>
        </label>
      </div>
    </div>
  </section>

  <!-- Technical Vector & Asset Architecture Card -->
  <section class="info-card">
    <div class="info-header">
      <div class="info-title">📐 Motion Architecture & Vector Asset Specifications</div>
      <span class="badge-tag">Strict Brand Preservation</span>
    </div>
    <p>
      This animation deconstructs the authentic Oma brand mark into its discrete visual components, recreating the dynamic entrance inspired by the Dribbble reference:
      <strong>1)</strong> The central <em>Green Medical Cross</em> drops in with smooth spring deceleration;
      <strong>2)</strong> The <em>Green Lower Swoosh (Hand)</em> arcs smoothly from the bottom left;
      <strong>3)</strong> The <em>Deep Teal Circular Emblem</em> rotates and locks with the house apex;
      <strong>4)</strong> The wordmark glyphs <em>'m'</em> and <em>'a'</em> glide into exact kerning position;
      <strong>5)</strong> The tagline <em>"Healthcare at your doorstep."</em> slides in with soft opacity easing and clip reveal.
    </p>

    <div class="asset-grid">
      <div class="asset-pill">
        <img src="{assets['cross']}" alt="Cross">
        <span>1. Green Cross</span>
      </div>
      <div class="asset-pill">
        <img src="{assets['swoosh']}" alt="Swoosh">
        <span>2. Green Swoosh</span>
      </div>
      <div class="asset-pill">
        <img src="{assets['emblem_ring']}" alt="Emblem Ring">
        <span>3. Teal Ring</span>
      </div>
      <div class="asset-pill">
        <img src="{assets['letter_m']}" alt="Letter M">
        <span>4. Letter 'm'</span>
      </div>
      <div class="asset-pill">
        <img src="{assets['letter_a']}" alt="Letter A">
        <span>5. Letter 'a'</span>
      </div>
      <div class="asset-pill">
        <img src="{assets['tagline']}" alt="Tagline">
        <span>6. Tagline</span>
      </div>
    </div>
  </section>

  <script>
    // Animation Engine Constants
    const TOTAL_DURATION_MS = 5000;
    let playbackSpeed = 1.0;
    let isPlaying = false;
    let isLooping = false;
    let currentTimeMs = 0;
    let lastTimestamp = null;
    let animationFrameId = null;

    // DOM Elements
    const scrubber = document.getElementById('timeline-scrubber');
    const timeDisplay = document.getElementById('time-display');
    const btnPlayPause = document.getElementById('btn-play-pause');
    const playIcon = document.getElementById('play-icon');
    const playLabel = document.getElementById('play-label');
    const btnReplay = document.getElementById('btn-replay');
    const loopToggle = document.getElementById('loop-toggle');
    const compareToggle = document.getElementById('compare-toggle');
    const speedButtons = document.querySelectorAll('.segmented-btn');

    // Layer Elements
    const layerCross = document.getElementById('layer-cross');
    const layerSwoosh = document.getElementById('layer-swoosh');
    const layerRing = document.getElementById('layer-ring');
    const groupEmblem = document.getElementById('group-emblem');
    const layerM = document.getElementById('layer-m');
    const layerA = document.getElementById('layer-a');
    const layerTagline = document.getElementById('layer-tagline');
    const taglineClip = document.getElementById('tagline-clip-rect');
    const layerReference = document.getElementById('layer-reference');

    // Easing helper functions
    // Cubic Out (fast start, smooth deceleration, no unwanted bounce)
    function easeOutCubic(t) {{
      return 1 - Math.pow(1 - t, 3);
    }}

    // Quart Out (silk-smooth landing)
    function easeOutQuart(t) {{
      return 1 - Math.pow(1 - t, 4);
    }}

    // Spring Bounce Out for letter 'm' (smooth entry with organic spring-like settling upon reaching final position)
    function easeOutSpringBounce(t) {{
      if (t <= 0) return 0;
      if (t >= 1) return 1;
      const base = 1 - Math.pow(1 - t, 3.5);
      if (t < 0.6) return base;
      const p = (t - 0.6) / 0.4;
      const bounce = Math.sin(p * Math.PI * 2.5) * Math.exp(-3.2 * p) * 0.09;
      return 1.0 + bounce;
    }}

    // Clamp value between 0 and 1
    function clamp01(v) {{
      return Math.max(0, Math.min(1, v));
    }}

    // Interpolate progress within a specific time window [startMs, endMs]
    function subProgress(tMs, startMs, endMs) {{
      if (tMs <= startMs) return 0;
      if (tMs >= endMs) return 1;
      return (tMs - startMs) / (endMs - startMs);
    }}

    // Core Frame Renderer based on explicit timestamp (ms)
    function renderFrame(t) {{
      // 1. Initial State (0.0s - 0.3s): pure white background
      // -------------------------------------------------------------

      // 2. Medical Cross Entry (0.3s - 1.4s)
      // Drops in from above (-80px), scales from 0.15 to 1.0, subtle spin (-25deg to 0deg)
      const pCross = subProgress(t, 300, 1400);
      const eCross = easeOutCubic(pCross);
      const crossY = (1 - eCross) * -80;
      const crossScale = 0.15 + 0.85 * eCross;
      const crossRot = (1 - eCross) * -25;
      const crossOpacity = clamp01(pCross * 3.5);
      layerCross.style.transform = `translateY(${{crossY}}px) scale(${{crossScale}}) rotate(${{crossRot}}deg)`;
      layerCross.style.opacity = crossOpacity;

      // 3. Green Lower Swoosh Entry (0.7s - 1.8s)
      // Glides in from bottom-left with gentle rotation (-18deg to 0deg)
      const pSwoosh = subProgress(t, 700, 1800);
      const eSwoosh = easeOutQuart(pSwoosh);
      const swooshX = (1 - eSwoosh) * -65;
      const swooshY = (1 - eSwoosh) * 60;
      const swooshRot = (1 - eSwoosh) * -18;
      const swooshScale = 0.3 + 0.7 * eSwoosh;
      const swooshOpacity = clamp01(pSwoosh * 3.0);
      layerSwoosh.style.transform = `translate(${{swooshX}}px, ${{swooshY}}px) rotate(${{swooshRot}}deg) scale(${{swooshScale}})`;
      layerSwoosh.style.opacity = swooshOpacity;

      // 4. Teal Circular Ring & House Emblem Entry (1.2s - 2.3s)
      // Sweeps in with rotation (35deg to 0deg), scale from 0.55 to 1.0
      const pRing = subProgress(t, 1200, 2300);
      const eRing = easeOutCubic(pRing);
      const ringScale = 0.55 + 0.45 * eRing;
      const ringRot = (1 - eRing) * 35;
      const ringOpacity = clamp01(pRing * 2.8);
      layerRing.style.transform = `scale(${{ringScale}}) rotate(${{ringRot}}deg)`;
      layerRing.style.opacity = ringOpacity;

      // Emblem assembly micro-pulse at 2.2s - 2.5s
      const pAssemblePulse = subProgress(t, 2200, 2550);
      let emblemScale = 1.0;
      if (pAssemblePulse > 0 && pAssemblePulse < 1) {{
        emblemScale = 1.0 + Math.sin(pAssemblePulse * Math.PI) * 0.025;
      }}
      groupEmblem.style.transform = `scale(${{emblemScale}})`;

      // 5. Letter 'm' Entry (2.0s - 3.1s)
      // Enters from right (+75px), fades in, and executes an organic spring-like bounce upon reaching its final position
      const pM = subProgress(t, 2000, 3100);
      const eM = easeOutSpringBounce(pM);
      const mX = (1 - eM) * 75;
      const mScale = eM > 1 ? 1.0 + (eM - 1.0) * 0.5 : 1.0;
      const mOpacity = clamp01(pM * 2.8);
      layerM.style.transform = `translateX(${{mX}}px) scale(${{mScale}})`;
      layerM.style.opacity = mOpacity;

      // 6. Letter 'a' Entry (2.3s - 3.35s)
      // Follows letter 'm' from right (+85px), docks alongside 'm'
      const pA = subProgress(t, 2300, 3350);
      const eA = easeOutCubic(pA);
      const aX = (1 - eA) * 85;
      const aOpacity = clamp01(pA * 2.8);
      layerA.style.transform = `translateX(${{aX}}px)`;
      layerA.style.opacity = aOpacity;

      // 7. Tagline 'Healthcare at your doorstep.' (3.2s - 4.2s)
      // Subtle horizontal movement from left (-32px) and clean clip-rect wipe
      const pTagline = subProgress(t, 3200, 4200);
      const eTagline = easeOutQuart(pTagline);
      const taglineX = (1 - eTagline) * -32;
      const taglineOpacity = clamp01(pTagline * 2.5);
      layerTagline.style.transform = `translateX(${{taglineX}}px)`;
      layerTagline.style.opacity = taglineOpacity;

      if (taglineClip) {{
        const clipW = 992 * eTagline;
        taglineClip.setAttribute('width', clipW.toString());
      }}

      // Update Scrubber and time text
      scrubber.value = t;
      const sec = (t / 1000).toFixed(2);
      timeDisplay.textContent = `${{sec.padStart(5, '0')}}s / 05.00s`;
    }}

    // Playback Loop
    function tick(timestamp) {{
      if (!lastTimestamp) lastTimestamp = timestamp;
      const delta = (timestamp - lastTimestamp) * playbackSpeed;
      lastTimestamp = timestamp;

      currentTimeMs += delta;

      if (currentTimeMs >= TOTAL_DURATION_MS) {{
        if (isLooping) {{
          currentTimeMs = 0;
          renderFrame(currentTimeMs);
          animationFrameId = requestAnimationFrame(tick);
        }} else {{
          currentTimeMs = TOTAL_DURATION_MS;
          renderFrame(currentTimeMs);
          pauseAnimation();
        }}
        return;
      }}

      renderFrame(currentTimeMs);
      if (isPlaying) {{
        animationFrameId = requestAnimationFrame(tick);
      }}
    }}

    function startAnimation() {{
      if (currentTimeMs >= TOTAL_DURATION_MS) {{
        currentTimeMs = 0;
      }}
      isPlaying = true;
      lastTimestamp = null;
      btnPlayPause.classList.add('btn-primary');
      playIcon.innerHTML = '<path d="M6 19h4V5H6v14zm8-14v14h4V5h-4z"/>';
      playLabel.textContent = 'Pause';
      animationFrameId = requestAnimationFrame(tick);
    }}

    function pauseAnimation() {{
      isPlaying = false;
      if (animationFrameId) {{
        cancelAnimationFrame(animationFrameId);
        animationFrameId = null;
      }}
      playIcon.innerHTML = '<path d="M8 5v14l11-7z"/>';
      playLabel.textContent = 'Play';
    }}

    function replayAnimation() {{
      pauseAnimation();
      currentTimeMs = 0;
      renderFrame(0);
      startAnimation();
    }}

    // Event Listeners
    btnPlayPause.addEventListener('click', () => {{
      if (isPlaying) pauseAnimation();
      else startAnimation();
    }});

    btnReplay.addEventListener('click', replayAnimation);

    scrubber.addEventListener('input', (e) => {{
      pauseAnimation();
      currentTimeMs = parseFloat(e.target.value);
      renderFrame(currentTimeMs);
    }});

    loopToggle.addEventListener('change', (e) => {{
      isLooping = e.target.checked;
    }});

    compareToggle.addEventListener('change', (e) => {{
      layerReference.style.opacity = e.target.checked ? '0.45' : '0';
    }});

    speedButtons.forEach(btn => {{
      btn.addEventListener('click', () => {{
        speedButtons.forEach(b => b.classList.remove('active'));
        btn.classList.add('active');
        playbackSpeed = parseFloat(btn.dataset.speed);
      }});
    }});

    // Keyboard Shortcuts (Space: Play/Pause, R: Replay)
    window.addEventListener('keydown', (e) => {{
      if (e.target.tagName === 'INPUT') return;
      if (e.code === 'Space') {{
        e.preventDefault();
        if (isPlaying) pauseAnimation();
        else startAnimation();
      }} else if (e.code === 'KeyR') {{
        e.preventDefault();
        replayAnimation();
      }}
    }});

    // Initialize with frame 0, then auto-play once after a brief pause
    renderFrame(0);
    setTimeout(() => {{
      startAnimation();
    }}, 450);
  </script>
</body>
</html>
"""

os.makedirs('web', exist_ok=True)
with open('web/index.html', 'w') as f:
    f.write(html)

print("Generated web/index.html successfully! File size:", len(html), "bytes")
