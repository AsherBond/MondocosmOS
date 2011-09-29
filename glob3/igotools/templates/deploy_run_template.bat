@echo off

set script_directory=%~dp0

REM detect 32 or 64 bits
set BITS_32_OR_64=64
if "%PROCESSOR_ARCHITECTURE%" == "x86" set BITS_32_OR_64=32

set PLATFORM=windows

REM echo Detected %PLATFORM% %BITS_32_OR_64%

REM set java_cmd=jre\%PLATFORM%\%BITS_32_OR_64%\bin\javaw.exe
set java_cmd=jre\%PLATFORM%\%BITS_32_OR_64%\bin\java.exe


if not "%BITS_32_OR_64%" == "32" goto else
    set JAVA_VM_ARGS=@JAVA_VM_ARGS_windows_32@
    goto endif
:else
    set JAVA_VM_ARGS=@JAVA_VM_ARGS_windows_64@
:endif
echo JAVA_VM_ARGS=%JAVA_VM_ARGS%


set JAVA_VM_JAVA_LIBRARY_PATH=(@JAVA_VM_JAVA_LIBRARY_PATH@)
set java_library_path=
for %%i in %JAVA_VM_JAVA_LIBRARY_PATH% do ( set java_library_path=%java_library_path% -Djava.library.path=libs\native\%%i\%PLATFORM%\%BITS_32_OR_64% )
REM echo java_library_path=%java_library_path%


set JAVA_VM_JNA_LIBRARY_PATH=(@JAVA_VM_JNA_LIBRARY_PATH@)
set jna_library_path=
for %%i in %JAVA_VM_JNA_LIBRARY_PATH% do ( set jna_library_path=%jna_library_path% -Djna.library.path=libs\native\%%i\%PLATFORM%\%BITS_32_OR_64% )
REM echo jna_library_path=%jna_library_path%


set CLASSPATH=@WINDOWS_CLASSPATH@


%java_cmd% -client -showversion -splash:bitmaps\splash.png %java_library_path% %jna_library_path% %JAVA_VM_ARGS% @MAIN_CLASS@ %1 %2 %3 %4 %5 %6 %7 %8 %9
