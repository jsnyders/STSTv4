#!/bin/sh
# Set STST_HOME to the folder where stst.zip was extracted to
# Rename to "stst.sh" or "stst" and copy to somewhere on your path.
# Make sure the java command is on the path
STST_HOME=$( cd "$( dirname $0 )" && pwd )
CP=$STST_HOME/build/jar/stst.jar:$STST_HOME/lib/ST-4.0.9.jar:$STST_HOME/lib/antlr-runtime-3.5.2.jar
java -cp $CP jjs.stst.STStandaloneTool $*
