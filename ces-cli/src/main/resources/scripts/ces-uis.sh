#! /bin/bash 
#
#     index CLI start script $Revision$
#
# if Java is not in your binary path, you need to supply its
# location in this script. The script automatically finds 
# LBE_HOME when called directly, via binary path, or symbolic
# link. 
#
#===================================================================
#
# configurables:

# path to the java interpreter
JAVA=java

# end configurables
#
#===================================================================
#
# calculate true location

PRG=`type $0`
PRG=${PRG##* }

# If PRG is a symlink, trace it to the real home directory

while [ -L "$PRG" ]
do
    newprg=$(ls -l ${PRG})
    newprg=${newprg##*-> }
    [ ${newprg} = ${newprg#/} ] && newprg=${PRG%/*}/${newprg}
    PRG="$newprg"
done

PRG=${PRG%/*}
CES_HOME=${PRG}/.. 

# --------------------------------------------------------------------

$JAVA -cp $CES_HOME/ces.jar com.adobe.dx.aep.skaluskar.poc.cesuis.cesuiscli.CesUISCli "$@"
