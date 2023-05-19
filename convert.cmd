@echo off
path|find /i "e:\jdk1.8\bin\"    >nul || set path=%path%;"e:\jdk1.8\bin\"
java -jar CSVConvert.jar %*