param([string]$Jdk='C:\Program Files\Java\jdk-21', [string]$H2Jar="$PSScriptRoot\..\mandarin-demo\build\input\h2.jar", [switch]$Package)
$ErrorActionPreference='Stop'
Set-Location $PSScriptRoot
$buildDir=Join-Path $PSScriptRoot 'build'
New-Item -ItemType Directory -Force "$buildDir\app-classes", "$buildDir\input" | Out-Null
$libs=Join-Path $PSScriptRoot 'original\target\libs'
$sources=Get-ChildItem src/main/java -Recurse -Filter *.java | ForEach-Object { '"'+$_.FullName.Replace('\','/')+'"' }
[IO.File]::WriteAllLines("$buildDir\sources.txt",$sources)
& "$Jdk\bin\javac.exe" -encoding UTF-8 -cp "$libs\*;$H2Jar" -d "$buildDir\app-classes" "@$buildDir\sources.txt"
if($LASTEXITCODE){throw 'Compilation failed'}
Copy-Item -Recurse -Force src/main/resources/* "$buildDir\app-classes"
Copy-Item $H2Jar "$buildDir\input\h2.jar"
Get-ChildItem $libs -Filter *.jar | Where-Object {$_.Name -match '^(javafx-|jfoenix-|fontawesomefx-)'} | Copy-Item -Destination "$buildDir\input"
& "$Jdk\bin\jar.exe" --create --file "$buildDir\input\dha-demo.jar" --main-class main.AppLauncher -C "$buildDir\app-classes" .
if($LASTEXITCODE){throw 'JAR creation failed'}
if($Package){
 $packageDir=Join-Path $buildDir ('package-'+(Get-Date -Format yyyyMMddHHmmss))
 New-Item -ItemType Directory -Force $packageDir | Out-Null
 & "$Jdk\bin\jpackage.exe" --type app-image --name DHAInventoryDemo --input "$buildDir\input" --main-jar dha-demo.jar --main-class main.AppLauncher --dest $packageDir --add-modules java.desktop,java.sql,java.naming,java.management,java.logging,java.xml,jdk.unsupported,jdk.charsets
 if($LASTEXITCODE){throw 'Packaging failed'}
 Copy-Item README.txt "$packageDir\DHAInventoryDemo\README.txt"
 Compress-Archive -Path "$packageDir\DHAInventoryDemo" -DestinationPath "$buildDir\dha-inventory-demo-windows.zip" -Force
 Copy-Item "$buildDir\dha-inventory-demo-windows.zip" '..\..\downloads\dha-inventory-demo-windows.zip'
 Write-Output "$packageDir\DHAInventoryDemo\DHAInventoryDemo.exe"
}
