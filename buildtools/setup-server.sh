#! /bin/sh

echo "Installing server to /usr/bin/jc installation directory"
mkdir /var/jc
mkdir /var/log/jc
mkdir /var/bin/jc
cp -r ./* /usr/bin/jc/
useradd jcserver -b /usr/bin/jc -d /usr/bin/jc -c "jail user for jaycorp server" -l -M
chown jcserver /var/jc
chown jcserver /var/log/jc
chown jcserver /usr/bin/jc/*
chown jcserver /usr/bin/jc/*/*
chown jcserver /usr/bin/jc/*/*/*
echo "please edit the following chron file manually in order to set the @reboot flag to the startup script."
crontab -e -u jcserver
