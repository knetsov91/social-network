// Reads HTML from stdin, adds an id to every h1-h3 matching GitHub's heading-slug
// algorithm, and writes the result to stdout. Needed because the ToC in README.md
// links to #anchors that only work if the generated headings actually carry those ids.
const html = require("fs").readFileSync(0, "utf8");

function decodeEntities(text) {
  return text
    .replace(/&amp;/g, "&")
    .replace(/&lt;/g, "<")
    .replace(/&gt;/g, ">")
    .replace(/&quot;/g, '"')
    .replace(/&#39;/g, "'");
}

const seen = new Map();
function slugify(text) {
  const base = decodeEntities(text)
    .toLowerCase()
    .replace(/[^\w\- ]/g, "")
    .replace(/ /g, "-");
  const count = seen.get(base) || 0;
  seen.set(base, count + 1);
  return count === 0 ? base : `${base}-${count}`;
}

const withIds = html.replace(/<(h[1-3])>(.*?)<\/\1>/g, (match, tag, inner) => {
  const id = slugify(inner.replace(/<[^>]+>/g, ""));
  return `<${tag} id="${id}">${inner}</${tag}>`;
});

process.stdout.write(withIds);
