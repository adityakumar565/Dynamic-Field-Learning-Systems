param (
    [string]$agent = "ForgerAgent.ForgerAgent"
)

# Create an output directory for compiled .class files
$outDir = "out"
if (-Not (Test-Path $outDir)) {
    New-Item -ItemType Directory -Path $outDir | Out-Null
}

Write-Host "Compiling project to '$outDir'..." -ForegroundColor Cyan

# Find all .java files in the project
$javaFiles = Get-ChildItem -Path . -Filter *.java -Recurse | Select-Object -ExpandProperty FullName

# Compile all Java files into the out directory
javac -d $outDir $javaFiles

if ($LASTEXITCODE -eq 0) {
    Write-Host "Compilation successful. Running $agent..." -ForegroundColor Green
    java -cp $outDir $agent
} else {
    Write-Host "Compilation failed." -ForegroundColor Red
}
