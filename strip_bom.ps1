$baseDir = "d:\Mays\MAYS-SRM-BE\src\main\java\com\mays\srm"
$allJavaFiles = Get-ChildItem -Path $baseDir -Filter "*.java" -Recurse

$utf8NoBom = New-Object System.Text.UTF8Encoding $false

foreach ($file in $allJavaFiles) {
    $bytes = [System.IO.File]::ReadAllBytes($file.FullName)
    if ($bytes.Length -ge 3 -and $bytes[0] -eq 0xEF -and $bytes[1] -eq 0xBB -and $bytes[2] -eq 0xBF) {
        $content = [System.IO.File]::ReadAllText($file.FullName)
        [System.IO.File]::WriteAllText($file.FullName, $content, $utf8NoBom)
        Write-Host "Stripped BOM from $($file.Name)"
    }
}
Write-Host "Done"
