@echo off
echo 快速编译 ruoyi-framework 和 ruoyi-admin...
"D:\JetBrains\IntelliJ IDEA 2024.3.1.1\jbr\bin\java.exe" ^
  -Dmaven.multiModuleProjectDirectory=F:\project\lf\langfang-shebao ^
  -Djansi.passthrough=true ^
  -Dmaven.home=D:\maven\apache-maven-3.8.8 ^
  -Dclassworlds.conf=D:\maven\apache-maven-3.8.8\bin\m2.conf ^
  "-Dmaven.ext.class.path=D:\JetBrains\IntelliJ IDEA 2024.3.1.1\plugins\maven\lib\maven-event-listener.jar" ^
  "-javaagent:D:\JetBrains\IntelliJ IDEA 2024.3.1.1\lib\idea_rt.jar=53949:D:\JetBrains\IntelliJ IDEA 2024.3.1.1\bin" ^
  -Dfile.encoding=UTF-8 ^
  -Dsun.stdout.encoding=UTF-8 ^
  -Dsun.stderr.encoding=UTF-8 ^
  -classpath "D:\maven\apache-maven-3.8.8\boot\plexus-classworlds-2.6.0.jar;D:\maven\apache-maven-3.8.8\boot\plexus-classworlds.license" ^
  org.codehaus.classworlds.Launcher ^
  -Didea.version=2024.3.1.1 ^
  package -pl ruoyi-framework,ruoyi-admin -am -Dmaven.test.skip=true
