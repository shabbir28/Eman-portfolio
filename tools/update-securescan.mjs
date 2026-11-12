import {readFileSync,writeFileSync} from 'node:fs';
for (const file of ['index.html','projects/securescan.html']) {
 let s=readFileSync(file,'utf8');
 const prefix=file==='index.html'?'assets/':'../assets/';
 s=s.replace(/<div class="work-cover tone-plum" aria-hidden="true">[\s\S]*?<span class="work-cover-arrow">[\s\S]*?<\/div>/,`<div class="work-cover work-cover-laptop"><img class="laptop-cover-image" src="${prefix}securescan-cover.png" alt="Illustrative SecureScan phone mockups on a plum background" loading="lazy"></div>`);
 s=s.replaceAll('An Android app that scans QR codes, checks scanned links and alerts users to potentially suspicious links.','An Android QR scanner with camera scanning, gallery import and HTTP/HTTPS link checks.');
 s=s.replace('Illustrative project artwork. Actual screenshots will be added here.','Illustrative project cover inspired by the original app artwork.');
 s=s.replace('SecureScan is an Android application focused on QR-code scanning and checking the links those codes contain. It alerts users when a scanned link appears potentially suspicious.','SecureScan scans QR codes using the camera or images imported from the gallery. Built with Java, XML, CameraX and ML Kit, its camera scanner distinguishes HTTP from HTTPS links. This checks the URL scheme; it does not establish whether a website is trustworthy.');
 writeFileSync(file,s);
}
