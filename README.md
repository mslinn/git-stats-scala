# Git Activity

[![Build Status](https://travis-ci.org/mslinn/git-stats-scala.svg?branch=master)](https://travis-ci.org/mslinn/git-stats-scala)
[![GitHub version](https://badge.fury.io/gh/mslinn%2Fgit-stats-scala.svg)](https://badge.fury.io/gh/mslinn%2Fgit-status-scala)

For your resume: Totals and grand totals of lines added and deleted to/from your git repositories.

Ignores git repos if a file called `.ignore` exists in the root of the directory tree.

Help message:
```
$ bin/run --help
GitStats 0.1.0
Usage: GitStats [options] [<yyyy-mm>]

For Linux and Mac, an executable program called git must be on the PATH;
for Windows, and executable called git.exe must be on the Path.

Ignores files committed with these filetypes: exe, gz, log, pdf, tar, zip.

Tries to continue processing remaining git repos if an exception is encountered.

  -a, --author <value>  author to attribute
  -d, --dir <value>     directory to scan (defaults to current directory)
  -i, --ignore <value>  additional filetype to ignore, without the leading dot (can be specified multiple times)
  -v, --verbose         show per-repo subtotals)
  <yyyy-mm>             yyyy_mm to search (defaults to the date for the previous month, 2017-07)
  --help                prints this usage text
```

For example, to get all-time totals for the current git user (per git directory), type:
```
$ bin/run
Mike Slinn added 408 lines, deleted 139 lines, net 269 lines for language 'Scala' in git-stats-scala
Mike Slinn added 141 lines, deleted 0 lines, net 141 lines for language 'SBT' in git-stats-scala
Mike Slinn added 45 lines, deleted 0 lines, net 45 lines for language 'XML' in git-stats-scala
Mike Slinn added 46 lines, deleted 14 lines, net 32 lines for language 'Markdown' in git-stats-scala
Mike Slinn added 21 lines, deleted 0 lines, net 21 lines for language 'Miscellaneous' in git-stats-scala
Mike Slinn added 35 lines, deleted 32 lines, net 3 lines for language 'Unknown' in git-stats-scala
Mike Slinn added 1 lines, deleted 0 lines, net 1 lines for language 'Properties' in git-stats-scala
Mike Slinn added 697 lines, deleted 185 lines, net 512 lines in git-stats-scala

Mike Slinn added 697 lines, deleted 185 lines, net 512 lines in all git repositories
```

To get statistics for 2017 for the current logged on user, type:

    bin/run 2017

To get statistics for May 2017 for the current logged on user, type:

    bin/run 2017-05

To get statistics for 2016 for the GitHub user `mslinn`, type:

    bin/run -u mslinn 2016

## Scaladoc
[Here](http://mslinn.github.io/git-stats-scala/latest/api/index.html)

## License
This software is published under the [Apache 2.0 License](http://www.apache.org/licenses/LICENSE-2.0.html).

