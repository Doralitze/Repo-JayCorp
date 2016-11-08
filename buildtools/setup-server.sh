#! /bin/sh
scriptname=`basename "$0"`

  if [[ $UID -ne 0 ]]; then
     echo "${scriptname} must be run as root"
     exit 1
  fi

echo "Installing server to current installation directory"
mkdir /var/jc
mkdir /var/log/jc
useradd jcserver -b . -d . -c "jail user for jaycorp server" -l -M 
chown jcserver /var/jc
chown jcserver /var/log/jc
echo "please edit the following chron file manually in order to set the @reboot flag to the startup script."
chrontab -e -u jcserver
