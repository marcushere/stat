@rem Do not use "echo off" to not affect any child calls.
@setlocal

@rem Get the abolute path to the current directory, which is assumed to be the
@rem Git installation root.
@for /F "delims=" %%I in ("%~dp0") do @set git_install_root=C:\Tools\git
@set PATH=%git_install_root%\bin;%git_install_root%\mingw\bin;%git_install_root%\cmd;C:\Tools\Dwimperl;C:\Tools\Dwimperl\c;C:\Tools\Dwimperl\c\bin;C:\Tools\Dwimperl\c\i686-w64-mingw32\bin;C:\Tools\Dwimperl\c\i686-w64-mingw32\include;C:\Tools\Dwimperl\c\i686-w64-mingw32\lib;C:\Tools\Dwimperl\c\lib;C:\Tools\Dwimperl\c\lib;C:\Tools\Dwimperl\cpan\build;C:\Tools\Dwimperl\cpan;C:\Tools\Dwimperl\cpan\build\App-cpanminus-1.7001-B33SEu;C:\Tools\Dwimperl\cpan\build\WWW-Mechanize-1.73-7k1QnR;C:\Tools\Dwimperl\perl\bin;C:\Tools\Dwimperl\perl\lib;C:\Tools\Dwimperl\win32;%PATH%

@if not exist "%HOME%" @set HOME=%HOMEDRIVE%%HOMEPATH%
@if not exist "%HOME%" @set HOME=%USERPROFILE%

@set PLINK_PROTOCOL=ssh

@start
