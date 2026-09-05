#!/usr/bin/env bash
# Regenerates README.html from README.md.
# README.html is generated, self-contained (no CDN/network needed to view it), and committed
# alongside README.md so it can be opened offline. Re-run this after every README.md change.
set -euo pipefail

cd "$(dirname "$0")/.."

BODY="$(npx --yes marked README.md | node scripts/slugify-headings.js)"

cat > README.html <<HTML
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>Social network</title>
<style>
  body {
    max-width: 900px;
    margin: 2rem auto;
    padding: 0 1.5rem;
    font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Helvetica, Arial, sans-serif;
    color: #1f2328;
    line-height: 1.6;
  }
  h1, h2, h3 { border-bottom: 1px solid #d1d9e0; padding-bottom: 0.3rem; }
  a { color: #0969da; text-decoration: none; }
  a:hover { text-decoration: underline; }
  code { background: #f6f8fa; padding: 0.15em 0.4em; border-radius: 4px; font-size: 0.9em; }
  pre { background: #f6f8fa; padding: 1rem; border-radius: 6px; overflow-x: auto; }
  pre code { background: none; padding: 0; }
  table { border-collapse: collapse; width: 100%; margin: 1rem 0; }
  th, td { border: 1px solid #d1d9e0; padding: 0.5rem 0.75rem; text-align: left; }
  th { background: #f6f8fa; }
  blockquote { border-left: 4px solid #d1d9e0; margin: 0; padding: 0 1rem; color: #59636e; }
  img { max-width: 100%; }
</style>
</head>
<body>
${BODY}
</body>
</html>
HTML

echo "Wrote README.html"
