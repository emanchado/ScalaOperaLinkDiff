DEPRECATED
==========

This program is deprecated. It probably doesn't work anymore on more recent versions of Scala anyway.






Opera Link diff for Scala
=========================

This is a small experiment to see if I could calculate a "diff"
between two sets of Opera Link data (say, the difference between two
backups) with the
[Opera Link Scala library](https://github.com/emanchado/Scala-OperaLink). It
consists of a simple diff class, `org.demiurgo.operalink.Diff` (that
returns the "diff" as a sequence of
`org.demiurgo.operalink.LinkAPIItemDiff` objects) and a *very* simple
program that shows a simplistic diff between two Opera Link backups
(just the full JSON text returned by the Opera Link API when calling
something like `/rest/bookmark/descendants`).

The idea is not showing, or even calculating, a machine-readable diff
that can be applied with something like `patch`, but something
human-readable that makes it easy to understand what has changed
between two snapshots of the data. Say, to let a user decide if she
wants to go back to a certain snapshot, or if she wants to import
certain information from it.

You can run the utility with:

    sbt "run backup1.json backup2.json"

To run it, you'll need to install the Opera Link Scala library. To run
the tests, you'll need [ScalaTest](http://www.scalatest.org/) in addition.
