// Dependency-free local preview. Run: node server.mjs [port]
import { createServer } from "node:http";
import { readFile, stat } from "node:fs/promises";
import { dirname, extname, resolve, sep } from "node:path";
import { fileURLToPath } from "node:url";

const root = dirname(fileURLToPath(import.meta.url));
const port = Number(process.argv[2] || 4173);
const types = { ".gif": "image/gif", ".apk": "application/vnd.android.package-archive", ".mjs": "text/javascript; charset=utf-8", ".zip": "application/zip", ".jpg": "image/jpeg", ".html": "text/html; charset=utf-8", ".css": "text/css; charset=utf-8", ".js": "text/javascript; charset=utf-8", ".svg": "image/svg+xml", ".png": "image/png", ".webp": "image/webp", ".woff2": "font/woff2", ".ico": "image/x-icon" };

createServer(async (request, response) => {
  if (request.method !== "GET" && request.method !== "HEAD") {
    response.writeHead(405, { Allow: "GET, HEAD" }).end();
    return;
  }
  try {
    const pathname = decodeURIComponent(new URL(request.url, "http://localhost").pathname);
    const requestedPath = pathname === "/" ? "/index.html" : pathname;
    const filePath = resolve(root, `.${requestedPath}`);
    if (!filePath.startsWith(root + sep) || requestedPath.split(/[\\/]/).some(part => part.startsWith(".")) || !types[extname(filePath)]) {
      response.writeHead(403).end("Forbidden");
      return;
    }
    if (!(await stat(filePath)).isFile()) throw new Error("Not a file");
    const contents = await readFile(filePath);
    response.writeHead(200, { "Content-Type": types[extname(filePath)], "Cache-Control": "no-cache", "X-Content-Type-Options": "nosniff" });
    response.end(request.method === "HEAD" ? undefined : contents);
  } catch {
    response.writeHead(404, { "Content-Type": "text/plain; charset=utf-8" }).end("Not found");
  }
}).listen(port, "127.0.0.1", () => console.log(`Portfolio preview: http://127.0.0.1:${port}`));
