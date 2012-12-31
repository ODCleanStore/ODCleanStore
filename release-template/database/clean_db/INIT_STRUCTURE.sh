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
  isql $1:$2 $3 $4 00_autocommit_on.sql
  isql $1:$2 $3 $4 01_create_rel_database.sql
  isql $1:$2 $3 $4 02_stored_procedures.sql
  isql $1:$2 $3 $4 03_odcs_prefixes.sql
  isql $1:$2 $3 $4 04_user_accounts.sql
  isql $1:$2 $3 $4 05_enable_fulltext_index.sql
fi
