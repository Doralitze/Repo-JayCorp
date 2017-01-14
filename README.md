# JayCorp

## Content

The goal of this project is to provide an open source solution for staff
vacation planning. Furthermore I try to write a highly configurable tool
collection to enable everybody fitting their needs.

## Technics

This software is capable of running on multiple platforms due to it's
usage of Java/Swing. It's developed and tested under UNIX like operating
systems but runs under Windows too. Please note that using OpenJDK instead
of the oracle one will improve your user experience. Running the server
software under UNIX (for example NetBSD, FreeBSD or Linux) will also save you
some trouble.

## Compiling

Simply type ant within the top level directory of the repository you just
cloned and you're good to go.

The following stuff is required to compile this project:
 * git (it's very likely that it's already installed on your system)
 * a working JDK (OpenJDK is preferred over the Oracle one, at least version 7)
 * a somewhat recent version of Apache Ant
 * a few MB of free disk space would be great

## Ant targets

The following Ant targets may be important to you:
 * install_all
 * install_client
 * install_server
 * installers
 * all

###install_all
This target will install both the server and the client software to your local
machine. This is recommended if you're planning to use that machine both for
hosting the server and administrating it. Note that running this target requires
root privileges (That means admin rights if you're going to do this on MSWindows).
You should also be aware of the fact that every install target doesn't create
symlinks to your path or alters your path variable in any way. That means that
you have to do that on your own.

###install_client
This target will only install the client software to your local computer.
Doing so is recommended if you have a server running somewhere else. Note that
running this target requires root privileges (That means admin rights if you're
going to do this on MSWindows). By default this target will install the client
to /usr/bin/jcclient and you have to add the executable inside that folder to
your path in a manual manner.

###install_server
Like the name says this target is desired to install the server application to
your machine. Note that running this target requires root privileges (That means
admin rights if you're going to do this on MSWindows). By default this target
will install the client to /usr/bin/jcserver and you have to add the executable
inside that folder to your path in a manual manner.

###installers
This target creates zipped distributions of the software. This is useful if you
don't want to install a complete compile environment on every machine you're
going to install the software on. On FreeBSD it is highly recommended to use
Poudriere instead of this method.

###all
This target compiles the entire software tree but does not install anything.
This especially usefull for testing changes and development.

## Documentation

Please visit https://technikradio.org/ for further details.

## License

This project is licensed under the terms of the
GNU General Public License Version 3 (also known as GPLv3).
For further information read LICENSE.md or go to
https://www.gnu.org/licenses/gpl.html.
