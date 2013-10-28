@rem Do not use "echo off" to not affect any child calls.
@setlocal

@if not exist "%HOME%" @set HOME=%HOMEDRIVE%%HOMEPATH%
@if not exist "%HOME%" @set HOME=%USERPROFILE%

java -Djava.library.path="C:\Tools\jdbc\sqljdbc_4.0\enu\auth\x64" -jar jar\Congress2012MC.jar %1