#!/bin/sh

usage ()
{
  echo "$0 host port username password"
  exit 1
}

if [ $# -lt 4 ]
then
  usage
else
  isql $1:$2 $3 $4 06_test_data_Engine.sql
  isql $1:$2 $3 $4 07_test_data_QA.sql
  isql $1:$2 $3 $4 08_test_data_DN.sql
  isql $1:$2 $3 $4 09_test_data_OI.sql
  isql $1:$2 $3 $4 10_test_data_QE.sql
fi