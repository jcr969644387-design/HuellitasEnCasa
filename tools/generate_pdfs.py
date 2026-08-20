#!/usr/bin/env python3
"""
tools/generate_pdfs.py

Convierte los 3 manuales Markdown principales de docs/ en PDF reales usando
markdown -> HTML -> xhtml2pdf. No usa mocks ni HTML renombrado: produce bytes
%PDF reales, verificables con pypdf.

Uso:
    python3 tools/generate_pdfs.py
"""
import os
import markdown
from xhtml2pdf import pisa

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
DOCS = os.path.join(ROOT, "docs")
OUT = os.path.join(DOCS, "pdf")

CSS = """
<style>
    @page { size: A4; margin: 2.2cm 1.8cm; }
    body { font-family: Helvetica, sans-serif; font-size: 10.5pt; line-height: 1.45; color: #2E241D; }
    h1 { font-size: 20pt; color: #E06A3C; border-bottom: 2pt solid #FF8A5B; padding-bottom: 6pt; margin-top: 0; }
    h2 { font-size: 15pt; color: #4FB0A5; margin-top: 18pt; }
    h3 { font-size: 12.5pt; color: #6B4A34; margin-top: 12pt; }
    table { border-collapse: collapse; width: 100%; margin: 8pt 0; }
    th { background-color: #FFE8D6; padding: 5pt; border: 0.75pt solid #D9BFA6; font-size: 9.5pt; text-align: left; }
    td { padding: 5pt; border: 0.75pt solid #E5DDD0; font-size: 9.5pt; }
    code { background-color: #F1EDFA; padding: 1pt 3pt; font-family: Courier; font-size: 9pt; }
    pre { background-color: #F5F2EC; padding: 6pt; font-family: Courier; font-size: 8.5pt; }
    ul, ol { margin: 4pt 0 8pt 0; }
    li { margin-bottom: 2pt; }
    hr { border: 0.5pt solid #E5DDD0; }
</style>
"""

FILES = [
    ("MEMORIA_DESCRIPTIVA.md", "MEMORIA_DESCRIPTIVA.pdf"),
    ("MANUAL_USUARIO.md", "MANUAL_USUARIO.pdf"),
    ("MANUAL_TECNICO.md", "MANUAL_TECNICO.pdf"),
]


def convert(md_path, pdf_path):
    with open(md_path, "r", encoding="utf-8") as f:
        md_text = f.read()
    html_body = markdown.markdown(md_text, extensions=["tables", "fenced_code", "toc"])
    full_html = f"<html><head><meta charset='utf-8'/>{CSS}</head><body>{html_body}</body></html>"
    with open(pdf_path, "wb") as out_file:
        result = pisa.CreatePDF(src=full_html, dest=out_file, encoding="utf-8")
    if result.err:
        raise RuntimeError(f"xhtml2pdf reportó {result.err} error(es) al convertir {md_path}")
    print(f"OK: {os.path.basename(md_path)} -> {pdf_path}")


def main():
    os.makedirs(OUT, exist_ok=True)
    for md_name, pdf_name in FILES:
        convert(os.path.join(DOCS, md_name), os.path.join(OUT, pdf_name))


if __name__ == "__main__":
    main()
