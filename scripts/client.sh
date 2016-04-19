#! /bin/bash

[ -z $LOGTRACE_HOME ] && LOGTRACE_HOME=`cd ..;pwd`
echo "LOGTRACE_HOME   : "$LOGTRACE_HOME

LOGTRACE_BIN=$LOGTRACE_HOME/bin
LOGTRACE_SERVER=$LOGTRACE_HOME/server
LOGTRACE_LIB=$LOGTRACE_HOME/lib
LOGTRACE_LOG=$LOGTRACE_HOME/logs
LOGTRACE_OPTS=$LOGTRACE_BIN/runtime-c.properties

LOGTRACE_PROJECT_FILE_PREFIX="c1000kNetty"
LOGTRACE_PROJECT_FILE_SUFFIX="jar"

if [ ! -z "$CLASSPATH" ]; then
  CLASSPATH="$CLASSPATH"":"
fi


if [ ! -d "$LOGTRACE_LOG" ]; then
  mkdir $LOGTRACE_LOG
fi
if [ -z "$LOGTRACE_OUT" ]; then
  LOGTRACE_OUT="$LOGTRACE_LOG"/client.out
fi
touch "$LOGTRACE_OUT"
> "$LOGTRACE_OUT"

cd $LOGTRACE_SERVER
PROJECT_JAR=`ls -l $LOGTRACE_PROJECT_FILE_PREFIX*.$LOGTRACE_PROJECT_FILE_SUFFIX | grep '^-' | awk '{print $9}' | sort -V | awk 'END{print $1}'`
# TODO catch error
PROJECT_NAME=${PROJECT_JAR%$".$LOGTRACE_PROJECT_FILE_SUFFIX"}
if [ ! -d $LOGTRACE_SERVER/$PROJECT_NAME ]; then
  echo "extract $PROJECT_JAR"
  unzip -q $LOGTRACE_SERVER/$PROJECT_JAR -d $LOGTRACE_SERVER/$PROJECT_NAME
fi

CLASSPATH=$LOGTRACE_SERVER/$PROJECT_NAME:$CLASSPATH$LOGTRACE_LIB/*

JAVA_OPTS=""
while read line; do
  if [ -n "$line" -a "${line:0:1}" != "#" ]; then
    if [ -n "$JAVA_OPTS" ]; then
      JAVA_OPTS="$JAVA_OPTS $line"
    else
      JAVA_OPTS="$line"
    fi
  fi
done < "$LOGTRACE_OPTS"

echo "LOGTRACE_OUT    : $LOGTRACE_OUT"
echo "CLASSPATH : $CLASSPATH"
echo "JAVA_OPTS : $JAVA_OPTS"

cd $LOGTRACE_HOME
exec java $JAVA_OPTS -cp $CLASSPATH com.c1000k.netty.client.Netty5C1000kClient "172.18.2.100" "10000" "172.18.2.127" "8082">> "$LOGTRACE_OUT" 2>&1 &

