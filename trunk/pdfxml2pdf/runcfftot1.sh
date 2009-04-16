#!/bin/sh
cfftot1=/usr/local/bin/cfftot1
t1rawafm=/usr/local/bin/t1rawafm
`$cfftot1 -o $2 $1`
`$t1rawafm -o $3 $2`
