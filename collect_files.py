import os
from pathlib import Path

def collect_project_files(root_dir, output_file='combined_project.txt'):
    """
    Собирает все файлы проекта в один файл
    """
    # Расширения файлов для поиска
    extensions = {'.java', '.yaml', '.yml', '.js', '.jsx', '.css', '.sql'}

    # Специфические имена файлов
    special_files = {'Dockerfile', 'nginx.json', 'package.json', 'package-lock.json'}

    # Директории для игнорирования
    ignore_dirs = {'node_modules', '.git', 'target', 'build', 'dist', '__pycache__',
                   '.idea', '.vscode', 'venv', 'env'}

    collected_files = []

    # Рекурсивный обход директорий
    for root, dirs, files in os.walk(root_dir):
        # Удаляем игнорируемые директории из обхода
        dirs[:] = [d for d in dirs if d not in ignore_dirs]

        for file in files:
            file_path = os.path.join(root, file)
            relative_path = os.path.relpath(file_path, root_dir)

            # Проверяем расширение или специфическое имя
            file_ext = Path(file).suffix
            if file_ext in extensions or file in special_files:
                collected_files.append((relative_path, file_path))

    # Сортируем файлы по пути
    collected_files.sort()

    # Записываем в выходной файл
    with open(output_file, 'w', encoding='utf-8') as out:
        out.write(f"=== Проект: {os.path.basename(root_dir)} ===\n")
        out.write(f"Всего файлов: {len(collected_files)}\n")
        out.write("=" * 80 + "\n\n")

        for relative_path, full_path in collected_files:
            out.write("=" * 80 + "\n")
            out.write(f"Файл: {relative_path}\n")
            out.write("=" * 80 + "\n")

            try:
                with open(full_path, 'r', encoding='utf-8') as f:
                    content = f.read()
                    out.write(content)
                    if not content.endswith('\n'):
                        out.write('\n')
            except UnicodeDecodeError:
                # Пробуем другую кодировку
                try:
                    with open(full_path, 'r', encoding='latin-1') as f:
                        content = f.read()
                        out.write(content)
                        if not content.endswith('\n'):
                            out.write('\n')
                except Exception as e:
                    out.write(f"[Ошибка чтения файла: {e}]\n")
            except Exception as e:
                out.write(f"[Ошибка чтения файла: {e}]\n")

            out.write("\n\n")

    print(f"✓ Собрано {len(collected_files)} файлов")
    print(f"✓ Результат сохранён в: {output_file}")

    # Выводим список собранных файлов
    print("\nСобранные файлы:")
    for relative_path, _ in collected_files:
        print(f"  - {relative_path}")

if __name__ == "__main__":
    # Укажите путь к корневой директории проекта
    project_root = "."  # Текущая директория, или укажите полный путь

    # Имя выходного файла
    output_filename = "combined_project.txt"

    collect_project_files(project_root, output_filename)
