home=`pwd`
fake_client_pid_file=$home/fakeClient.pid

#Checking if pid file exist
if test -e $fake_client_pid_file; then
    pid=$(cat $fake_client_pid_file)
    kill -s 9 $pid
    rm -f $fake_client_pid_file
else
    echo "[" $fake_client_pid_file "] not exist."
    exit 1
fi
echo "Process "$pid "is terminated." 
