// Renders the listing artwork in marketing/images/ from real in-game screenshots, isometric renders of the mod's own
// models (marketing/source/renders/, made by renders.py), the mod logo and the bundled OFL fonts. Usage (from the
// repository root):
//   NODE_PATH="$(npm root -g)" node marketing/source/render.cjs [name...]
// Requires the `playwright` package with a Chromium build available. Set CHROMIUM to a Chromium executable to use
// that one instead of Playwright's own download.
const path = require('path');
const fs = require('fs');
const { chromium } = require('playwright');

// The release and the supported Minecraft versions. Adding a version is one line here
// (plus one row in the compatibility tables of curseforge/description.md and modrinth/description.md).
const RELEASE = { version: '1.0' };
const VERSIONS = [
  { minecraft: '1.20.1', loaders: ['Fabric', 'Forge'] },
  { minecraft: '1.21.1', loaders: ['Fabric', 'NeoForge'] },
  { minecraft: '26.2', loaders: ['Fabric', 'NeoForge'] },
  { minecraft: '26.3', loaders: ['Fabric', 'NeoForge'] },
];
const LOADERS = ['Fabric', 'Forge', 'NeoForge'].filter((loader) => VERSIONS.some((v) => v.loaders.includes(loader)));

const root = path.resolve(__dirname, '..', '..');
const out = path.join(root, 'marketing', 'images');
const shots = path.join(out, 'screenshots');
const fonts = path.join(__dirname, 'fonts');
const renders = path.join(__dirname, 'renders');
const url = (file) => 'file://' + file;
const shot = (name) => url(path.join(shots, name));
const source = (name) => url(path.join(__dirname, name));

// The fifteen plushies in PlushieSpecies order, with their classic texture name, display name and rarity
// (from plushie/PlushieSpecies.java and the en_us lang file).
const PLUSHIES = [
  ['strawberry_bunny', 'classic', 'Strawberry Bunny', 'Common'], ['pink_teddy_bear', 'pink', 'Pink Teddy Bear', 'Common'],
  ['lavender_kitty', 'lavender', 'Lavender Kitty', 'Common'], ['cloud_puppy', 'sky', 'Cloud Puppy', 'Uncommon'],
  ['cherry_blossom_fox', 'sakura', 'Cherry Blossom Fox', 'Uncommon'], ['angel_bunny', 'seraph', 'Angel Bunny', 'Rare'],
  ['sleepy_panda', 'classic', 'Sleepy Panda', 'Uncommon'], ['heart_bear', 'rose', 'Heart Bear', 'Rare'],
  ['little_duck', 'sunny', 'Little Duck', 'Common'], ['moon_cat', 'midnight', 'Moon Cat', 'Rare'],
  ['sugarplum_dragon', 'sugarplum', 'Sugarplum Dragon', 'Epic'], ['pastel_axolotl', 'blossom', 'Pastel Axolotl', 'Uncommon'],
  ['baby_deer', 'fawn', 'Baby Deer', 'Uncommon'], ['honey_bee', 'honey', 'Honey Bee', 'Common'],
  ['strawberry_cow', 'strawberry', 'Strawberry Cow', 'Rare'],
];
const RARITY = { Common: '#8f8aa3', Uncommon: '#3fa27a', Rare: '#4f8fd6', Epic: '#a65fd6' };
const plush = (id) => { const p = PLUSHIES.find((x) => x[0] === id); return `${id}@${p[1]}`; };
// Every texture of a plushie, classic first and the Sparkle Edition last.
const variantsOf = (id) => { const p = PLUSHIES.find((x) => x[0] === id);
  const all = fs.readdirSync(renders).filter((f) => f.startsWith(id + '@')).map((f) => f.slice(id.length + 1, -4));
  return [p[1], ...all.filter((v) => v !== p[1] && v !== 'sparkle'), 'sparkle'].map((v) => `${id}@${v}`); };

const css = `
@font-face { font-family: SpDisplay; src: url('${url(path.join(fonts, 'fredoka.ttf'))}'); }
@font-face { font-family: SpText; src: url('${url(path.join(fonts, 'nunito.ttf'))}'); font-weight: 400; }
@font-face { font-family: SpText; src: url('${url(path.join(fonts, 'nunito-extrabold.ttf'))}'); font-weight: 800; }
:root { --text:#574a63; --dim:#9a8ca6; --plum:#6b3f78; --pink:#ef6fa3; --pink-deep:#d24e88; --lav:#9a7ae0; --mint:#56c2a0;
        --sky:#6fb6ea; --butter:#f6cf5f; --cream:#fff7fb; --line:rgba(210,78,136,.28); }
* { box-sizing: border-box; }
html, body { margin: 0; background: var(--cream); color: var(--text); font-family: SpText, sans-serif; }
.stage { position: relative; overflow: hidden; background: linear-gradient(160deg, #fff7fb 0%, #fbeef8 55%, #eaf5f3 100%); }
.display { font-family: SpDisplay, sans-serif; }
.candy { background: linear-gradient(100deg, #e9558f 0%, #ef7fae 36%, #b68ae6 70%, #6fb6ea 100%);
         -webkit-background-clip: text; background-clip: text; color: transparent; }
.soft { filter: drop-shadow(0 3px 0 rgba(255,255,255,.95)) drop-shadow(0 10px 24px rgba(210,78,136,.24)); }
.kicker { font-family: SpDisplay, sans-serif; color: var(--lav); letter-spacing: .14em; font-size: 24px; text-transform: uppercase; }
.heart { display: inline-block; vertical-align: -4px; margin-right: 12px; }
.dots { background-color: #f9c6dc; background-image: radial-gradient(circle, #fff 28%, transparent 31%);
        background-size: 16px 16px; background-position: 0 0; }
.dots.lav { background-color: #d8c8f4; } .dots.mint { background-color: #bfe8da; }
.stitch { height: 6px; border-radius: 3px; background: repeating-linear-gradient(90deg, var(--pink) 0 12px, transparent 12px 18px, var(--lav) 18px 30px, transparent 30px 36px, var(--mint) 36px 48px, transparent 48px 54px); opacity: .8; }
.frame { position: relative; border: 7px solid #fff; border-radius: 26px; overflow: hidden; background: #fff;
         box-shadow: 0 0 0 2px rgba(239,111,163,.55), 0 26px 60px rgba(107,63,120,.22), 0 6px 14px rgba(107,63,120,.12); }
.frame > img { display: block; width: 100%; }
canvas.motes { position: absolute; left: 0; top: 0; width: 100%; height: 100%; pointer-events: none; }
.wash { position: absolute; inset: 0; background: radial-gradient(ellipse at 70% 45%, rgba(255,255,255,0) 30%, rgba(255,244,250,.6) 100%); }
.backdrop { position: absolute; inset: -40px; background-size: cover; background-position: center; filter: blur(18px) brightness(1.15) saturate(1.05); }
.pill { display: inline-block; border-radius: 999px; padding: 9px 22px 7px; margin-right: 12px; font-family: SpDisplay, sans-serif; font-size: 22px;
        color: #fff; background: linear-gradient(100deg, #ef6fa3, #b68ae6); box-shadow: 0 6px 16px rgba(210,78,136,.3); }
.pill.ghost { color: var(--plum); background: rgba(255,255,255,.9); box-shadow: 0 0 0 2px rgba(154,122,224,.6), 0 6px 16px rgba(107,63,120,.12); }
.label { position: absolute; font-family: SpDisplay, sans-serif; font-size: 23px; color: var(--plum); background: rgba(255,255,255,.95);
         border-radius: 999px; padding: 8px 20px 6px; white-space: nowrap; box-shadow: 0 0 0 2px rgba(239,111,163,.7), 0 10px 26px rgba(107,63,120,.25); z-index: 2; }
ul.points { list-style: none; padding: 0; margin: 0; }
ul.points li { position: relative; padding-left: 38px; margin: 0 0 16px; font-size: 27px; line-height: 1.3; color: var(--text); }
ul.points li::before { content: ''; position: absolute; left: 0; top: 8px; width: 22px; height: 22px; background: var(--pink);
  -webkit-mask: var(--heart) center / contain no-repeat; mask: var(--heart) center / contain no-repeat; }
ul.points li:nth-child(3n+2)::before { background: var(--lav); }
ul.points li:nth-child(3n)::before { background: var(--mint); }
:root { --heart: url("data:image/svg+xml;utf8,<svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24'><path d='M12 21.5C5 16.6 1.5 12.9 1.5 8.6 1.5 5.3 4 3 7 3c2.1 0 3.8 1.1 5 2.9C13.2 4.1 14.9 3 17 3c3 0 5.5 2.3 5.5 5.6 0 4.3-3.5 8-10.5 12.9z'/></svg>"); }
b.hi { color: var(--plum); font-weight: 800; }
.stats { display: flex; gap: 14px; margin-top: 22px; }
.stats > div { flex: 1; padding: 14px 20px 12px; border-radius: 20px; background: rgba(255,255,255,.93);
               box-shadow: 0 0 0 2px rgba(239,111,163,.4), 0 14px 34px rgba(107,63,120,.15); text-align: center; }
.stats .t { font-family: SpText, sans-serif; font-weight: 800; font-size: 16px; letter-spacing: .1em; text-transform: uppercase; color: var(--dim); }
.stats .v { font-family: SpDisplay, sans-serif; font-size: 34px; color: var(--plum); margin-top: 2px; white-space: nowrap; }
.tile { position: relative; border-radius: 20px; background: rgba(255,255,255,.93); text-align: center;
        box-shadow: 0 0 0 2px rgba(239,111,163,.4), 0 14px 30px rgba(107,63,120,.15); }
.tile .art { display: flex; align-items: center; justify-content: center; }
.tile .art img { image-rendering: pixelated; max-width: 100%; max-height: 100%; }
.tile .n { font-family: SpDisplay, sans-serif; color: var(--plum); line-height: 1.1; }
.tile .s { font-family: SpText, sans-serif; font-weight: 800; color: var(--dim); font-size: 15px; letter-spacing: .04em; }
pre.code { margin: 0; font-family: 'DejaVu Sans Mono', monospace; font-size: 21px; line-height: 1.5; color: var(--text); }
`;

// A plump heart with a white outline and a highlight, the plushie counterpart of the cottage kit's daisies.
const heartSvg = (fill, size) => `<svg class="heart" width="${size}" height="${size}" viewBox="0 0 24 24" style="display:inline-block">
  <path d="M12 21.5C5 16.6 1.5 12.9 1.5 8.6 1.5 5.3 4 3 7 3c2.1 0 3.8 1.1 5 2.9C13.2 4.1 14.9 3 17 3c3 0 5.5 2.3 5.5 5.6 0 4.3-3.5 8-10.5 12.9z"
        fill="${fill}" stroke="#fff" stroke-width="1.4"/><ellipse cx="7.2" cy="8" rx="2" ry="1.3" fill="#fff" opacity=".6" transform="rotate(-30 7.2 8)"/></svg>`;

// Floating hearts, four-point twinkles and dots. Deterministic so renders are reproducible.
const motes = (count = 40, seed = 7) => `<canvas class="motes"></canvas><script>
(() => { const c = document.currentScript.previousElementSibling; const r = c.getBoundingClientRect();
  c.width = r.width; c.height = r.height; const g = c.getContext('2d'); let s = ${seed};
  const rnd = () => (s = (s * 16807) % 2147483647) / 2147483647;
  const tints = ['239,111,163', '182,138,230', '86,194,160', '111,182,234'];
  const heart = (x, y, z, a, tint) => { g.save(); g.translate(x, y); g.scale(z / 10, z / 10); g.fillStyle = 'rgba(' + tint + ',' + a + ')';
    g.beginPath(); g.moveTo(0, 8); g.bezierCurveTo(-11, 0, -9, -9, 0, -4); g.bezierCurveTo(9, -9, 11, 0, 0, 8); g.fill(); g.restore(); };
  const twinkle = (x, y, z, a) => { g.save(); g.translate(x, y); g.fillStyle = 'rgba(255,255,255,' + a + ')'; g.beginPath();
    for (let i = 0; i < 8; i++) { const t = i * Math.PI / 4, d = i % 2 ? z * .35 : z * 1.6; g.lineTo(Math.sin(t) * d, -Math.cos(t) * d); }
    g.closePath(); g.fill(); g.restore(); };
  for (let i = 0; i < ${count}; i++) { const x = rnd() * c.width, y = rnd() * c.height, a = .35 + rnd() * .45, z = 3 + rnd() * 5;
    const k = rnd(); if (k < .45) heart(x, y, z * 2.2, a * .8, tints[Math.floor(rnd() * 4)]); else if (k < .8) twinkle(x, y, z, a + .15);
    else { g.fillStyle = 'rgba(' + tints[Math.floor(rnd() * 4)] + ',' + a * .6 + ')'; g.beginPath(); g.arc(x, y, z * .6, 0, Math.PI * 2); g.fill(); } } })();
</script>`;

// Isometric renders of the mod's own models (renders.py), drawn pixel-sharp. Waits for every image to load.
const loadScript = `<script>
(() => { const imgs = [...document.images]; let left = imgs.length;
  const done = () => { if (--left <= 0) document.body.dataset.items = 'ready'; };
  if (!left) document.body.dataset.items = 'ready';
  for (const i of imgs) { if (i.complete) done(); else { i.onload = done; i.onerror = done; } } })();
</script>`;
const model = (name, h) => `<img src="${url(path.join(renders, `${name}.png`))}" style="height:${h}px;image-rendering:pixelated">`;
// A tile with a model render and a caption.
const tile = (name, title, { h = 120, art = 140, size = 20, sub = '', subColor = '' } = {}) => `<div class="tile" style="padding:12px 8px 12px">
    <div class="art" style="height:${art}px">${model(name, h)}</div>
    <div class="n" style="font-size:${size}px;margin-top:6px">${title}</div>${sub ? `<div class="s"${subColor ? ` style="color:${subColor}"` : ''}>${sub}</div>` : ''}</div>`;
const grid = (cols, items, gap = 14) => `<div style="display:grid;grid-template-columns:repeat(${cols},1fr);gap:${gap}px">${items.join('')}</div>`;

const page = (width, height, body, transparent = false) => `<!doctype html><html><head><meta charset="utf-8"><style>${css}
body { width: ${width}px; height: ${height}px; } .stage { width: ${width}px; height: ${height}px; }
${transparent ? 'html, body, .stage { background: transparent; }' : ''}</style></head>
<body><div class="stage">${body}</div>${loadScript}</body></html>`;

// A section heading: a stitched ribbon with the title, readable on CurseForge's and Modrinth's light and dark themes.
const header = (title) => page(1400, 120, `
  <div style="position:absolute;inset:0;display:flex;align-items:center;justify-content:center;gap:22px">
    <div class="stitch" style="width:230px;-webkit-mask-image:linear-gradient(90deg,transparent,#000);mask-image:linear-gradient(90deg,transparent,#000)"></div>
    ${heartSvg('#b68ae6', 34)}
    <div style="position:relative;padding:14px 54px 12px;border-radius:999px;background:linear-gradient(100deg,#ef6fa3 0%,#e889c0 50%,#b68ae6 100%);
                box-shadow:0 0 0 4px #fff,0 0 0 6px rgba(154,122,224,.75),0 12px 28px rgba(107,63,120,.28)">
      <div style="position:absolute;inset:5px;border-radius:999px;border:2px dashed rgba(255,255,255,.75)"></div>
      <div class="display" style="position:relative;font-size:46px;line-height:1;color:#fff;white-space:nowrap;text-shadow:0 3px 0 rgba(150,50,110,.38)">${title}</div></div>
    ${heartSvg('#56c2a0', 34)}
    <div class="stitch" style="width:230px;-webkit-mask-image:linear-gradient(90deg,#000,transparent);mask-image:linear-gradient(90deg,#000,transparent)"></div>
  </div>`, true);

// A feature card: copy on the left, artwork on the right. Bullets never end on a one-word line.
const card = ({ kicker, title, points, art, width = 1600, height = 900, artWidth = 980 }) => page(width, height, `
  <div class="backdrop" style="background-image:url('${source('backdrop.jpg')}');opacity:.45"></div>
  <div style="position:absolute;inset:0;background:linear-gradient(90deg,rgba(255,247,251,.96) 0%,rgba(255,247,251,.88) 34%,rgba(250,240,250,.6) 62%,rgba(236,246,244,.42) 100%)"></div>
  <div class="wash"></div>${motes()}
  <div class="dots" style="position:absolute;left:0;right:0;top:0;height:14px;opacity:.9"></div>
  <div class="dots lav" style="position:absolute;left:0;right:0;bottom:0;height:14px;opacity:.9"></div>
  <div style="position:absolute;left:76px;top:0;bottom:0;width:${width - artWidth - 176}px;display:flex;flex-direction:column;justify-content:center">
    <div class="kicker"${kicker.length > 22 ? ' style="font-size:20px;letter-spacing:.1em"' : ''}>${heartSvg('#ef6fa3', 26)}${kicker}</div>
    <div class="display candy soft" style="font-size:66px;line-height:1.02;margin:16px 0 22px">${title}</div>
    <div class="stitch" style="width:70%;margin-bottom:30px"></div>
    <ul class="points">${points.map((point) => `<li>${point.replace(/ (\S+)$/, '&nbsp;$1')}</li>`).join('')}</ul>
  </div>
  <div style="position:absolute;right:56px;top:50%;transform:translateY(-50%);width:${artWidth}px">${art}</div>`);

// An exact region (x, y, w, h in source pixels, 1920x1080 by default) of a capture, scaled to W pixels wide.
const region = (file, x, y, w, h, W, full = 1920) => `<div style="position:relative;width:${W}px;height:${Math.round(W * h / w)}px;overflow:hidden">
    <img src="${shot(file)}" style="position:absolute;max-width:none;width:${W * full / w}px;left:${-x * W / w}px;top:${-y * W / w}px"></div>`;
const framed = (inner, style = '') => `<div class="frame" style="${style}">${inner}</div>`;
const label = (text, style) => `<div class="label" style="${style}">${text}</div>`;
const stats = (items) => `<div class="stats">${items.map(([t, v, flex]) =>
  `<div${flex ? ` style="flex:${flex}"` : ''}><div class="v">${v}</div><div class="t">${t}</div></div>`).join('')}</div>`;
// Two overlapping captures, the first top left and the second bottom right.
const pair = (first, second, firstLabel, secondLabel, height = 640, width = '73%') => `<div style="position:relative;height:${height}px">
    <div class="frame" style="position:absolute;left:0;top:0;width:${width}">${first}</div>
    <div class="frame" style="position:absolute;right:0;bottom:0;width:${width}">${second}</div>
    ${firstLabel ? label(firstLabel, 'left:24px;top:22px') : ''}${secondLabel ? label(secondLabel, 'right:24px;bottom:22px') : ''}</div>`;
const code = (file, body) => `<div class="frame" style="background:rgba(255,255,255,.96);padding:22px 32px">
  <div style="font-family:SpText;font-weight:800;font-size:18px;letter-spacing:.08em;color:var(--dim);margin-bottom:10px">${file}</div><pre class="code">${body}</pre></div>`;
const k = (t) => `<span style="color:#d24e88">${t}</span>`;
const v = (t) => `<span style="color:#3fa27a">${t}</span>`;
const cm = (t) => `<span style="color:#b9abc4">${t}</span>`;

const versionText = VERSIONS.map((ver) => ver.minecraft).join(' · ');

const assets = {
  banner: () => page(1800, 600, `
    <img src="${shot('bedroom-closeup.jpg')}" style="position:absolute;right:-30px;top:-40px;width:1180px;-webkit-mask-image:linear-gradient(90deg,transparent 0%,#000 30%);mask-image:linear-gradient(90deg,transparent 0%,#000 30%)">
    <div style="position:absolute;inset:0;background:linear-gradient(90deg,rgba(255,247,251,.98) 0%,rgba(255,247,251,.96) 42%,rgba(255,247,251,.5) 60%,rgba(255,247,251,0) 76%)"></div>
    ${motes(46, 11)}
    <div class="dots" style="position:absolute;left:0;right:0;top:0;height:16px"></div>
    <div class="dots lav" style="position:absolute;left:0;right:0;bottom:0;height:16px"></div>
    <div style="position:absolute;left:86px;top:58px;width:1000px">
      <div class="kicker">${heartSvg('#ef6fa3', 26)}Version ${RELEASE.version} · Sugarplum Studios</div>
      <div class="display candy soft" style="font-size:104px;line-height:1;white-space:nowrap;margin:16px 0 4px">Sugarplum Plushies</div>
      <div class="stitch" style="width:640px;margin:18px 0 24px"></div>
      <div style="font-family:SpDisplay;font-size:42px;color:var(--plum);line-height:1.2">Craft, collect, display and cuddle.</div>
      <div style="font-size:27px;color:var(--text);line-height:1.35;margin-top:8px">15 pastel plushies in 60 variants, a workshop to sew them,<br>furniture to show them off and a collection book.</div>
      <div style="margin-top:30px">${LOADERS.map((l) => `<span class="pill">${l}</span>`).join('')}<span class="pill ghost">Fabric API on Fabric</span></div>
      <div style="margin-top:16px;font-family:SpDisplay;font-size:26px;color:var(--dim)">Minecraft ${versionText}</div>
    </div>
`),

  icon: () => page(512, 512, `
    <div style="position:absolute;inset:0;background:radial-gradient(circle at 50% 40%,#fff7fb 0%,#f8e6f2 55%,#e2f1ee 100%)"></div>${motes(22, 5)}
    <div class="dots" style="position:absolute;left:46px;top:46px;width:420px;height:420px;border-radius:50%;
                box-shadow:0 0 0 6px #fff,0 0 0 10px #b68ae6,0 24px 50px rgba(107,63,120,.3)"></div>
    <div style="position:absolute;left:74px;top:74px;width:364px;height:364px;border-radius:50%;background:radial-gradient(circle at 50% 45%,#fffafd,#fbeaf4)"></div>
    <div style="position:absolute;left:74px;top:74px;width:364px;height:364px;border-radius:50%;border:3px dashed rgba(239,111,163,.55)"></div>
    <div style="position:absolute;left:74px;top:70px;width:364px;height:364px;display:flex;align-items:center;justify-content:center">
      ${model(plush('pink_teddy_bear'), 250)}</div>
    ${[[256, 34], [478, 256], [256, 478], [34, 256]].map(([x, y], i) => `<div style="position:absolute;left:${x - 20}px;top:${y - 20}px">${heartSvg(['#ef6fa3', '#b68ae6', '#56c2a0', '#6fb6ea'][i], 40)}</div>`).join('')}`),

  'card-plushies': () => card({
    kicker: '15 plushies', title: 'Fifteen soft<br>friends',
    points: ['Bunnies, bears, kitties, a fox, a panda and a dragon', 'Each one a real 3D model with a stitched face',
      'Own cuddle animation, sound and particles', 'They breathe, bob, sway, wag and flap while idle', 'Common, Uncommon, Rare and one Epic'],
    art: grid(5, PLUSHIES.map(([id, , name, rarity]) => tile(plush(id), name, { h: 104, art: 114, size: 18, sub: rarity, subColor: RARITY[rarity] })), 12) +
      stats([['Plushies', '15'], ['Variants', '60'], ['Rarities', '4'], ['Blocks', '29']]) }),

  'card-variants': () => card({
    kicker: '60 variants', title: 'Three colours<br>and a sparkle',
    points: ['Every plushie comes in three colourways', 'One extra ingredient picks the colourway',
      'Plus a rare pearly Sparkle Edition with gold trim', 'Sparkles glint, glow and twinkle', 'Collect all 60 for the Rainbow Trophy'],
    art: `<div style="display:flex;flex-direction:column;gap:12px">${['sugarplum_dragon', 'cherry_blossom_fox', 'heart_bear', 'honey_bee'].map((id) => {
      const p = PLUSHIES.find((x) => x[0] === id); return `
      <div class="tile" style="display:flex;align-items:center;gap:14px;padding:6px 18px;text-align:left">
        <div style="width:240px;flex:none"><div class="display" style="font-size:28px;color:var(--plum);white-space:nowrap">${p[2]}</div>
          <div class="s" style="font-size:15px;color:${RARITY[p[3]]}">${p[3]}</div></div>
        <div style="flex:1;display:flex;align-items:flex-end;justify-content:space-around;height:172px;padding-bottom:6px">
          ${variantsOf(id).map((n, i) => `<div style="text-align:center">${model(n, 150)}${i === 3 ? '<div class="s" style="color:#c99a1c;margin-top:-4px">Sparkle</div>' : ''}</div>`).join('')}</div>
      </div>`; }).join('')}</div>` }),

  'card-cuddle': () => card({
    kicker: 'Right-click', title: 'Cuddle<br>them',
    points: ['Hops, hugs, spins, twirls and a big yawn', 'Hearts, petals, bubbles, clouds and Zzz’s', 'A little personality message for each one',
      'Sneak to pick one up, a brush turns it', 'Plushies on furniture cuddle back too'],
    art: framed(region('cuddle.jpg', 240, 180, 1440, 810, 978)) + `<div style="margin-top:18px">${grid(5, [
      ['strawberry_bunny', 'Double hop'], ['sleepy_panda', 'Big yawn'], ['cherry_blossom_fox', 'Spin'], ['heart_bear', 'Heartbeat'], ['sugarplum_dragon', 'Wing-flap hop']]
      .map(([id, s]) => tile(plush(id), PLUSHIES.find((x) => x[0] === id)[2], { h: 70, art: 80, size: 17, sub: s })), 10)}</div>` }),

  'card-furniture': () => card({
    kicker: 'Display furniture', title: 'Show them<br>off',
    points: ['Wall shelves hold 2, cabinets hold 4', 'Display stands spin like a turntable', 'Beds in four colours, the plushie snores Zzz’s',
      'Gift boxes in four colours hold 9 stacks', 'Comparators read how full a display is'],
    art: framed(region('bedroom.jpg', 0, 0, 1920, 1080, 978)) + `<div style="margin-top:18px">${grid(6, [
      ['plushie_shelf', 'Shelf', 'Holds 2'], ['display_stand', 'Stand', 'Spins'], ['display_cabinet', 'Cabinet', 'Holds 4'],
      ['pink_plushie_bed', 'Bed', '4 colours'], ['mint_plushie_box', 'Gift Box', '9 slots'], ['golden_collector_trophy', 'Trophy', 'A reward']]
      .map(([n, t, s]) => tile(n, t, { h: 70, art: 80, size: 17, sub: s })), 10)}</div>` }),

  'card-workshop': () => card({
    kicker: 'Plushie Workshop', title: 'Sew your<br>own',
    points: ['Pick a pattern and press Sew!', 'Ingredients come straight from your inventory', 'Green has enough, red is short, a dot is new',
      'Fabric, stuffing, button eyes, thread and ribbon', 'Shift-click sews up to 16 at once'],
    art: framed(region('workshop.jpg', 465, 60, 990, 960, 760), 'width:760px;margin:0 auto') }),

  'card-book': () => card({
    kicker: 'Plushie Collection Book', title: 'Collect<br>them all',
    points: ['Every plushie and its four variants', 'New finds pop a “New plushie collected!” toast', 'Rarity, personality and what you own',
      'Seven milestones with fixed rewards', 'Saved on the player, kept through death'],
    art: pair(region('book.jpg', 420, 150, 1080, 780, 715), region('book-rewards.jpg', 420, 150, 1080, 780, 715), '', '', 720, '73%') }),

  'card-names': () => card({
    kicker: 'Make them yours', title: 'Name your<br>plushies',
    points: ['A renamed name tag names a placed plushie', 'Anvil-renamed plushies keep their names', 'The name floats above it when you look',
      'Picking it up keeps its variant and name', 'Pick-block gives you the exact variant'],
    art: framed(region('names.jpg', 240, 170, 1440, 810, 978)) }),

  'card-glow': () => card({
    kicker: 'After dark', title: 'Little lights<br>at night',
    points: ['Moon Cat glows and its moon orbits', 'Angel Bunny glows under a spinning halo', 'Sparkle Editions glow and twinkle',
      'Moon Cat twinkles more at night', 'Collector’s Trophies glow and sparkle'],
    art: framed(region('night.jpg', 180, 300, 1560, 780, 978)) + stats([['Moon Cat', '7'], ['Angel Bunny', '5'], ['Sparkle Editions', '5'], ['Rainbow Trophy', '10']]) }),

  'card-config': () => card({
    kicker: 'Make it yours', title: 'Set the<br>rules',
    points: ['Cuddle messages, particles and cooldown', 'Sneak pick-up and name tag use', 'Collection rewards and trader trades',
      'Idle animations and their speed, per player', 'When name plates show, how fast stands spin'],
    art: `<div style="display:flex;flex-direction:column;gap:22px">
      ${code('config/sugarplumplushies-common.toml', `${k('[interactions]')}
  ${k('interactionMessages')} = ${v('true')}
  ${k('interactionCooldownTicks')} = ${v('12')}
  ${k('sneakPickUp')} = ${v('true')}
${k('[collection]')}
  ${k('collectionRewards')} = ${v('true')}
${k('[world]')}
  ${k('wanderingTraderTrades')} = ${v('true')}`)}
      ${code('config/sugarplumplushies-client.toml', `${k('[rendering]')}
  ${k('idleAnimations')} = ${v('true')}
  ${k('animationSpeed')} = ${v('1.0')}
  ${k('nameplates')} = ${v('"LOOKING"')}   ${cm('# ALWAYS, LOOKING or NEVER')}
  ${k('displayStandSpinSpeed')} = ${v('1.5')}`)}</div>` }),
};

const headers = { 'header-plushies': 'The Plushies', 'header-variants': 'Colourways & Sparkles', 'header-cuddle': 'Cuddles',
  'header-furniture': 'Furniture', 'header-workshop': 'The Plushie Workshop', 'header-book': 'The Collection Book',
  'header-names': 'Naming', 'header-glow': 'After Dark', 'header-recipes': 'Crafting', 'header-config': 'Configuration',
  'header-everything': 'Everything Else', 'header-compatibility': 'Downloads & Compatibility', 'header-faq': 'Questions',
  'header-gallery': 'Gallery' };
for (const [name, title] of Object.entries(headers)) assets[name] = () => header(title);
assets.divider = () => page(1400, 40, `<div style="position:absolute;inset:0;display:flex;align-items:center;justify-content:center;gap:14px">
  <div class="stitch" style="width:480px;-webkit-mask-image:linear-gradient(90deg,transparent,#000);mask-image:linear-gradient(90deg,transparent,#000)"></div>
  ${heartSvg('#b68ae6', 22)}${heartSvg('#ef6fa3', 28)}${heartSvg('#56c2a0', 22)}
  <div class="stitch" style="width:480px;-webkit-mask-image:linear-gradient(90deg,#000,transparent);mask-image:linear-gradient(90deg,#000,transparent)"></div></div>`, true);

(async () => {
  const wanted = process.argv.slice(2);
  const browser = await chromium.launch(process.env.CHROMIUM
    ? { executablePath: process.env.CHROMIUM, args: ['--no-sandbox', '--use-angle=swiftshader', '--enable-unsafe-swiftshader'] }
    : {});
  fs.mkdirSync(out, { recursive: true });
  for (const [name, build] of Object.entries(assets)) {
    if (wanted.length && !wanted.includes(name)) continue;
    const html = build();
    const size = html.match(/body \{ width: (\d+)px; height: (\d+)px; \}/);
    const tab = await browser.newPage({ viewport: { width: +size[1], height: +size[2] }, deviceScaleFactor: 1 });
    const file = path.join(__dirname, `.render-${name}.html`);
    fs.writeFileSync(file, html);
    await tab.goto(url(file));
    await tab.evaluate(() => document.fonts.ready);
    await tab.waitForLoadState('networkidle');
    await tab.waitForFunction(() => document.body.dataset.items === 'ready');
    await tab.screenshot({ path: path.join(out, `${name}.png`), omitBackground: name === 'divider' || name.startsWith('header-') });
    fs.unlinkSync(file);
    await tab.close();
    console.log('rendered', name);
  }
  await browser.close();
})().catch((error) => { console.error(error); process.exit(1); });
