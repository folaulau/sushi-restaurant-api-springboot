#!/bin/bash

# run this from pom.xml file or commandline to update hasura metadata

# --project:  directory where commands are executed (default: current dir)
hasura metadata clear --endpoint http://localhost:7008 --admin-secret test --project hasura