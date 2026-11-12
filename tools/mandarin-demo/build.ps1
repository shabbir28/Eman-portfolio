# Rebuild with JDK 21 and an H2 2.3.232 JAR supplied by the developer.
param([Parameter(Mandatory=$true)][string]$H2Jar, [string]$Jdk = 'C:\Program Files\Java\jdk-21')
$ErrorActionPreference = 'Stop'
Set-Location $PSScriptRoot
$buildDir = Join-Path $PSScriptRoot ('build\rebuild-' + (Get-Date -Format 'yyyyMMddHHmmss'))
New-Item -ItemType Directory -Force "$buildDir\classes", "$buildDir\input" | Out-Null
$javaSources = Get-ChildItem src -Filter *.java | ForEach-Object FullName
& "$Jdk\bin\javac.exe" -encoding UTF-8 -d "$buildDir\classes" $javaSources
if ($LASTEXITCODE) { throw 'Compilation failed' }
Copy-Item -Recurse src/Resource "$buildDir\classes\Resource"
Copy-Item -LiteralPath $H2Jar "$buildDir\input\h2.jar"
& "$Jdk\bin\jar.exe" --create --file "$buildDir\input\mandarin-demo.jar" --main-class Main -C "$buildDir\classes" .
& "$Jdk\bin\jpackage.exe" --type app-image --name MandarinInventoryDemo --input "$buildDir\input" --main-jar mandarin-demo.jar --main-class Main --dest "$buildDir\package" --add-modules java.desktop,java.sql,java.naming,java.management,java.logging,java.xml,jdk.unsupported
if ($LASTEXITCODE) { throw 'Packaging failed' }
Copy-Item README.txt "$buildDir\package\MandarinInventoryDemo\README.txt"
Compress-Archive -Path "$buildDir\package\MandarinInventoryDemo" -DestinationPath "$buildDir\mandarin-inventory-demo-windows.zip"
Write-Output "$buildDir\mandarin-inventory-demo-windows.zip"
