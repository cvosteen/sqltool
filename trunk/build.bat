del SQLTool.jar

cd cvosteen\sqltool
del *.class
cd database
del *.class
cd ..\memory
del *.class
cd ..\gui
del *.class
cd components
del *.class
cd ..\syntax
del *.class
cd ..\..\task
del *.class
cd ..\tasks
del *.class
cd ..\..\..

javac -source 1.6 -target 1.6 -bootclasspath "C:\Program Files (x86)\Java\jre6\lib\rt.jar" cvosteen\sqltool\SQLTool.java
@if errorlevel 1 goto :eof

jar cvfm SQLTool.jar manifest.mf cvosteen\sqltool\*.class cvosteen\sqltool\license.html cvosteen\sqltool\database\*.class cvosteen\sqltool\memory\*.class cvosteen\sqltool\gui\*.class cvosteen\sqltool\gui\components\*.class cvosteen\sqltool\gui\syntax\*.class cvosteen\sqltool\gui\icons\*.png cvosteen\sqltool\task\*.class cvosteen\sqltool\tasks\*.class

cd cvosteen\sqltool
del *.class
cd database
del *.class
cd ..\memory
del *.class
cd ..\gui
del *.class
cd components
del *.class
cd ..\syntax
del *.class
cd ..\..\task
del *.class
cd ..\tasks
del *.class
cd ..\..\..

