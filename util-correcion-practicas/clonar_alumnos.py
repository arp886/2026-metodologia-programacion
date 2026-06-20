#!/usr/bin/env python3
import argparse
import json
import os
import re
import subprocess
import unicodedata
from pathlib import Path

from openpyxl import load_workbook


def remove_accents(value):
    normalized = unicodedata.normalize("NFD", value)
    return "".join(char for char in normalized if unicodedata.category(char) != "Mn")


def format_name_part(value):
    value = remove_accents(value)
    value = re.sub(r"[^A-Za-z ]", " ", value)
    return "".join(part.capitalize() for part in value.split())


def split_full_name(full_name):
    parts = str(full_name).strip().split()
    if len(parts) < 2:
        raise ValueError(f"Nombre incompleto: {full_name!r}")

    surnames = parts[:2]
    names = parts[2:] or parts[1:]
    return surnames, names


def repo_name_from_student(full_name, suffix):
    surnames, names = split_full_name(full_name)
    return f"{format_name_part(' '.join(surnames))}{format_name_part(' '.join(names))}{suffix}"


def parse_grade(value):
    if value is None:
        return None
    if isinstance(value, (int, float)):
        return float(value)

    text = str(value).strip().replace(",", ".")
    match = re.search(r"-?\d+(?:\.\d+)?", text)
    return float(match.group(0)) if match else None


def approved_students(workbook_path, sheet_name):
    workbook = load_workbook(workbook_path, data_only=True, read_only=True)
    if sheet_name not in workbook.sheetnames:
        available = ", ".join(workbook.sheetnames)
        raise ValueError(f"No existe la hoja {sheet_name!r}. Hojas disponibles: {available}")

    sheet = workbook[sheet_name]
    for row in sheet.iter_rows(min_row=1):
        full_name = row[1].value if len(row) > 1 else None
        grade = parse_grade(row[4].value if len(row) > 4 else None)
        if full_name and grade is not None and grade > 5:
            yield str(full_name).strip(), grade


def authenticated_url(base_url, username, token, repo_name):
    base_url = base_url.rstrip("/")
    if token:
        return base_url.replace("https://", f"https://{username}:{token}@", 1) + f"/{repo_name}.git"
    return f"{base_url}/{repo_name}.git"


def public_url(base_url, repo_name):
    return f"{base_url.rstrip('/')}/{repo_name}"


def main():
    parser = argparse.ArgumentParser(description="Clona repositorios de alumnos aprobados de MP2026.")
    parser.add_argument("--config", default="config.json", help="Ruta al fichero de configuracion")
    parser.add_argument("--dry-run", action="store_true", help="Muestra lo que haria sin clonar")
    args = parser.parse_args()

    root = Path(__file__).resolve().parent
    config_path = (root / args.config).resolve()
    config = json.loads(config_path.read_text(encoding="utf-8"))

    github = config["github"]
    excel = config["excel"]
    clone = config["clone"]

    token = os.environ.get("GITHUB_TOKEN", github.get("token", ""))
    destination = (root / clone.get("destination", "alumnos")).resolve()
    destination.mkdir(parents=True, exist_ok=True)

    workbook_path = root / excel["file"]
    suffix = clone.get("suffix", "MP2026")

    for full_name, grade in approved_students(workbook_path, excel["sheet"]):
        repo_name = repo_name_from_student(full_name, suffix)
        repo_dir = destination / repo_name
        repo_url = authenticated_url(github["organization_unit"], github["username"], token, repo_name)
        visible_url = public_url(github["organization_unit"], repo_name)

        if repo_dir.exists():
            print(f"Saltando {full_name} ({grade}): ya existe {repo_dir}")
            continue

        print(f"Clonando {full_name} ({grade}): {visible_url}")
        if not args.dry_run:
            subprocess.run(["git", "clone", repo_url, str(repo_dir)], check=True)


if __name__ == "__main__":
    main()
