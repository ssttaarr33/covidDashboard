#bin/sh

/usr/bin/java -jar /srv/demo-0.0.1-SNAPSHOT.jar > /dev/null 2> /dev/null < /dev/null &
echo `ps aux | grep java | grep -v grep | awk '{print $2}'` > /srv/javapid