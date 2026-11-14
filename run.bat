@echo off
setlocal
chcp 65001 > nul
set JAR=target\gogol-nos-spark-1.0-SNAPSHOT-jar-with-dependencies.jar

java ^
  --add-opens=java.base/java.lang=ALL-UNNAMED ^
  --add-opens=java.base/java.lang.invoke=ALL-UNNAMED ^
  --add-opens=java.base/java.lang.reflect=ALL-UNNAMED ^
  --add-opens=java.base/java.io=ALL-UNNAMED ^
  --add-opens=java.base/java.net=ALL-UNNAMED ^
  --add-opens=java.base/java.nio=ALL-UNNAMED ^
  --add-opens=java.base/java.util=ALL-UNNAMED ^
  --add-opens=java.base/java.util.concurrent=ALL-UNNAMED ^
  --add-opens=java.base/java.util.concurrent.atomic=ALL-UNNAMED ^
  --add-opens=java.base/sun.nio.ch=ALL-UNNAMED ^
  --add-opens=java.base/sun.nio.cs=ALL-UNNAMED ^
  --add-opens=java.base/sun.security.action=ALL-UNNAMED ^
  --add-opens=java.base/sun.util.calendar=ALL-UNNAMED ^
  -Dfile.encoding=UTF-8 ^
  -Dstdout.encoding=UTF-8 ^
  -jar %JAR%

endlocal