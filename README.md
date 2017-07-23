
This is an attempt to implement a Cron Parser. This codebase produces a script that parses and outputs the representation of the given cron command according to the task description. Invalid cron commands are rejected.

# Running

## Prerequisites

`scala` should be installed on the host machine to run the script. Ideally version `2.12.*`.  
`sbt 0.13` should be installed on the host to run the tests and to run the script via sbt console.

## Make Targets

### Compile
`make compile`

### Run
`make run` compiles and runs the script. Cron command should be provided in the `CMD` argument;  
`make run CMD="1 1 1 1 4 /usr/bin/find"`

After the initial compilation you can use the `run-without-compile` target in the same manner to avoid compilation at each run.

Alternatively you can run `make sbt` (requires sbt) and in the sbt console you can use `run` command;  
`make sbt`
`run "1 1 1 1 4 /usr/bin/find"`


### Run the Tests
`make test` (requires sbt)

# Supported Features

## Single Numeric Values
Examples are;  
`1 1 1 1 4 /usr/bin/find`  
`20 12 5 1 1 /usr/bin/find`

## Comma Separated Numeric Values
Examples are;  
`1,3,5,10 1 1 1 4 /usr/bin/find`  
`1,3,5,10 1 1 1 1,2,3,4,5 /usr/bin/find`

## Numeric Ranges
Examples are;  
`1-10 1 1 1 4 /usr/bin/find`  
`50 1-10 1-5 1 1 /usr/bin/find`

## Single Literal Values for Months and Day of Week
Examples are;  
`1 1 1 JAN 4 /usr/bin/find`  
`1 1 1 MAY 4 /usr/bin/find`  
`1 1 1 AUG 4 /usr/bin/find`  
`1 1 1 JAN SUN /usr/bin/find`  
`1 1 1 JAN MON /usr/bin/find`

## Comma Separated Literal Values for Months and Day of Week
Examples are;  
`1 1 1 JAN,FEB,MAR 4 /usr/bin/find`  
`1 1 1 MAY,AUG,JAN 4 /usr/bin/find`  
`1 1 1 JAN SUN,MON,FRI /usr/bin/find`  

## Literal Ranges for Months and Day of Week
Examples are;  
`1 1 1 JAN-MAR 4 /usr/bin/find`  
`1 1 1 MAY-AUG 4 /usr/bin/find`  
`1 1 1 JAN SUN-FRI /usr/bin/find`  

## Single Asterisk
Examples are;  
`* 1 1 1 1 /usr/bin/find`  
`1 1 * 1 1 /usr/bin/find`  
`1 1 1 1 * /usr/bin/find`  


# Not Supported Features

## Asterisk with Dividends
Expressions sunc as `*/15` are not supported, even though the task description expects it. **This feature is left out in order to reduce the development time**.


## Non-standard Features
Non-standard expression such as `@yearly`, `@monthly` and other non-standard symbols such as `?`,`H`,`L`,`W` are not supported as indicated in the task description. The non-standard `Year` field is also not supported.

# Assumptions
Input values are strictly validated, there is no graceful degradation for the input values. Invalid inputs are rejected.

# Improvements
At the moment the script doesn't give any indication as to why an input was deemed invalid. This is the most important feature that lacks at this version of the script.
