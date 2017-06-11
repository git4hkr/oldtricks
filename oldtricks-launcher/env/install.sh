#!/bin/sh
INSTALL_DIR=${mvn.filter.install.dir}
F_PERMIT=644
D_PERMIT=755
C_PERMIT=755
DIRNAME=`dirname $0`

#
# functions
#
exit_m(){
	echo
	echo "--------------------------"
	echo "ERROR: an error occured in installation."
	echo "$1"
	echo "--------------------------"
	echo
	exit 1;
}

exec_cmd(){
	cmd=$*
	out=`$cmd`
	ecode=$?
	if [ $ecode -ne 0 ]
	then
		exit_m "cmd=[$cmd], exit_code=$ecode"
	else
		echo "CMD OK. [$cmd]"
	fi
	if [ -n "$out" ]
	then
		echo $out
	fi
}

#
# main scenario
#
if [ -d ${INSTALL_DIR} ]
then
	exec_cmd "/bin/cp -faL ${DIRNAME}/release/* ${INSTALL_DIR}"
else
	exit_m "Error:Install dir not exist. [${INSTALL_DIR}]"
fi

echo
echo "--------------------------"
echo "Succeeded in installation. "
echo "--------------------------"
exit 0;
