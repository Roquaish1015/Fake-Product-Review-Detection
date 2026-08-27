Write-Host "===================================================" -ForegroundColor Cyan
Write-Host "  Starting REVIEW//SENTINEL Fake Review System" -ForegroundColor Cyan
Write-Host "===================================================" -ForegroundColor Cyan
Write-Host ""

$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$mvnwPath = Join-Path $ScriptDir "mvnw.cmd"

if (Test-Path $mvnwPath) {
    Write-Host "[INFO] Launching using Maven Wrapper..." -ForegroundColor Green
    & $mvnwPath clean spring-boot:run
} elseif (Get-Command mvn -ErrorAction SilentlyContinue) {
    Write-Host "[INFO] Launching using installed Maven..." -ForegroundColor Green
    mvn clean spring-boot:run
} else {
    $vscodeMvn = Get-ChildItem -Path "$env:USERPROFILE\.vscode\extensions" -Recurse -Filter "mvn.cmd" -ErrorAction SilentlyContinue | Select-Object -ExpandProperty FullName -First 1
    if ($vscodeMvn -and (Test-Path $vscodeMvn)) {
        Write-Host "[INFO] Launching using local VS Code Maven..." -ForegroundColor Green
        & $vscodeMvn clean spring-boot:run
    } else {
        Write-Host "[ERROR] Maven or Maven Wrapper not found!" -ForegroundColor Red
    }
}
