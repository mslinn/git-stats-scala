# Scoped Git Activity Summary for a User

[![Build Status](https://travis-ci.org/mslinn/git-stats-scala.svg?branch=master)](https://travis-ci.org/mslinn/git-stats-scala)
[![GitHub version](https://badge.fury.io/gh/mslinn%2Fgit-stats-scala.svg)](https://badge.fury.io/gh/mslinn%2Fgit-status-scala)

For your resume: totals and grand totals of lines added and deleted to/from your git repositories.

[Read the author's blog](http://blog.mslinn.com/blog/2017/08/07/how-much-do-you-program) to learn why this program was written.

*The output of this program merely answers the question: "are you an active programmer?"
This program only reports textual additions and deletions, 
which one hopes are indications of actual programming. 
Statistics are reported for each computer language found.
The reader is free to impart any meaning they deem appropriate to this output.
The author of this software (GitStats) makes no claims regarding meaning.*

GitStats scans local git repos, so running the program does not generate outbound network traffic.

Ignores git repos containing a file called `.ignore.stats` in the root of the directory tree.
Obtains git repo histories by examining the output of `git log`.
The user name for each repository is obtained by running `git config user.name` in each repository.

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
  -i, --ignore <value>  Comma-separated additional filetypes to ignore, without the leading dot
  -I, --Ignore <value>  Comma-separated additional subdirectories to ignore, without slashes
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
....
Subtotals By Language (lines changed across all projects)
┌───────────────────┬───────────────────┬───────────────────┬──────────────────┐
│Language           │Lines added        │Lines deleted      │Net change        │
├───────────────────┼───────────────────┼───────────────────┼──────────────────┤
│Scala              │+40,701            │-16,219            │+24,482           │
│HTML               │+24,158            │-15,703            │+8,455            │
│SBT                │+10,386            │-4,342             │+6,044            │
│Java               │+3,823             │-1,631             │+2,192            │
│Markdown           │+2,323             │-728               │+1,595            │
│JavaScript         │+75,909            │-75,585            │+324              │
│Properties         │+656               │-451               │+205              │
│Bash               │+4                 │-4                 │0                 │
├───────────────────┼───────────────────┼───────────────────┼──────────────────┤
│Total              │+157,960           │-114,663           │+43,297           │
└───────────────────┴───────────────────┴───────────────────┴──────────────────┘
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
with subtotals, and all unknown files are to be ignored as well as XML files:

    sbt "run --previous365days -d /mnt/work/training/cadenza -v -o -i xml"

## Scaladoc
[Here](http://mslinn.github.io/git-stats-scala/latest/api/index.html)

## License
This software is published under the [Apache 2.0 License](http://www.apache.org/licenses/LICENSE-2.0.html).
