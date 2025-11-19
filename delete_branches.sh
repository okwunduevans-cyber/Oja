#!/bin/bash

# Branch Cleanup Script for Oja Repository
# This script deletes all branches except 'main' and the current working branch
# 
# Prerequisites:
# - Git must be configured with credentials that have push access to the repository
# - Run this script from the repository root directory
#
# Usage: ./delete_branches.sh [--dry-run]

set -e

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Check if dry-run mode
DRY_RUN=false
if [ "$1" = "--dry-run" ]; then
    DRY_RUN=true
    echo -e "${YELLOW}Running in DRY-RUN mode - no branches will be deleted${NC}"
    echo ""
fi

# Branches to delete (verified to have no unique commits not in main)
BRANCHES_TO_DELETE=(
    "codex/add-baseline-resources-and-restore-main-files"
    "codex/add-missing-imports-to-screens"
    "codex/add-missing-lazy-imports-in-screens"
    "codex/add-ojaapp-class-and-compose-implementation"
    "codex/add-tools-namespace-to-androidmanifest"
    "codex/create-actionable-solutions-from-new-plan"
    "codex/create-actionable-solutions-from-new-plan-h0auyj"
    "codex/create-data-models-and-repository"
    "codex/create-fix-tasks-for-missing-files"
    "codex/create-new-android-compose-project-oja"
    "codex/implement-material-3-scaffold-and-screens"
    "codex/read-repository-files"
    "codex/read-repository-files-723i0z"
    "codex/read-repository-files-7jhnz5"
    "codex/read-repository-files-diyyst"
    "codex/read-repository-files-jjhdks"
    "codex/read-repository-files-su9hm0"
    "codex/refactor-data-and-network-package-structure"
    "codex/rename-destructured-variable-in-cartscreen"
    "codex/rename-variable-in-cartscreen-and-verify"
    "codex/update-android-gradle-plugin-version"
    "codex/update-android-gradle-plugin-version-m31gb6"
    "codex/update-composeoptions-for-kotlin-2.0.20"
    "codex/update-kotlincompilerextensionversion"
    "codex/update-kotlincompilerextensionversion-in-build.gradle.kts"
    "codex/update-kotlincompilerextensionversion-qc4l1o"
    "codex/update-readme-for-latest-commits"
    "codex/update-screens-to-import-items"
    "copilot/sub-pr-1"
    "copilot/sub-pr-1-again"
    "copilot/sub-pr-19"
    "copilot/sub-pr-19-again"
    "copilot/sub-pr-3"
    "copilot/sub-pr-5"
    "copilot/sub-pr-6"
    "copilot/sub-pr-7"
)

echo -e "${GREEN}Branch Cleanup Script${NC}"
echo "====================="
echo ""
echo "This script will delete ${#BRANCHES_TO_DELETE[@]} branches from the remote repository."
echo "The following branches will be preserved:"
echo "  - main (the default branch)"
echo "  - copilot/delete-other-branches (current working branch)"
echo ""

if [ "$DRY_RUN" = false ]; then
    echo -e "${YELLOW}WARNING: This action cannot be undone!${NC}"
    echo -n "Do you want to proceed? (yes/no): "
    read -r confirmation
    echo ""
    
    if [ "$confirmation" != "yes" ]; then
        echo "Operation cancelled."
        exit 0
    fi
fi

# Counter for statistics
SUCCESS_COUNT=0
FAIL_COUNT=0
SKIPPED_COUNT=0

echo "Starting branch deletion..."
echo ""

for branch in "${BRANCHES_TO_DELETE[@]}"; do
    # Check if branch exists
    if ! git ls-remote --heads origin "$branch" | grep -q "$branch"; then
        echo -e "${YELLOW}⊘ Branch '$branch' does not exist (already deleted?)${NC}"
        SKIPPED_COUNT=$((SKIPPED_COUNT + 1))
        continue
    fi
    
    if [ "$DRY_RUN" = true ]; then
        echo -e "${YELLOW}[DRY-RUN]${NC} Would delete remote branch: $branch"
        SUCCESS_COUNT=$((SUCCESS_COUNT + 1))
    else
        echo -n "Deleting remote branch: $branch ... "
        if git push origin --delete "$branch" 2>&1; then
            echo -e "${GREEN}✓ Done${NC}"
            SUCCESS_COUNT=$((SUCCESS_COUNT + 1))
        else
            echo -e "${RED}✗ Failed${NC}"
            FAIL_COUNT=$((FAIL_COUNT + 1))
        fi
    fi
done

echo ""
echo "====================="
echo -e "${GREEN}Branch deletion complete!${NC}"
echo ""
echo "Statistics:"
echo "  - Successfully deleted: $SUCCESS_COUNT"
echo "  - Failed: $FAIL_COUNT"
echo "  - Skipped (not found): $SKIPPED_COUNT"
echo "  - Total: ${#BRANCHES_TO_DELETE[@]}"
echo ""

if [ "$DRY_RUN" = true ]; then
    echo -e "${YELLOW}This was a dry-run. No branches were actually deleted.${NC}"
    echo "Run without --dry-run to perform actual deletion:"
    echo "  ./delete_branches.sh"
fi

if [ $FAIL_COUNT -gt 0 ]; then
    exit 1
fi

exit 0
