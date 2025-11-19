#!/usr/bin/env python3
"""
Branch Cleanup Script for Oja Repository using GitHub API

This script deletes all branches except 'main' and the current working branch
using the GitHub REST API.

Prerequisites:
- Python 3.6+
- A GitHub Personal Access Token with 'repo' scope
- Set the token as GITHUB_TOKEN environment variable or pass it as an argument

Usage:
    # With environment variable
    export GITHUB_TOKEN=your_token_here
    python3 delete_branches_api.py [--dry-run]
    
    # Or pass token as argument
    python3 delete_branches_api.py --token your_token_here [--dry-run]
"""

import os
import sys
import argparse
import json

try:
    import urllib.request
    import urllib.error
except ImportError:
    print("Error: This script requires Python 3")
    sys.exit(1)

# Repository information
REPO_OWNER = "okwunduevans-cyber"
REPO_NAME = "Oja"

# Branches to delete (verified to have no unique commits not in main)
BRANCHES_TO_DELETE = [
    "codex/add-baseline-resources-and-restore-main-files",
    "codex/add-missing-imports-to-screens",
    "codex/add-missing-lazy-imports-in-screens",
    "codex/add-ojaapp-class-and-compose-implementation",
    "codex/add-tools-namespace-to-androidmanifest",
    "codex/create-actionable-solutions-from-new-plan",
    "codex/create-actionable-solutions-from-new-plan-h0auyj",
    "codex/create-data-models-and-repository",
    "codex/create-fix-tasks-for-missing-files",
    "codex/create-new-android-compose-project-oja",
    "codex/implement-material-3-scaffold-and-screens",
    "codex/read-repository-files",
    "codex/read-repository-files-723i0z",
    "codex/read-repository-files-7jhnz5",
    "codex/read-repository-files-diyyst",
    "codex/read-repository-files-jjhdks",
    "codex/read-repository-files-su9hm0",
    "codex/refactor-data-and-network-package-structure",
    "codex/rename-destructured-variable-in-cartscreen",
    "codex/rename-variable-in-cartscreen-and-verify",
    "codex/update-android-gradle-plugin-version",
    "codex/update-android-gradle-plugin-version-m31gb6",
    "codex/update-composeoptions-for-kotlin-2.0.20",
    "codex/update-kotlincompilerextensionversion",
    "codex/update-kotlincompilerextensionversion-in-build.gradle.kts",
    "codex/update-kotlincompilerextensionversion-qc4l1o",
    "codex/update-readme-for-latest-commits",
    "codex/update-screens-to-import-items",
    "copilot/sub-pr-1",
    "copilot/sub-pr-1-again",
    "copilot/sub-pr-19",
    "copilot/sub-pr-19-again",
    "copilot/sub-pr-3",
    "copilot/sub-pr-5",
    "copilot/sub-pr-6",
    "copilot/sub-pr-7",
]


def delete_branch(token, branch_name, dry_run=False):
    """Delete a branch using GitHub API."""
    url = f"https://api.github.com/repos/{REPO_OWNER}/{REPO_NAME}/git/refs/heads/{branch_name}"
    
    if dry_run:
        print(f"[DRY-RUN] Would delete branch: {branch_name}")
        return True, "dry-run"
    
    request = urllib.request.Request(url, method='DELETE')
    request.add_header('Authorization', f'token {token}')
    request.add_header('Accept', 'application/vnd.github.v3+json')
    
    try:
        with urllib.request.urlopen(request) as response:
            if response.status == 204:
                return True, "deleted"
            else:
                return False, f"unexpected status: {response.status}"
    except urllib.error.HTTPError as e:
        if e.code == 404:
            return False, "not found"
        elif e.code == 422:
            return False, "cannot delete (may be default branch or protected)"
        else:
            error_body = e.read().decode('utf-8') if e.fp else ''
            return False, f"HTTP {e.code}: {error_body}"
    except Exception as e:
        return False, str(e)


def main():
    parser = argparse.ArgumentParser(
        description='Delete branches from GitHub repository using API'
    )
    parser.add_argument(
        '--token',
        help='GitHub Personal Access Token (or use GITHUB_TOKEN env var)'
    )
    parser.add_argument(
        '--dry-run',
        action='store_true',
        help='Show what would be deleted without actually deleting'
    )
    args = parser.parse_args()
    
    # Get token from argument or environment
    token = args.token or os.environ.get('GITHUB_TOKEN')
    if not token:
        print("Error: GitHub token is required.")
        print("Set GITHUB_TOKEN environment variable or use --token argument")
        sys.exit(1)
    
    print("Branch Cleanup Script (GitHub API)")
    print("=" * 50)
    print()
    print(f"Repository: {REPO_OWNER}/{REPO_NAME}")
    print(f"Branches to delete: {len(BRANCHES_TO_DELETE)}")
    print()
    
    if args.dry_run:
        print("*** DRY-RUN MODE - No branches will be deleted ***")
        print()
    else:
        print("WARNING: This action cannot be undone!")
        response = input("Do you want to proceed? (yes/no): ")
        if response.lower() != 'yes':
            print("Operation cancelled.")
            sys.exit(0)
        print()
    
    # Statistics
    success_count = 0
    fail_count = 0
    not_found_count = 0
    
    print("Starting branch deletion...")
    print()
    
    for branch in BRANCHES_TO_DELETE:
        print(f"Processing: {branch} ... ", end='', flush=True)
        success, message = delete_branch(token, branch, args.dry_run)
        
        if success:
            print(f"✓ {message}")
            success_count += 1
        else:
            if message == "not found":
                print(f"⊘ {message}")
                not_found_count += 1
            else:
                print(f"✗ {message}")
                fail_count += 1
    
    print()
    print("=" * 50)
    print("Branch deletion complete!")
    print()
    print("Statistics:")
    print(f"  - Successfully deleted: {success_count}")
    print(f"  - Failed: {fail_count}")
    print(f"  - Not found: {not_found_count}")
    print(f"  - Total: {len(BRANCHES_TO_DELETE)}")
    print()
    
    if args.dry_run:
        print("This was a dry-run. No branches were actually deleted.")
        print("Run without --dry-run to perform actual deletion")
    
    sys.exit(0 if fail_count == 0 else 1)


if __name__ == '__main__':
    main()
