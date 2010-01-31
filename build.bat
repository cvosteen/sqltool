del SQLTool.jar

del *.class
cd database
del *.class
cd ..\gui
del *.class
cd components
del *.class
cd ..\..\task
del *.class
cd ..\tasks
del *.class
cd ..

javac SQLTool.java
@if errorlevel 1 goto :eof

jar cvfm SQLTool.jar manifest.mf *.class license.html database\*.class gui\*.class gui\components\*.class task\*.class tasks\*.class

del *.class
cd database
del *.class
cd ..\gui
del *.class
cd components
del *.class
cd ..\..\task
del *.class
cd ..\tasks
del *.class
cd ..

