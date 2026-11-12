"use strict";

// Navigation collapses into a compact pill on scroll across every page.
const menuButton = document.querySelector(".menu-toggle");
const navLinks = document.querySelector(".nav-links");
const siteHeader = document.querySelector(".site-header");

function closeMenu(returnFocus = false) {
  if (!menuButton || !navLinks) return;
  menuButton.setAttribute("aria-expanded", "false");
  menuButton.setAttribute("aria-label", "Open navigation");
  navLinks.classList.remove("is-open");
  if (returnFocus) menuButton.focus();
}

if (menuButton && navLinks) {
  menuButton.addEventListener("click", () => {
    const open = menuButton.getAttribute("aria-expanded") !== "true";
    menuButton.setAttribute("aria-expanded", String(open));
    menuButton.setAttribute("aria-label", open ? "Close navigation" : "Open navigation");
    navLinks.classList.toggle("is-open", open);
  });

  navLinks.addEventListener("click", (event) => {
    if (event.target.closest("a")) closeMenu();
  });
  document.addEventListener("click", (event) => {
    if (!event.target.closest(".nav-pill")) closeMenu();
  });
  document.addEventListener("keydown", (event) => {
    if (event.key === "Escape" && menuButton.getAttribute("aria-expanded") === "true") closeMenu(true);
  });
  const mobileNav = window.matchMedia("(max-width: 850px)");
  mobileNav.addEventListener("change", () => closeMenu());
}

// A small scrollspy highlights the section currently being read.
const sections = [...document.querySelectorAll("main > section[id]")];
const sectionLinks = navLinks ? [...navLinks.querySelectorAll('a[href^="#"]')] : [];
let scrollPending = false;
function updateCurrentSection() {
  const compact = window.scrollY > 80;
  if (siteHeader && siteHeader.classList.contains("is-compact") !== compact) {
    const focused = document.activeElement;
    siteHeader.classList.toggle("is-compact", compact);
    if (menuButton && navLinks) {
      closeMenu();
      if (compact && navLinks.contains(focused)) menuButton.focus({ preventScroll: true });
      if (!compact && !window.matchMedia("(max-width: 850px)").matches && focused === menuButton) {
        siteHeader.querySelector(".wordmark").focus({ preventScroll: true });
      }
    }
  }
  if (!navLinks) {
    scrollPending = false;
    return;
  }
  const readingLine = window.innerHeight * 0.34;
  let current = "home";
  for (const section of sections) {
    if (section.getBoundingClientRect().top <= readingLine) current = section.id;
  }
  if (window.scrollY + window.innerHeight >= document.documentElement.scrollHeight - 8) current = "contact";
  for (const link of sectionLinks) {
    if (link.hash === `#${current}`) link.setAttribute("aria-current", "location");
    else link.removeAttribute("aria-current");
  }
  scrollPending = false;
}
window.addEventListener("scroll", () => {
  if (!scrollPending) {
    scrollPending = true;
    window.requestAnimationFrame(updateCurrentSection);
  }
}, { passive: true });
updateCurrentSection();

if (document.querySelector(".hero")) {
// The illustrated portrait gently follows the pointer. It is an image, not a 3D model.
// The animation stops when settled, off screen, on touch devices, or with reduced motion.
const hero = document.querySelector(".hero");
const portraitMover = document.querySelector(".portrait-mover");
const portraitGlow = document.querySelector(".portrait-glow");
const portraitButton = document.querySelector(".portrait-button");
const bubble = document.querySelector(".hello-bubble");
const motionPreference = window.matchMedia("(prefers-reduced-motion: reduce)");
const finePointer = window.matchMedia("(hover: hover) and (pointer: fine)");
let heroVisible = true;
let targetX = 0;
let targetY = 0;
let currentX = 0;
let currentY = 0;
let animationFrame = 0;
let helloTimeout;

function motionAllowed() { return !motionPreference.matches && finePointer.matches && heroVisible; }

function animatePortrait() {
  currentX += (targetX - currentX) * 0.09;
  currentY += (targetY - currentY) * 0.09;
  portraitMover.style.transform = `perspective(1000px) translate3d(${currentX * 9}px, ${currentY * 3}px, 0) rotateY(${currentX * 5}deg) rotateZ(${currentX * 1.1}deg)`;
  portraitGlow.style.transform = `translate3d(${currentX * 18}px, ${currentY * 10}px, 0)`;
  if (Math.abs(targetX - currentX) + Math.abs(targetY - currentY) > 0.002) {
    animationFrame = window.requestAnimationFrame(animatePortrait);
  } else {
    animationFrame = 0;
  }
}

function startMotion() {
  if (!animationFrame) animationFrame = window.requestAnimationFrame(animatePortrait);
}

function resetPortrait() {
  targetX = 0;
  targetY = 0;
  if (motionAllowed()) startMotion();
  else {
    window.cancelAnimationFrame(animationFrame);
    animationFrame = 0;
    currentX = 0;
    currentY = 0;
    portraitMover.style.transform = "";
    portraitGlow.style.transform = "";
  }
}

hero.addEventListener("pointermove", (event) => {
  if (!motionAllowed()) return;
  const bounds = hero.getBoundingClientRect();
  targetX = Math.max(-1, Math.min(1, ((event.clientX - bounds.left) / bounds.width - 0.5) * 2));
  targetY = Math.max(-1, Math.min(1, ((event.clientY - bounds.top) / bounds.height - 0.5) * 2));
  startMotion();
}, { passive: true });
hero.addEventListener("pointerleave", resetPortrait);
motionPreference.addEventListener("change", resetPortrait);
finePointer.addEventListener("change", resetPortrait);

// Keep both columns pinned while the next card swipes into the same slot.
// The page scroll drives the timeline; wheel/touch input remains native.
const skillsSection = document.querySelector(".skills");
const skillsStage = document.querySelector(".skills-stage");
const skillCards = [...document.querySelectorAll(".capability-card")];
const skillsDesktop = window.matchMedia("(min-width: 901px) and (min-height: 550px)");
let skillsFrame = 0;
let skillsVisible = true;
let skillsStart = 0;
let skillsStep = 0;
let skillsEntryDistance = 0;

function clampProgress(value) { return Math.max(0, Math.min(1, value)); }

function updateSkillCards() {
  skillsFrame = 0;
  if (!skillsVisible || !skillsSection.classList.contains("is-animated")) return;
  const position = Math.max(0, Math.min(skillCards.length - 1, (window.scrollY - skillsStart) / skillsStep));
  skillCards.forEach((card, index) => {
    // A short hold gives each skill its own reading moment between swipes.
    const arrival = index === 0 ? 1 : clampProgress((position - index + 1 - 0.2) / 0.8);
    const covered = clampProgress((position - index - 0.2) / 0.8);
    const restingAngle = index === skillCards.length - 1 ? 0 : (index % 2 ? -1.2 : 1.2);
    const rotation = index === 0 ? 0 : -14 * (1 - arrival) + restingAngle * arrival;
    card.style.setProperty("--card-y", `${((1 - arrival) * skillsEntryDistance).toFixed(2)}px`);
    card.style.setProperty("--card-x", `${(-16 * (1 - arrival)).toFixed(2)}px`);
    card.style.setProperty("--card-rotation", `${rotation.toFixed(2)}deg`);
    card.style.setProperty("--card-scale", (1 - covered * 0.025).toFixed(4));
    card.style.setProperty("--card-opacity", arrival > 0 && position - index < 2 ? "1" : "0");
    card.style.setProperty("--card-pointer", arrival > 0 && position - index < 2 ? "auto" : "none");
  });
}

function requestSkillsUpdate() {
  if (!skillsFrame && skillsVisible && skillsSection.classList.contains("is-animated")) {
    skillsFrame = window.requestAnimationFrame(updateSkillCards);
  }
}

function configureSkillsMotion() {
  let enabled = skillsDesktop.matches && !motionPreference.matches;
  skillsSection.classList.toggle("is-animated", enabled);
  if (enabled) {
    const stickyTop = parseFloat(window.getComputedStyle(skillsStage).top);
    const stageHeight = skillsStage.offsetHeight;
    // Preserve reading space if zoom or larger text makes the stage too tall.
    enabled = stageHeight <= window.innerHeight - stickyTop - 20;
    skillsSection.classList.toggle("is-animated", enabled);
    if (enabled) {
      const sectionStyle = window.getComputedStyle(skillsSection);
      skillsStep = Math.max(320, window.innerHeight * 0.65);
      skillsStart = skillsSection.getBoundingClientRect().top + window.scrollY + parseFloat(sectionStyle.paddingTop) - stickyTop;
      skillsEntryDistance = window.innerHeight - stickyTop + 80;
      skillsSection.style.setProperty("--skills-stage-height", `${stageHeight}px`);
      skillsSection.style.setProperty("--skills-travel", `${skillsStep * (skillCards.length - 1 + 0.25)}px`);
      requestSkillsUpdate();
    }
  }
  if (!enabled) {
    window.cancelAnimationFrame(skillsFrame);
    skillsFrame = 0;
    skillsSection.style.removeProperty("--skills-stage-height");
    skillsSection.style.removeProperty("--skills-travel");
    skillCards.forEach((card) => {
      ["--card-x", "--card-y", "--card-rotation", "--card-scale", "--card-opacity", "--card-pointer"].forEach(property => card.style.removeProperty(property));
    });
  }
}

window.addEventListener("scroll", requestSkillsUpdate, { passive: true });
window.addEventListener("resize", configureSkillsMotion, { passive: true });
skillsDesktop.addEventListener("change", configureSkillsMotion);
motionPreference.addEventListener("change", configureSkillsMotion);
configureSkillsMotion();
// Fonts and the portrait can change the section's document position after load.
window.addEventListener("load", configureSkillsMotion, { once: true });
if (document.fonts) document.fonts.ready.then(configureSkillsMotion);
if ("IntersectionObserver" in window) {
  const skillsObserver = new IntersectionObserver(([entry]) => {
    skillsVisible = entry.isIntersecting;
    if (skillsVisible) requestSkillsUpdate();
  }, { rootMargin: "100px" });
  skillsObserver.observe(skillsSection);
}

if ("IntersectionObserver" in window) {
  const heroObserver = new IntersectionObserver(([entry]) => {
    heroVisible = entry.isIntersecting;
    if (!heroVisible) resetPortrait();
  });
  heroObserver.observe(hero);
}

portraitButton.addEventListener("click", () => {
  window.clearTimeout(helloTimeout);
  bubble.textContent = "Hello there! 👋";
  bubble.classList.add("is-visible");
  helloTimeout = window.setTimeout(() => {
    bubble.classList.remove("is-visible");
    bubble.textContent = "";
  }, 2600);
});

// Mailto remains the primary contact action. Clipboard is an optional enhancement.
const copyButton = document.querySelector(".copy-email");
const copyStatus = document.querySelector(".copy-status");
let copyTimeout;
if (navigator.clipboard && window.isSecureContext) {
  copyButton.hidden = false;
  copyButton.addEventListener("click", async () => {
    window.clearTimeout(copyTimeout);
    try {
      await navigator.clipboard.writeText("emanabdulmalik18@gmail.com");
      copyStatus.textContent = "Email address copied.";
      copyButton.querySelector("use").setAttribute("href", "#i-check");
    } catch {
      copyStatus.textContent = "Please select the email address to copy it.";
    }
    copyTimeout = window.setTimeout(() => {
      copyStatus.textContent = "";
      copyButton.querySelector("use").setAttribute("href", "#i-copy");
    }, 3500);
  });
}

document.querySelector("#year").textContent = String(new Date().getFullYear());
}
