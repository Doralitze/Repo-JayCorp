#! /usr/bin/env python3
#script to automatically create makefile code from packages

import sys

packagefolder = ""

for arg in sys.argv:
    if arg.startswith("-p="):
        packagefolder = arg.replace("-p=", "")
