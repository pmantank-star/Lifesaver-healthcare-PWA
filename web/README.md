# LifeSavers Healthcare — Web App (PWA) Deployment Guide

This folder contains the complete, standalone Web Application for **LifeSavers Healthcare**.

---

## 📁 What Files Do You Need to Upload?

When deploying to a live domain or hosting server, you only need the **production web files**:

| File / Folder | Needed on Live Server? | Purpose |
| :--- | :---: | :--- |
| **`index.html`** | **YES (Required)** | The entire application (HTML, CSS, JS, and all embedded images). |
| **`manifest.json`** | **YES (Recommended)** | Enables Progressive Web App (PWA) support ("Add to Home Screen"). |
| **`sw.js`** | **YES (Recommended)** | Service worker for offline caching and fast asset delivery. |
| **`assets/`** | **YES** | Contains `lifesavers_logo.png` (app icon for home screens). |
| `build_full_web.py` | *NO (Optional)* | Generator script used to compile the standalone HTML. Safe to omit. |
| `README.md` | *NO (Optional)* | Documentation. Safe to omit. |

> **Note**: Even if you upload **only `index.html`**, the entire application works 100% because all images, icons, and stylesheets are self-contained. Adding `manifest.json`, `sw.js`, and `assets/` makes it installable as a mobile app directly from the browser.

---

## 🚀 3 Ways to Put It on a Live Domain

### Method 1: Netlify Drop / Vercel (Fastest — 30 Seconds, Free)
1. Go to [app.netlify.com/drop](https://app.netlify.com/drop) or [vercel.com/new](https://vercel.com/new).
2. Drag and drop the `web` folder into the browser window.
3. Your site is instantly live with free HTTPS (e.g., `lifesavers-healthcare.netlify.app`).
4. To connect your custom domain (e.g., `app.lifesavershealthcare.org`):
   - Go to **Domain Management** in Netlify/Vercel and add your domain.

---

### Method 2: cPanel / Traditional Web Hosting
1. Log into your cPanel or FTP account.
2. Open **File Manager** and navigate to your `public_html` directory (or subdomain folder).
3. Upload:
   - `index.html`
   - `manifest.json`
   - `sw.js`
   - `assets/` folder
4. Visit your domain (e.g., `https://yourdomain.com`) — the app will load immediately.

---

### Method 3: GitHub Pages (Directly from Repo)
1. In your GitHub repository (`pmantank-star/LifeSaversHealthCare`):
2. Go to **Settings** > **Pages**.
3. Under **Build and deployment** > **Branch**:
   - Select your main branch.
   - Set the folder dropdown to `/web`.
4. Click **Save**. GitHub will publish your live website at `https://<username>.github.io/<repo-name>/`.
