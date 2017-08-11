# Scoped Git Activity Summary for a User

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Build Status](https://travis-ci.org/mslinn/git-stats-scala.svg?branch=master)](https://travis-ci.org/mslinn/git-stats-scala)
[![GitHub version](https://badge.fury.io/gh/mslinn%2Fgit-stats-scala.svg)](https://badge.fury.io/gh/mslinn%2Fgit-status-scala)

For your resume: subtotals and totals of lines added and deleted to/from a filtered collection of all your git repositories.

[Read the author's blog](http://blog.mslinn.com/blog/2017/08/07/how-much-do-you-program) to learn why this program was written.

*The output of this program merely answers the question: "are you an active programmer?"
This program only reports textual additions and deletions,
which one hopes are indications of actual programming.
Statistics are reported for each computer language found.
The reader is free to impart any meaning they deem appropriate to this output.
The author of this software makes no claims regarding meaning.*

GitStats scans local git repos, so running the program does not generate outbound network traffic.

Ignores git repos containing a file called `.ignore.stats` in the root of the directory tree.
Obtains git repo histories by examining the output of `git log`.
The user name for each repository is obtained by running `git config user.name` in each repository.

The following file types are recognized: ASP, C, C++, Dart, Delphi, F#, Groovy, Haskell, HTML, Java, JSP, MS-DOS batch,
Objective-C, Markdown, Perl, PHP, Python, properties, R, Ruby, Scala, Shell scripts, SQL, Swift, Visual Basic, Windows script and XML.

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

  -3, --prev-30         Process the most recent 30 days; same as specifying --from=2017-07-13 --to=2017-08-11
  -9, --prev-90         Process the most recent 90 days; same as specifying --from=2017-05-14 --to=2017-08-11
  -d, --dir <value>     Directory to scan (defaults to current directory)
  -e, --excel <value>   Output an Excel .XLSX file with the given name instead of UTF-8 tables
  -f, --from <value>    First date to process (inclusive), in yyyy-MM-dd format; default is no limit
  -i, --ignore <value>  Comma-separated additional filetypes to ignore, without the leading dot
  -I, --Ignore <value>  Comma-separated additional relative subdirectories to ignore, ending slashes are optional
  -m, --prev-month      Process the most recent complete month's data; same as specifying --from=2017-07-11 --to=2017-08-11
  -o, --only-known      If a filetype is not recognized, ignore it's data when summarizing commits; filters out Unknown and Miscellaneous filetypes
  -s, --subtotals       Show per-repo subtotals
  -t, --to <value>      Last date to process (inclusive), in yyyy-MM-dd format; default is no limit
  -O, --output          Show output of OS commands executed
  -v, --verbose         Show OS commands executed and dots indicating progress
  -y, --prev-365        Same as specifying --from=2016-08-12 --to=2017-08-11
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

    bin/run --prev-365

For the previous 30 days:

    bin/run --prev-30

For the previous 90 days:

    bin/run --prev-90

For all of May 2017:

    bin/run --from={2017-05-01} --to={2017-05-31}

For 2016 for the GitHub user `mslinn`, type:

    bin/run -u mslinn --from={2016-01-01} --to={2016-12-31}

You can also run SBT if desired.
The options shown below cause `git log`s from the previous year to be processed, for all git projects under `/work/cadenza`,
with subtotals. All ignore commits for unknown file types as well as commits for JavaScript and XML files.
Also ignore any commits involving the entire `modules/cadenza/public/ckeditor/` subdirectory:

    sbt "run --prev-365 -d /work/cadenza -s -o -i js,xml -I modules/cadenza/public/ckeditor"

## Scaladoc
[Here](http://mslinn.github.io/git-stats-scala/latest/api/index.html)
