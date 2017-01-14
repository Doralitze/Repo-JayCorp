#! /bin/bash
DB_FOLDER="/var/jc/"
DB_LOCATION=$DB_FOLDER"database.xml"
echo backing up database @ $DB_LOCATION
rm -f $DB_FOLDER"database_backup3.xml"
mv -f $DB_FOLDER"database_backup2.xml" $DB_FOLDER"database_backup3.xml"
mv -f $DB_FOLDER"database_backup1.xml" $DB_FOLDER"database_backup2.xml"
mv -f $DB_FOLDER"database_backup.xml" $DB_FOLDER"database_backup1.xml"
cp $DB_LOCATION $DB_FOLDER"database_backup.xml"
java -jar Server.jar $DB_LOCATION"database.xml" > /var/log/jcserver/latest.log
