#bin/sh

/usr/bin/nohup /usr/bin/java -jar /srv/demo-0.0.1-SNAPSHOT.jar & 2>&1
echo `ps aux | grep java | grep -v grep | awk '{print $2}'` > /srv/javapid