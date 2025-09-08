#!/usr/bin/env python3
"""
Maven POM Dependency Cleaner - Removes unused dependencies automatically
Usage: python pom_cleaner.py
"""

import xml.etree.ElementTree as ET
import subprocess
import re
import sys
import shutil
from pathlib import Path

class MavenTidy:
    def __init__(self, pom_path="pom.xml"):
        self.pom_path = Path(pom_path)
        self.backup_path = Path(f"{pom_path}.backup")
        self.namespace = {"": "http://maven.apache.org/POM/4.0.0"}
        
    def backup_pom(self):
        """Create backup of pom.xml"""
        shutil.copy2(self.pom_path, self.backup_path)
        print(f"📋 Backup created: {self.backup_path}")
        
    def run_dependency_analysis(self):
        """Run Maven dependency analysis and parse results"""
        print("🔍 Analyzing dependencies...")
        try:
            result = subprocess.run(
                ["mvn", "dependency:analyze"],
                capture_output=True,
                text=True,
                check=False
            )
            return self.parse_analysis_output(result.stdout)
        except Exception as e:
            print(f"❌ Error running dependency analysis: {e}")
            return [], []
            
    def parse_analysis_output(self, output):
        """Parse Maven dependency analysis output"""
        unused_deps = []
        used_undeclared = []
        
        lines = output.split('\n')
        in_unused_section = False
        in_used_undeclared_section = False
        
        for line in lines:
            line = line.strip()
            
            if "Unused declared dependencies found:" in line:
                in_unused_section = True
                in_used_undeclared_section = False
                continue
            elif "Used undeclared dependencies found:" in line:
                in_used_undeclared_section = True
                in_unused_section = False
                continue
            elif line.startswith("[INFO]") and not line.startswith("[INFO]    "):
                in_unused_section = False
                in_used_undeclared_section = False
                continue
                
            if in_unused_section and line.startswith("[WARNING]"):
                # Extract dependency coordinates
                dep_match = re.search(r'([^:]+):([^:]+):([^:]+):([^:]+)', line)
                if dep_match:
                    group_id, artifact_id, packaging, version = dep_match.groups()
                    unused_deps.append({
                        'groupId': group_id,
                        'artifactId': artifact_id,
                        'version': version
                    })
                    
            if in_used_undeclared_section and line.startswith("[WARNING]"):
                dep_match = re.search(r'([^:]+):([^:]+):([^:]+):([^:]+)', line)
                if dep_match:
                    group_id, artifact_id, packaging, version = dep_match.groups()
                    used_undeclared.append({
                        'groupId': group_id,
                        'artifactId': artifact_id,
                        'version': version
                    })
                    
        return unused_deps, used_undeclared
        
    def remove_unused_dependencies(self, unused_deps):
        """Remove unused dependencies from pom.xml"""
        if not unused_deps:
            print("✅ No unused dependencies found")
            return
            
        print(f"🗑️  Removing {len(unused_deps)} unused dependencies...")
        
        # Register namespace to avoid prefix issues
        ET.register_namespace('', 'http://maven.apache.org/POM/4.0.0')
        
        tree = ET.parse(self.pom_path)
        root = tree.getroot()
        
        # Find dependencies section
        dependencies = root.find('.//dependencies', self.namespace)
        if dependencies is None:
            print("❌ No dependencies section found")
            return
            
        removed_count = 0
        for dependency in dependencies.findall('dependency', self.namespace):
            group_id_elem = dependency.find('groupId', self.namespace)
            artifact_id_elem = dependency.find('artifactId', self.namespace)
            
            if group_id_elem is not None and artifact_id_elem is not None:
                group_id = group_id_elem.text
                artifact_id = artifact_id_elem.text
                
                # Check if this dependency should be removed
                for unused_dep in unused_deps:
                    if (unused_dep['groupId'] == group_id and 
                        unused_dep['artifactId'] == artifact_id):
                        print(f"  - Removing {group_id}:{artifact_id}")
                        dependencies.remove(dependency)
                        removed_count += 1
                        break
        
        if removed_count > 0:
            # Write back to file with proper formatting
            self.write_formatted_xml(tree, self.pom_path)
            print(f"✅ Removed {removed_count} unused dependencies")
        
    def write_formatted_xml(self, tree, file_path):
        """Write XML with proper formatting"""
        # Convert to string first
        xml_str = ET.tostring(tree.getroot(), encoding='unicode')
        
        # Add XML declaration
        xml_str = '<?xml version="1.0" encoding="UTF-8"?>\n' + xml_str
        
        # Write to file
        with open(file_path, 'w', encoding='utf-8') as f:
            f.write(xml_str)
    
    def update_dependencies(self):
        """Update dependencies to latest versions"""
        print("🔄 Updating dependencies to latest versions...")
        try:
            result = subprocess.run(
                ["mvn", "versions:use-latest-releases"],
                check=True
            )
            print("✅ Dependencies updated!")
        except subprocess.CalledProcessError as e:
            print(f"❌ Error updating dependencies: {e}")
            
    def test_compilation(self):
        """Test if project compiles after changes"""
        print("🔨 Testing compilation...")
        try:
            result = subprocess.run(
                ["mvn", "compile"],
                capture_output=True,
                text=True,
                check=True
            )
            print("✅ Compilation successful!")
            return True
        except subprocess.CalledProcessError as e:
            print("❌ Compilation failed!")
            print("Error output:", e.stderr)
            return False
            
    def restore_backup(self):
        """Restore from backup"""
        if self.backup_path.exists():
            shutil.copy2(self.backup_path, self.pom_path)
            print(f"🔄 Restored from backup: {self.backup_path}")
        
    def run(self):
        """Run the complete Maven tidy process"""
        print("🚀 Maven Tidy - Starting dependency cleanup")
        print("=" * 50)
        
        # Create backup
        self.backup_pom()
        
        # Analyze dependencies
        unused_deps, used_undeclared = self.run_dependency_analysis()
        
        if unused_deps:
            print(f"\n📦 Found {len(unused_deps)} unused dependencies:")
            for dep in unused_deps:
                print(f"  - {dep['groupId']}:{dep['artifactId']}")
                
            response = input("\n🤔 Remove unused dependencies? (y/N): ").lower()
            if response == 'y':
                self.remove_unused_dependencies(unused_deps)
        
        if used_undeclared:
            print(f"\n⚠️  Found {len(used_undeclared)} used but undeclared dependencies:")
            for dep in used_undeclared:
                print(f"  - {dep['groupId']}:{dep['artifactId']} (should be added)")
        
        # Ask about updates
        response = input("\n🔄 Update dependencies to latest versions? (y/N): ").lower()
        if response == 'y':
            self.update_dependencies()
        
        # Test compilation
        if not self.test_compilation():
            response = input("\n❌ Compilation failed. Restore backup? (Y/n): ").lower()
            if response != 'n':
                self.restore_backup()
                return False
                
        print("\n🎉 Maven Tidy Complete!")
        print(f"📋 Backup available at: {self.backup_path}")
        return True

if __name__ == "__main__":
    tidy = MavenTidy()
    tidy.run()
