# Git Activity

[![Build Status](https://travis-ci.org/mslinn/userActivity.svg?branch=master)](https://travis-ci.org/mslinn/git-stats-scala)
[![GitHub version](https://badge.fury.io/gh/mslinn%2FuserActivity.svg)](https://badge.fury.io/gh/mslinn%2Fgit-status-scala)

Totals current git user's added and deleted lines.

```
Usage: sbt run GitStats [options] [<yyyy-mm>]

  -u, --user               GitHub user id
  <yyyy-mm>                yyyy_mm to search (defaults to all-time total)

```

Not sure if this still matters: Your github userid should be the same as your OS userid.

For example, to get all-time totals for the current git user (per git directory), type:

    bin/run

To get statistics for 2017 for the current logged on user, type:

    bin/run 2017

To get statistics for May 2017 for the current logged on user, type:

    bin/run 2017-05

To get statistics for 2016 for the GitHub user mslinn, type:

    bin/run -u mslinn 2016
