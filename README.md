VersionControlCheck
===================

This project is a maven plugin which validates the major, minor and build numbers of modules in a given parent pom. It is intended to replace the use of the unit test currently used for the same puropse. The build number is derived from either the SVN revision number or the changes since last branch in GIT. The resulting properties are also stored in properties files for use in the application at run time. Properties are also provided for each module to be used in setting the dependencies between the various modules.
