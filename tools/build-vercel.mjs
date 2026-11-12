import { cp, mkdir, rm } from "node:fs/promises";

const root = new URL("../", import.meta.url);
const output = new URL("dist/", root);
const siteEntries = [
  "index.html",
  "styles.css",
  "projects.css",
  "script.js",
  "ruya-realty-demo.js",
  "securescan-demo.js",
  "projects",
  "assets",
  "downloads",
];

await rm(output, { recursive: true, force: true });
await mkdir(output);

for (const entry of siteEntries) {
  await cp(new URL(entry, root), new URL(entry, output), {
    recursive: true,
    filter: (source) => !source.endsWith(".md"),
  });
}
