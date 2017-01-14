#! /bin/sh

echo "Installing server to /usr/bin/jc installation directory"
mkdir /var/jc
mkdir /var/log/jc
mkdir /usr/bin/jcserver
cp -r ./* /usr/bin/jcserver/
useradd jcserver -b /usr/bin/jc -d /usr/bin/jc -c "jail user for jaycorp server" -l -M
chown jcserver /var/jc
chown jcserver /var/log/jc
chown jcserver /usr/bin/jcserver/*
chown jcserver /usr/bin/jcserver/*/*
chown jcserver /usr/bin/jcserver/*/*/*
chmod 777 /var/jc
chmod 777 /var/log/jc
chmod 777 /usr/binserver/jc
echo "please edit the following chron file manually in order to set the @reboot flag to the startup script."
crontab -e -u jcserver
