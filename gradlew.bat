@echo off
set DIR=%~dp0
if exist "%DIR%gradle\wrapper\gradle-wrapper.jar" (
  java -classpath "%DIR%gradle\wrapper\gradle-wrapper.jar" org.gradle.wrapper.GradleWrapperMain %*
) else (
  where gradle >nul 2>nul && (gradle %*) || (echo gradle-wrapper.jar missing and gradle not installed. Install Gradle 9.2+ and run: gradle wrapper & exit /b 1)
)
