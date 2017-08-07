# Git Activity

[![Build Status](https://travis-ci.org/mslinn/git-stats-scala.svg?branch=master)](https://travis-ci.org/mslinn/git-stats-scala)
[![GitHub version](https://badge.fury.io/gh/mslinn%2Fgit-stats-scala.svg)](https://badge.fury.io/gh/mslinn%2Fgit-status-scala)

For your resume: Totals and grand totals of lines added and deleted to/from your git repositories.

*The output of this program merely answers the question: "are you an active programmer?"
This program only reports textual additions and deletions, 
which one hopes are indications of actual programming. 
Statistics are reported for each computer language found.
The reader is free to impart any meaning they deem appropriate to this output.
The author of this software (GitStats) makes no claims regarding meaning.*

Ignores git repos containing a file called `.ignore.stats` in the root of the directory tree.
Obtains git repo histories by examining the output of `git log`.

The help message is dynamically generated, so the dates shown in the help message correspond to the system clock:
```
$ bin/run --help
GitStats 0.1.0
Usage: GitStats [options]

For Linux and Mac, an executable program called git must be on the PATH;
for Windows, and executable called git.exe must be on the Path.

Ignores files committed with these filetypes: exe, gif, gz, jpg, log, pdf, png, tar, zip.
Ignores directories committed called node_modules.

Tries to continue processing remaining git repos if an exception is encountered.

  -3, --prev-30         Process the most recent 30 days; same as specifying --from=2017-07-07 --to=2017-08-06
  -9, --prev-90         Process the most recent 90 days; same as specifying --from=2017-05-08 --to=2017-08-06
  -a, --author <value>  Author to attribute
  -d, --dir <value>     Directory to scan (defaults to current directory)
  -f, --from <value>    First date to process, in yyyy-MM-dd format; default is no limit
  -i, --ignore <value>  Additional filetype to ignore, without the leading dot (can be specified multiple times)
  -I, --Ignore <value>  Additional subdirectories to ignore, without slashes (can be specified multiple times)
  -m, --prev-month      Process the most recent complete month's data; same as specifying --from=2017-07-06 --to=2017-08-06
  -o, --only-known      If a filetype is not recognized, ignore it's data when summarizing commits; filters out Unknown and Miscellaneous filetypes
  -t, --to <value>      Last date to process, in yyyy-MM-dd format; default is no limit
  -v, --verbose         Show per-repo subtotals
  -y, --prev-365        Same as specifying --from=2016-08-06 --to=2017-08-06
  --help                Print this usage text
```

For example, to get all-time totals for the current git user (per git directory), 
walking the directory tree below the current directory, type:
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

<img src='https://raw.githubusercontent.com/mslinn/git-stats-scala/images/resume-polish.jpg' align='right' width='25%'>

To get statistics for the previous 365 days for the current logged on user, type:
    
    bin/run --previous365days

For the previous month:

    bin/run --previousMonth

For the previous 30 days:

    bin/run --previous30days

For the previous 90 days:

    bin/run --previous90days

For all of May 2017:

    bin/run --from={2017-05-01} --to={2017-05-31}

For 2016 for the GitHub user `mslinn`, type:

    bin/run -u mslinn --from={2016-01-01} --to={2016-12-31}

You can also run SBT if desired. 
The options shown cause git logs from all of 2016 to be processed, for the `cadenza` project, 
with subtotals, and all unknown files are to be ignored:

    sbt "run --previous365days -d /mnt/c/work/training/cadenza -v -o"

## Scaladoc
[Here](http://mslinn.github.io/git-stats-scala/latest/api/index.html)

## License
This software is published under the [Apache 2.0 License](http://www.apache.org/licenses/LICENSE-2.0.html).
