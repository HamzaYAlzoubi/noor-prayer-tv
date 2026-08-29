@rem Minimal wrapper fallback
@if "%DEBUG%"=="" @echo off
@rem Execute gradle if available
where gradle >nul 2>&1
@if %ERRORLEVEL%==0 (
  gradle %*
  exit /b %ERRORLEVEL%
)
@if exist "gradle\wrapper\gradle-wrapper.jar" (
  java -jar "gradle\wrapper\gradle-wrapper.jar" %*
) else (
  echo Gradle not found
  exit /b 1
)
