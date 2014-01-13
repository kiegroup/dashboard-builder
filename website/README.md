Developing Drools and jBPM
==========================

**If you want to build or contribute to a droolsjbpm project, [read this document](https://github.com/droolsjbpm/droolsjbpm-build-bootstrap/blob/master/README.md).**

**It will save you and us a lot of time by setting up your development environment correctly.**
It solves all known pitfalls that can disrupt your development.
It also describes all guidelines, tips and tricks.
If you want your pull requests (or patches) to be merged into master, please respect those guidelines.

# How to build with Awestruct

Follow the instructions of Awestruct's [getting started guide](http://awestruct.org/getting_started/).

First set up your environment correctly:

    $ curl -L https://get.rvm.io | bash -s stable --ruby=1.9.3
    $ gem install awestruct bundler
    $ rake setup

Then build the website (before and after your changes):

    $ rake clean build
    $ firefox _site/index.html

And publish your changes:

    $ rake publish

    Note: this doesn't work, use `./build.sh publish` instead.
