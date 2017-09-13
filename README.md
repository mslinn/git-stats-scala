# Scoped Git Activity Summary for a User

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Build Status](https://travis-ci.org/mslinn/git-stats-scala.svg?branch=master)](https://travis-ci.org/mslinn/git-stats-scala)
[![GitHub version](https://badge.fury.io/gh/mslinn%2Fgit-stats-scala.svg)](https://badge.fury.io/gh/mslinn%2Fgit-status-scala)

For your resume: subtotals and totals of lines added and deleted to/from a filtered collection of all your SCM working directories.
The following SCM systems are supported:

* Git
* Subversion

[Read the author's blog](http://blog.mslinn.com/blog/2017/08/07/how-much-do-you-program) to learn why this program was written.

*The output of this program merely answers the question: "are you an active programmer?"
This program only reports textual additions and deletions,
which one hopes are indications of actual programming.
Statistics are reported for each computer language found.
The reader is free to impart any meaning they deem appropriate to this output.
The author of this software makes no claims regarding meaning.*

GitStats looks for local SCM working directories and then retrieves commit statistics for each detected directory.
By default, GitStats limits only to local operations and avoids network calls.
Git allows history queries via local operations only, Subversion on the contrary requires network calls.
Therefore, by default GitStats collects only statistics for Git repositories.
To enable statistics for Subversion directories as well, user has to explicitly allow remote operations.

Implementation details:
* Ignores working directories containing a file called `.ignore.stats` in the root of the directory tree.
* Obtains Git repo histories by examining the output of `git log`.
* Obtains Subversion repo histories by examining the output of `svn log`.
* The user name for each Git repository is obtained by running `git config user.name` in each repository.
* The user name for all Subversion repositories is obtained by running `svn auth` once before processing Subversion directories.

Note that Subversion queries are much slower than Git's due to their remote implementation.
Subversion speed depends on the network channel bandwidth and the server capabilities.
A query for 1 year long interval may take 30-60 minutes.
Moreover, Subversion queries may fail due to connectivity issues. In this case, GitStats reports the error
and continues with the next working directory. 

The following file types are recognized: ASP, C, C++, Dart, Delphi, Dockerfile, F#, Groovy, Haskell, HTML, Java, JSP, Makefile, MS-DOS batch,
Objective-C, Markdown, Perl, PHP, Python, properties, R, Ruby, Scala, Shell scripts, SQL, Swift, Visual Basic, Windows script, XML and YAML.

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
  -r, --remote          Allow remote operations. Required to collect statistics on Subversion repositories.
  --help                Print this usage text
```

For example, to get all-time totals,
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

To get statistics for the previous 365 days, type:

    bin/run --prev-365

For the previous 30 days:

    bin/run --prev-30

For the previous 90 days:

    bin/run --prev-90

For all of May 2017:

    bin/run --from={2017-05-01} --to={2017-05-31}

For all of May 2017, including Subversion repositories:

    bin/run --from={2017-05-01} --to={2017-05-31} -r

You can also run SBT if desired.
The options shown below collects statistics for the previous year, for all Git projects under `/work/cadenza`,
with subtotals. All ignore commits for unknown file types as well as commits for JavaScript and XML files.
Also ignore any commits involving the entire `modules/cadenza/public/ckeditor/` subdirectory:

    sbt "run --prev-365 -d /work/cadenza -s -o -i js,xml -I modules/cadenza/public/ckeditor"

## Scaladoc
[Here](http://mslinn.github.io/git-stats-scala/latest/api/index.html)
