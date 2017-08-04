# Git Activity

[![Build Status](https://travis-ci.org/mslinn/userActivity.svg?branch=master)](https://travis-ci.org/mslinn/git-stats-scala)
[![GitHub version](https://badge.fury.io/gh/mslinn%2FuserActivity.svg)](https://badge.fury.io/gh/mslinn%2Fgit-status-scala)

Reports git contributors

```
Usage: sbt run GitStats [options] [<yyyy-mm>]

  -u, --user               GitHub user id
  <yyyy-mm>                yyyy_mm to search (defaults to the date for the previous month, 2017-06)

```
For example, to get git statistics for the previous 12 months for the current logged on user
(their github userid must be the same as their OS userid), type:

    GitStats

To get git statistics for May 2017 for the current logged on user, type:

    GitStats 2017-05

To get git statistics for all of 2016 for the current logged on user, type:

    GitStats 2016

To get git statistics for 2016 for the GitHub user mslinn, type:

    GitStats -u mslinn 2016
