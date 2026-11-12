(() => {
  'use strict';
  const image = document.querySelector('#scan-image');
  const result = document.querySelector('#scan-result');
  const run = document.querySelector('#scan-run');
  const file = document.querySelector('#scan-file');
  const download = document.querySelector('#scan-download');
  let objectUrl;
  let revision = 0;
  const show = (title, message, kind = '') => {
    result.dataset.kind = kind;
    const heading = document.createElement('strong'); heading.textContent = title;
    const copy = document.createElement('p'); copy.textContent = message;
    result.replaceChildren(heading, copy);
  };
  const reset = () => { revision++; run.disabled = false; run.textContent = 'Scan QR code'; show('Ready to scan', 'Press Scan QR code to read this image.'); };
  document.querySelectorAll('[name="qr-sample"]').forEach(input => {
    input.addEventListener('change', () => {
      if (objectUrl) URL.revokeObjectURL(objectUrl);
      objectUrl = undefined; file.value = '';
      image.src = `../assets/securescan/${input.value}.gif`;
      image.alt = `Sample QR code: ${input.value === 'text' ? 'Hello from SecureScan' : input.value + '://example.com'}`;
      download.href = image.src; download.download = `securescan-test-${input.value}.gif`; download.hidden = false;
      reset();
    });
  });
  file.addEventListener('change', () => {
    const selected = file.files[0]; if (!selected) return;
    if (!['image/png','image/jpeg','image/webp','image/gif','image/svg+xml'].includes(selected.type) || selected.size > 10 * 1024 * 1024) {
      file.value = ''; show('Choose a smaller image', 'Use a PNG, JPG, WebP, GIF or SVG up to 10 MB.', 'warning'); return;
    }
    if (objectUrl) URL.revokeObjectURL(objectUrl);
    objectUrl = URL.createObjectURL(selected); image.src = objectUrl; image.alt = 'Your imported QR image';
    document.querySelectorAll('[name="qr-sample"]').forEach(input => { input.checked = false; });
    download.hidden = true; reset();
  });
  run.addEventListener('click', async () => {
    const current = revision; run.disabled = true; run.textContent = 'Reading QR…';
    try {
      await image.decode();
      if (current !== revision) return;
      const scale = Math.min(1, 1600 / Math.max(image.naturalWidth, image.naturalHeight));
      const canvas = document.createElement('canvas');
      canvas.width = Math.max(1, Math.round(image.naturalWidth * scale));
      canvas.height = Math.max(1, Math.round(image.naturalHeight * scale));
      const ctx = canvas.getContext('2d', { willReadFrequently: true });
      ctx.fillStyle = '#fff'; ctx.fillRect(0, 0, canvas.width, canvas.height); ctx.drawImage(image, 0, 0, canvas.width, canvas.height);
      const pixels = ctx.getImageData(0, 0, canvas.width, canvas.height);
      const code = jsQR(pixels.data, pixels.width, pixels.height, { inversionAttempts: 'attemptBoth' });
      if (!code) { show('No QR code found', 'Try a clear, uncropped image with the whole QR code visible.', 'warning'); return; }
      let url;
      try { if (/^https?:\/\/[^\s]+$/i.test(code.data.trim())) url = new URL(code.data.trim()); } catch {}
      if (url?.hostname && url.protocol === 'https:') show('HTTPS link detected', code.data, 'https');
      else if (url?.hostname && url.protocol === 'http:') show('HTTP link — unencrypted', code.data, 'warning');
      else show('QR decoded · not a web link', code.data || '(Empty QR content)', 'text');
    } catch { show('Could not read this image', 'Please choose another QR image and try again.', 'warning'); }
    finally { if (current === revision) { run.disabled = false; run.textContent = 'Scan QR code'; } }
  });
})();
