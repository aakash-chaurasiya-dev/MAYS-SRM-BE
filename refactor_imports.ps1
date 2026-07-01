$ErrorActionPreference = "Stop"
$baseDir = "d:\Mays\MAYS-SRM-BE\src\main\java\com\mays\srm"

# 1. Build a map of ClassName -> New FQCN
$classMap = @{}

# Get all Java files
$allJavaFiles = Get-ChildItem -Path $baseDir -Filter "*.java" -Recurse

foreach ($file in $allJavaFiles) {
    # Extract relative path from baseDir
    $relPath = $file.FullName.Substring($baseDir.Length + 1)
    $relDir = Split-Path $relPath -Parent
    
    # Calculate new package
    if ($relDir -eq "") {
        $newPackage = "com.mays.srm"
    } else {
        $pkgPath = $relDir -replace '\\', '.'
        $newPackage = "com.mays.srm.$pkgPath"
    }
    
    $className = $file.BaseName
    $fqcn = "$newPackage.$className"
    
    $classMap[$className] = $fqcn
}

Write-Host "Built class map with $($classMap.Count) classes."

# 2. Process each file
foreach ($file in $allJavaFiles) {
    $content = Get-Content $file.FullName -Raw
    $modified = $false

    # A. Update Package Declaration
    $relPath = $file.FullName.Substring($baseDir.Length + 1)
    $relDir = Split-Path $relPath -Parent
    if ($relDir -eq "") {
        $newPackage = "com.mays.srm"
    } else {
        $pkgPath = $relDir -replace '\\', '.'
        $newPackage = "com.mays.srm.$pkgPath"
    }
    
    $newContent = $content -replace '(?m)^package\s+com\.mays\.srm[a-zA-Z0-9_\.]*;\s*$', "package $newPackage;"
    if ($newContent -cne $content) {
        $content = $newContent
        $modified = $true
    }

    # B. Update Imports
    # Look for: import com.mays.srm.anything.ClassName;
    $importRegex = '(?m)^import\s+com\.mays\.srm\.[a-zA-Z0-9_\.]+\.([A-Za-z0-9_]+);'
    
    $matches = [regex]::Matches($content, $importRegex)
    foreach ($m in $matches) {
        $className = $m.Groups[1].Value
        if ($classMap.ContainsKey($className)) {
            $oldImport = $m.Value
            $newImport = "import $($classMap[$className]);"
            if ($oldImport -ne $newImport) {
                $content = $content.Replace($oldImport, $newImport)
                $modified = $true
            }
        }
    }

    # C. Update Constructor Projections (new com.mays.srm.anything.ClassName)
    $projRegex = 'new\s+com\.mays\.srm\.[a-zA-Z0-9_\.]+\.([A-Za-z0-9_]+)\s*\('
    $pMatches = [regex]::Matches($content, $projRegex)
    foreach ($pm in $pMatches) {
        $className = $pm.Groups[1].Value
        if ($classMap.ContainsKey($className)) {
            $oldProj = $pm.Value
            $newProj = "new $($classMap[$className])("
            if ($oldProj -ne $newProj) {
                $content = $content.Replace($oldProj, $newProj)
                $modified = $true
            }
        }
    }

    if ($modified) {
        [System.IO.File]::WriteAllText($file.FullName, $content, [System.Text.Encoding]::UTF8)
        Write-Host "Updated $($file.Name)"
    }
}

Write-Host "Import and package refactoring complete."
