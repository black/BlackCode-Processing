REM Adjust path to your installation directory
set PROCESSING_HOME=C:\Java\Processing-1.5.1
set JAVA6_HOME=C:\Java\jdk1.6.0_30
set P=../library/batik
REM Using Batik 1.8. Minimal set of jar files
%JAVA6_HOME%\bin\javac -cp %P%-dom.jar;%P%-svggen.jar;%P%-awt-util.jar;%PROCESSING_HOME%\lib\core.jar P8gGraphicsSVG.java
