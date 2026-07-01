$mappings = @{
    "Branch" = "organization"
    "Department" = "organization"
    "Enquiry" = "enquiry"
    "Inventory" = "inventory"
    "Parts" = "inventory"
    "Status" = "organization"
    "TicketAttachment" = "ticket"
    "Ticket" = "ticket"
    "TicketLogs" = "ticket"
    "TicketType" = "ticket"
    "Billing" = "billing"
    "ChargeType" = "billing"
    "Device" = "device"
    "DeviceModel" = "device"
    "Employee" = "user"
    "UserMaster" = "user"
}

$baseDir = "d:\Mays\MAYS-SRM-BE\src\main\java\com\mays\srm"
$daoPaths = @(
    "$baseDir\dao\core",
    "$baseDir\dao\custom",
    "$baseDir\dao\customImpl"
)

# 1. Move files and update their own package declarations
foreach ($daoPath in $daoPaths) {
    if (Test-Path $daoPath) {
        $files = Get-ChildItem -Path $daoPath -Filter *.java
        foreach ($file in $files) {
            # Extract base entity name (e.g., BillingDaoCustomImpl -> Billing)
            $entityName = $file.Name -replace "Dao.*",""
            
            if ($mappings.ContainsKey($entityName)) {
                $domain = $mappings[$entityName]
                $targetDir = "$baseDir\$domain\repository"
                
                # Create target dir if not exists
                if (-not (Test-Path $targetDir)) {
                    New-Item -ItemType Directory -Force -Path $targetDir | Out-Null
                }
                
                $targetPath = "$targetDir\$($file.Name)"
                
                # Update package declaration in the file itself before moving
                $content = Get-Content $file.FullName -Raw
                $newPackage = "package com.mays.srm.$domain.repository;"
                $content = $content -replace "package com\.mays\.srm\.dao\.(core|custom|customImpl);", $newPackage
                
                Set-Content -Path $file.FullName -Value $content
                
                # Move the file
                Move-Item -Path $file.FullName -Destination $targetPath -Force
                
                Write-Host "Moved $($file.Name) to $domain/repository"
            } else {
                Write-Host "Unknown domain for $($file.Name)"
            }
        }
    }
}

# 2. Global Search and Replace for Imports
$allJavaFiles = Get-ChildItem -Path $baseDir -Recurse -Filter *.java

foreach ($javaFile in $allJavaFiles) {
    $content = Get-Content $javaFile.FullName -Raw
    $originalContent = $content
    
    foreach ($key in $mappings.Keys) {
        $domain = $mappings[$key]
        $content = $content -replace "import com\.mays\.srm\.dao\.core\.$($key)Dao;", "import com.mays.srm.$domain.repository.$($key)Dao;"
        $content = $content -replace "import com\.mays\.srm\.dao\.custom\.$($key)DaoCustom;", "import com.mays.srm.$domain.repository.$($key)DaoCustom;"
        $content = $content -replace "import com\.mays\.srm\.dao\.customImpl\.$($key)DaoCustomImpl;", "import com.mays.srm.$domain.repository.$($key)DaoCustomImpl;"
    }
    
    # Also replace any wildcard imports just in case
    $content = $content -replace "import com\.mays\.srm\.dao\.core\.\*;", "import com.mays.srm.organization.repository.*; import com.mays.srm.ticket.repository.*; import com.mays.srm.inventory.repository.*;"
    
    if ($content -cne $originalContent) {
        Set-Content -Path $javaFile.FullName -Value $content
        Write-Host "Updated imports in $($javaFile.Name)"
    }
}

# 3. Check if dao dir is empty, if so remove it
$remaining = Get-ChildItem -Path "$baseDir\dao" -Recurse -File
if ($remaining.Count -eq 0) {
    Remove-Item -Path "$baseDir\dao" -Recurse -Force
    Write-Host "Removed empty dao directory."
} else {
    Write-Host "dao directory not empty, leaving it."
}
