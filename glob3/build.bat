@echo off

cls

set script_directory=%~dp0
REM echo script_directory=%script_directory%

set target_project=%1
SHIFT
REM echo target_project=%target_project%

if "%target_project%" == "" goto no_target_project

if not exist %target_project% goto no_directory

if not exist %target_project%/project.properties goto no_project_properties


echo Building %target_project% %1 %2 %3 %4 %5 %6 %7 %8 %9

call %script_directory%igotools/run.bat %target_project%
if ERRORLEVEL 2 goto error_building_tools

echo:
pushd %target_project%
call generated_ant.bat %1 %2 %3 %4 %5 %6 %7 %8 %9
popd

goto end



:no_target_project
echo:
echo ===============================================================================
echo ERROR: Execute the script passsing the project directory as the first argument
echo ===============================================================================
goto end

:no_directory
echo:
echo ===============================================================================
echo ERROR: %target_project% doesn't exist
echo ===============================================================================
goto end

:no_project_properties
echo:
echo ===============================================================================
echo ERROR: %target_project%/project.properties doesn't exist
echo ===============================================================================
goto end

:error_building_tools
echo:
echo ===============================================================================
echo ERROR: Error trying to compile igotools (ERRORLEVEL=%ERRORLEVEL%) 
echo ===============================================================================
goto end



:end

set script_directory=
set target_project=
