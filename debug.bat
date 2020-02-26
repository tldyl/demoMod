@echo off
cd /d E:\Steam\steamapps\common\SlayTheSpire
start java -Xdebug -Xrunjdwp:transport=dt_socket,address=5005,server=y,suspend=y -jar ModTheSpire.jar