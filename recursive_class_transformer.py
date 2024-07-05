import os
import subprocess
import argparse
from concurrent.futures import ThreadPoolExecutor, as_completed
import threading

def extract_dependencies(class_file):
    try:
        output = subprocess.check_output(['javap', '-v', class_file], stderr=subprocess.STDOUT, universal_newlines=True)
        dependencies = set()
        for line in output.split('\n'):
            if 'Class' in line and '=' in line:
                parts = line.split()
                if len(parts) >= 3:
                    class_name = parts[-1].strip()
                    if class_name.startswith('"'):
                        class_name = class_name.strip('"')
                    if '/' in class_name:
                        dep = class_name + '.class'
                        dependencies.add(dep)
        return dependencies
    except subprocess.CalledProcessError:
        print(f"Error processing {class_file}")
        return set()

def transform_class(path_to_class_file: str, transformer_jar: str, output_dir: str, class_files_dir: str) -> None:
    class_name = os.path.splitext(os.path.basename(path_to_class_file))[0]
    package = os.path.dirname(os.path.relpath(path_to_class_file, start=class_files_dir)).replace(os.path.sep, '.')
    full_class_name = f"{package}.{class_name}" if package else class_name
    
    output_path = os.path.join(output_dir, os.path.relpath(path_to_class_file, start=class_files_dir))
    os.makedirs(os.path.dirname(output_path), exist_ok=True)

    cmd = ['java', '-jar', transformer_jar, full_class_name, path_to_class_file, output_path]
    subprocess.run(cmd)
    print(f"Transformed {path_to_class_file} into {output_path}")

def main(start_class: str, transformer_jar: str, input_dir: str, output_dir: str, max_workers: int) -> None:
    processed = set()
    to_process = [start_class]
    lock = threading.Lock()

    def worker(current):
        nonlocal processed, to_process
        
        transform_class(current, transformer_jar, output_dir, input_dir)
        
        deps = extract_dependencies(current)
        new_deps = []
        for dep in deps:
            dep_path = os.path.join(input_dir, dep)
            if os.path.exists(dep_path):
                with lock:
                    if dep_path not in processed:
                        new_deps.append(dep_path)
        
        with lock:
            processed.add(current)
            to_process.extend(new_deps)

    with ThreadPoolExecutor(max_workers=max_workers) as executor:
        while to_process:
            futures = []
            with lock:
                while to_process and len(futures) < max_workers:
                    current = to_process.pop(0)
                    if current not in processed:
                        futures.append(executor.submit(worker, current))
            
            for future in as_completed(futures):
                future.result()  # This will raise an exception if the worker failed

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Recursively transform Java class files.")
    parser.add_argument("start_class", help="Path to the starting class file (e.g., PrintReads.class)")
    parser.add_argument("transformer_jar", help="Path to the MemoryAccessTransformer JAR file")
    parser.add_argument("input_dir", help="Directory storing the pre-transformed class files")
    parser.add_argument("output_dir", help="Directory to store transformed class files")
    parser.add_argument("--max_workers", type=int, default=4, help="Maximum number of worker threads")
    
    args = parser.parse_args()

    main(args.start_class, args.transformer_jar, args.input_dir, args.output_dir, args.max_workers)