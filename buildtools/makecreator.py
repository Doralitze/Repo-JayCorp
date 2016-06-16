#! /usr/bin/env python3
#script to automatically create makefile code from packages

import sys
import os

packagefolder = ""
outputfile = "Makefile"
scansuffix = ".java"
targetsuffix = ".class"
rulestart = "$(compile)"
buildfolder = "build"
content = ""

for arg in sys.argv:
    if arg.startswith("-p="):
        packagefolder = arg.replace("-p=", "")
    elif arg.startswith("-o="):
        outputfile = arg.replace("-o=", "")
    elif arg.startswith("-ft="):
        scansuffix = arg.replace("-ft=", "")
    elif arg.startswith("-ot="):
        targetsuffix = arg.replace("-ot=", "")
    elif arg.startswith("-r="):
        rulestart = arg.replace("-r=")
    elif arg.startswith("-bf="):
        buildfolder = arg.replace("-bf=", "")

def scanDirectory(directory):
    c = os.listdir(directory)
    builddirectory = buildfolder + "/" + directory.replace(packagefolder, "")
    for file in c:
        if os.path.isfile(file) and file.endswith(scansuffix):
            rule = builddirectory + "/" + file.replace(scansuffix, targetsuffix).replace(directory, "") + targetsuffix
            rule = rule + ": " + builddirectory + file.replace(packagefolder, "")
            content = content + rule
            pass
        elif os.path.isdir(file):
            scanDirectory(file)

scanDirectory(packagefolder)
print(content)
