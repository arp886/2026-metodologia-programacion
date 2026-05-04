# 2026-metodologia-programacion
Recursos para la asignatura de Metodología de la Pgramación 2026 UAL

Conversión de markdown (`*.md`) con diagramas Mermaid a PDF:

```shell
$ pandoc ${FIC}.md \
  --from=gfm --to=pdf \
  --pdf-engine=xelatex \
  --standalone \
  --filter mermaid-filter \
  -o ${FIC}.pdf
```
