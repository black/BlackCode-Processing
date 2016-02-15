mkdir ..\bin\org\philhosoft\p8g\svg
cd ..\bin
move /Y ..\src\*.class org\philhosoft\p8g\svg
%JAVA6_HOME%\bin\jar cfvm ..\library\P8gGraphicsSVG.jar ..\src\MANIFEST.MF .
cd ..\src
