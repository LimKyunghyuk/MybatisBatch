##############################################################################
# Smart SFA Mobile Batch
# 2018.03.06 khlim
##############################################################################

# 1. batch default info
batchName=INSERT_GP_COMMON_CODE
batchLocation=/msfa/mobile/batch/workspace/classes
batchMain=batch.main.BatchMain

# 2. set lib
SCRIPTDIR=/msfa/mobile/batch/workspace/lib
CLASSPATH=.
 
for file in $SCRIPTDIR/*.jar
do
CLASSPATH=$CLASSPATH:$file 
done
CLASSPATH=$CLASSPATH:$batchLocation
export CLASSPATH

# 3. set logfile and folder
v=`date +%Y-%m-%d-%H_%M_%S`

logdir=/msfa/mobile/batch/log
logPathHolderYear=`date +%Y`
logPathHolderMm=`date +%m`
logPathHolderDd=`date +%d`
logPath=$logdir/$logPathHolderYear/$logPathHolderMm/$logPathHolderDd

mkdir $logdir/$logPathHolderYear
mkdir $logdir/$logPathHolderYear/$logPathHolderMm
mkdir $logdir/$logPathHolderYear/$logPathHolderMm/$logPathHolderDd

nohup java -cp $CLASSPATH -Dfile.encoding=UTF-8 -Xms1024m -Xmx1024m -Dmode=dev $batchMain $batchName > $logPath/$batchName$v.log &


