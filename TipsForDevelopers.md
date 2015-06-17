# Introduction #

Welcome to placebook! To get hacking, you need to check out the svn repository from the source tab of this project. If you are a developer you can check out over https, which means you can commit changes directly. Otherwise, contact one of the developers to get added, or submit patches to one of the developers.

This page has some useful knowledge gleaned from our experiences working on this project.

# Details #

## Working on Code ##

First, use eclipse to check out code from the google svn repository. You should use the Eclipse Web Tools plugin to check the code out into a "Dynamic Web Project", which will correctly identify the various components of the application.

You can run the application on your local machine using eclipse by installing apache-tomcat. Look at [issue 4](https://code.google.com/p/placebook/issues/detail?id=4) (http://code.google.com/p/placebook/issues/detail?id=4) for more details.

## Committing Changes ##

Only developers can commit changes. Others should send changes as patches to one of the developers, or request developer status.

Changes should not be committed until they have been thoroughly tested, and all new functionality should have an associated unit test of some kind.

Once changes have been committed they can be deployed on the application server by connecting to the facebook@user.interface.org.nz, then entering the following commands:

```
cd placebook
svn update
ant remove
ant install
```