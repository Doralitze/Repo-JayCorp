#! /bin/sh
$DB_LOCATION="/var/jc/database.xml"
java -jar Server.jar $DB_LOCATION > /var/log/jc/latest.log
