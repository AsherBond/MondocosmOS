@echo off

set script_directory=%~dp0
set ant_home=apache-ant-1.8.2


REM detect 32 or 64 bits
set BITS_32_OR_64=64
if "%PROCESSOR_ARCHITECTURE%" == "x86" set BITS_32_OR_64=32

set PLATFORM=windows

echo Detected %PLATFORM% %BITS_32_OR_64%

if not "%TOOLS_JDK_HOME%" == "" goto else
   set pvt_tools_jdk_home=%script_directory%..\..\..\IGO-GIT-Repository\tools_jdk_1.6.26\

   if not exist %pvt_tools_jdk_home% goto no_tools_jdk_home

   echo - Auto set tools_jdk_home to "%pvt_tools_jdk_home%"

   goto endif
:else
   echo - Setting tools_jdk_home from given environment variable to "%TOOLS_JDK_HOME%"
   set pvt_tools_jdk_home=%TOOLS_JDK_HOME%
:endif


set java_home=%pvt_tools_jdk_home%\%PLATFORM%\%BITS_32_OR_64%\
if not exist %java_home% goto no_java_for_platform

set java_cmd=%java_home%bin\java



REM =========================================================================================================
REM Compile igotools

setLocal EnableDelayedExpansion

set CLASSPATH=

pushd %script_directory%%ant_home%\lib\
REM for %%x in (*.jar) do set CLASSPATH=%CLASSPATH%%script_directory%%ant_home%\lib\%%x;
for %%x in (*.jar) do (
    REM set CLASSPATH=%CLASSPATH%%ant_home%\lib\%%x; & echo %CLASSPATH%
    set CLASSPATH=!CLASSPATH!%script_directory%%ant_home%\lib\%%x;
)
popd
set CLASSPATH=!CLASSPATH!


REM needed for eclipse java compiler (jdt)
set CLASSPATH=%CLASSPATH%%script_directory%eclipse_helios\plugins\org.eclipse.jdt.core_3.6.0.v_A58.jar;
set CLASSPATH=%CLASSPATH%%script_directory%eclipse_helios\jdtCompilerAdapter.jar;

echo:
pushd %script_directory%
%java_cmd% -Dant.home=%script_directory%%ant_home% org.apache.tools.ant.launch.Launcher -logger org.apache.tools.ant.NoBannerLogger dist
popd
REM =========================================================================================================


REM =========================================================================================================
REM Run IGOTools

echo:

set CLASSPATH=%script_directory%_build\igotools.jar
set tools_jdk_home_property=
if not "%TOOLS_JDK_HOME%" == "" set tools_jdk_home_property= -Dtools.jdk.home=%TOOLS_JDK_HOME% 

%java_cmd% -Xmx1G %tools_jdk_home_property% -Dtemplates.directory=%script_directory%templates -Dtools.eclipse.jdt.jar=%script_directory%eclipse_helios\plugins\org.eclipse.jdt.core_3.6.0.v_A58.jar -Dtools.jdt.adapter.jar=%script_directory%eclipse_helios\jdtCompilerAdapter.jar -Dant.home=%script_directory%%ant_home% -Digotools.home=%script_directory% es.igosoftware.tools.TBuild %target_project%

REM =========================================================================================================


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
set target_project=
set ant_home=
set pvt_tools_jdk_home=
set BITS_32_OR_64=
set PLATFORM=
set java_home=
set java_cmd=
set CLASSPATH=
