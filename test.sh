#! /bin/bash


for entry in minijava-examples-new/minijava-testsuite/*.out
do
    echo "*************************************************************************************"
    javaFileName=$(echo "$entry" | cut -f 1 -d '.')
    echo "******** TEST $javaFileName.java ********"
    echo "`java Main $javaFileName.java`"
    echo "******** CORRECT ********"
    echo "`cat $entry`"
    echo "*************************************************************************************"
    echo ""
    echo ""
done

