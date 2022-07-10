#!/bin/bash

curr_dir=$(cd "$(dirname "$0")"; pwd)
temp_dir=${curr_dir}/temp
python_install_dir=${curr_dir}/python
openssl_dir=/usr/local/openssl-1.1.1n
media_directory=${curr_dir}/media
python_media_name=Python-3.10.4
python_media_name_suffix=.tgz

if [ -d  ${temp_dir} ]; then
    echo "Okay, "${temp_dir}" exists."
else    
    mkdir ${temp_dir}
fi

if [ -d ${python_install_dir} ]; then
    echo "Okay, "${python_install_dir}" exists."
else
    mkdir ${python_install_dir}
fi

if [ -e ${media_directory}/${python_media_name}${python_media_name_suffix} ];then
    tar zxvf ${media_directory}/${python_media_name}${python_media_name_suffix} -C ${temp_dir}
else
   echo "Can not find Python media"
   exit 1
fi

sudo yum -y install zlib-devel bzip2-devel openssl-devel ncurses-devel sqlite-devel readline-devel tk-devel gdbm-devel db4-devel libpcap-devel xz-devel libffi-devel
sudo yum install gcc -y

pushd ${temp_dir}/${python_media_name}

./configure -C \
--with-openssl=${openssl_dir} \
--with-openssl-rpath=auto \
--prefix=${python_install_dir}

make -j8 && make altinstall

popd 

rm -rf ${temp_dir}/${python_media_name}

cd ${curr_dir}
${python_install_dir}/bin/python3.10 -V
echo "Completed."

