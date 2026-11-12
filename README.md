# Eman Abdul Malik — Portfolio

A responsive portfolio built with plain HTML, CSS and JavaScript. No framework, package installation or build step is required.

## Preview

Open `index.html` directly, or run the included local preview server:

```sh
node server.mjs
```

Visit http://127.0.0.1:4173. You can choose a different port with `node server.mjs 8080`. Stop the server with Ctrl+C.

## Files

- `index.html` — all portfolio content, project cards, navigation and contact links.
- `styles.css` — colors, typography, desktop/mobile layouts and reduced-motion styles.
- `projects.css` — the visual project gallery and responsive project-detail layouts.
- `projects/*.html` — six separate project pages with confirmed details, live previews where available, and placeholders for upcoming media.
- `assets/eduoak-homepage.png` — a real screenshot of the live EduOak Consulting website.
- `script.js` — mobile navigation, active-section indication, portrait parallax, scroll-driven skill cards, greeting and optional email copy.
- `assets/eman-avatar.png` — generated avatar based on the supplied photo and approved mockup.
- `assets/favicon.svg` — the small E monogram used in the browser tab.
- `server.mjs` — optional dependency-free local preview server; not required for hosting.

## Updating the content

Edit the relevant section in `index.html`. Each project is an `article.work-card` linking to its page in `projects/`, with an accessible title, description and technology list. All text is present in HTML and remains readable without JavaScript. Replace an illustrative `.work-cover` with a real screenshot when available and provide meaningful alt text. Update both the gallery and the corresponding project page when changing details. In each project page, replace `.demo-placeholder` with the supplied screenshots, walkthrough video, or live demo. These placeholders are not playable demos. Mobile projects intentionally retain placeholder names. Add demo/source links only when real links are available. Update resume links in both the navigation and contact section when the document changes.

The main palette and font stacks are defined at the top of `styles.css`. Google Fonts supplies DM Sans, with system fallbacks if offline. The portfolio has no analytics or contact-form backend. EduOak's embedded website loads its own external content and services.

The avatar is a transparent illustrated image with lightweight pointer-responsive parallax, not a rigged 3D model. Motion is disabled for reduced-motion preferences and touch devices. Clicking or keyboard-activating the avatar shows a short greeting.

The Skills section pins both columns while scrolling swipes each card upward into the same position, with a slight rotation and alternating pink surfaces. Each card pauses briefly before the next enters. Scrolling backward reverses the movement, with no wheel or touch interception. Small screens, windows without enough reading space, and reduced-motion preferences use a normal list. All skill content remains available without JavaScript. Its section colors are controlled by `--skills-accent` and `--skills-fill` in `styles.css`.

## Content accuracy

SecureScan's description and Java/XML stack were checked against its public GitHub README and file list: https://github.com/eman-110/SecureScan. The description does not guarantee that suspicious-link detection catches every threat. EduOak's WordPress and Canva tools were supplied by the owner; its education-consulting content was checked at https://eduoak.org/. Its page includes a real homepage screenshot, a live iframe, and links to open the original website. The embedded site's content and availability are controlled by EduOak. Other project covers are labeled illustrative artwork. No employers, dates, metrics or additional project technologies were inferred.

## Static hosting

Upload `index.html`, `styles.css`, `projects.css`, `script.js`, and the `assets` and `projects` directories to any static host (for example GitHub Pages). Keep their relative paths intact. No server build is needed. The website is currently prepared for local use; no deployment is configured.
