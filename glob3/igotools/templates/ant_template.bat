@echo off

set script_directory=%~dp0

REM detect 32 or 64 bits
set BITS_32_OR_64=64
if "%PROCESSOR_ARCHITECTURE%" == "x86" set BITS_32_OR_64=32

set PLATFORM=windows

REM echo Detected %PLATFORM% %BITS_32_OR_64%


if not "%TOOLS_JDK_HOME%" == "" goto else
   set pvt_tools_jdk_home=%script_directory%..\..\..\IGO-GIT-Repository\tools_jdk_1.6.26\
   if exist %pvt_tools_jdk_home% goto found

   set pvt_tools_jdk_home=%script_directory%..\..\IGO-GIT-Repository\tools_jdk_1.6.26\
   if exist %pvt_tools_jdk_home% goto found

   goto no_tools_jdk_home

:found
   echo - Auto set tools_jdk_home to "%pvt_tools_jdk_home%"

   goto endif
:else
   echo - Setting tools_jdk_home from given environment variable to "%TOOLS_JDK_HOME%"
   set pvt_tools_jdk_home=%TOOLS_JDK_HOME%
:endif


set java_home=%pvt_tools_jdk_home%\%PLATFORM%\%BITS_32_OR_64%\
if not exist %java_home% goto no_java_for_platform

set java_cmd=%java_home%bin\java


set CLASSPATH="%CLASSPATH%"
set ant_home="%ant_home%"

%java_cmd% -Dant.home=%ant_home% org.apache.tools.ant.launch.Launcher -logger org.apache.tools.ant.NoBannerLogger -buildfile generated_build.xml %1 %2 %3 %4 %5 %6 %7 %8 %9

goto end


:no_tools_jdk_home
echo:
echo ===============================================================================
echo ERROR: Can't find %pvt_tools_jdk_home%
echo ===============================================================================
exit /B 2
goto end


:no_java_for_platform
echo:
echo ===============================================================================
echo ERROR: Can't find java for %PLATFORM%\%BITS_32_OR_64% in %java_home%
echo ===============================================================================
exit /B 3
goto end



:end
set script_directory=
set PLATFORM=
set ant_home=
set CLASSPATH=
