import os
import subprocess
import argparse

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

def transform_class(class_file, transformer_jar, output_dir, class_dir):
    class_name = os.path.splitext(os.path.basename(class_file))[0]
    package = os.path.dirname(os.path.relpath(class_file, start=class_dir)).replace(os.path.sep, '.')
    full_class_name = f"{package}.{class_name}" if package else class_name
    
    output_path = os.path.join(output_dir, os.path.relpath(class_file, start=class_dir))
    os.makedirs(os.path.dirname(output_path), exist_ok=True)
    
    cmd = ['java', '-jar', transformer_jar, full_class_name, class_file]
    subprocess.run(cmd)
    print(f"Transformed {class_file}")

def main(start_class: str, transformer_jar: str, input_dir: str, output_dir: str) -> None:
    processed = set()
    to_process = [start_class]
    
    while to_process:
        current = to_process.pop(0)
        if current in processed:
            continue
        
        transform_class(current, transformer_jar, output_dir, input_dir)
        processed.add(current)
        
        deps = extract_dependencies(current)
        for dep in deps:
            dep_path = os.path.join(input_dir, dep)
            if os.path.exists(dep_path) and dep_path not in processed:
                to_process.append(dep_path)

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Recursively transform Java class files.")
    parser.add_argument("start_class", help="Path to the starting class file (e.g., PrintReads.class)")
    parser.add_argument("transformer_jar", help="Path to the MemoryAccessTransformer JAR file")
    parser.add_argument("input_dir", help="Directory storing the pre-transformed class files")
    parser.add_argument("output_dir", help="Directory to store transformed class files")
    
    args = parser.parse_args()

    main(args.start_class, args.transformer_jar, args.input_dir, args.output_dir)