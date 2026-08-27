@echo off
echo ===================================================
echo   Starting REVIEW//SENTINEL Fake Review System
echo ===================================================
echo.

set "MVN_CMD="

if exist "%~dp0mvnw.cmd" (
    set "MVN_CMD=%~dp0mvnw.cmd"
) else (
    set "VSCODE_MVN="
    for /f "delims=" %%I in ('dir /b /s "%USERPROFILE%\.vscode\extensions\mvn.cmd" 2^>nul') do (
        set "VSCODE_MVN=%%I"
        goto :found_vscode_mvn
    )
    :found_vscode_mvn
    if defined VSCODE_MVN (
        set "MVN_CMD=%VSCODE_MVN%"
    ) else (
        where mvn >nul 2>nul
        if %ERRORLEVEL% EQU 0 (
            set "MVN_CMD=mvn"
        )
    )
)

if not defined MVN_CMD (
    echo [ERROR] Could not find Maven Wrapper or Maven installation.
    exit /b 1
)

echo [INFO] Using Maven: %MVN_CMD%
echo [INFO] Cleaning and starting Spring Boot Application...
call "%MVN_CMD%" clean spring-boot:run
