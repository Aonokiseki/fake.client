#!/bin/bash

curr_dir=$(cd "$(dirname "$0")"; pwd)
temp_dir=${curr_dir}/temp
openssl_dir=/usr/local/openssl-1.1.1n
media_directory=${curr_dir}/media
openssl_media_name=openssl-1.1.1n
openssl_media_name_suffix=.tar.gz
cert_dir=`find /etc/ -name openssl.cnf`

if [ -d  ${temp_dir} ]; then
    echo "Okay, "${temp_dir}" exists."
else    
    mkdir ${temp_dir}
fi

if [ -d ${openssl_dir} ]; then
    echo "Okay, "${openssl_dir}" exists."
else
    mkdir ${openssl_dir}
fi

if [ -e ${media_directory}/${openssl_media_name}${openssl_media_name_suffix} ]; then
    tar zxvf ${media_directory}/${openssl_media_name}${openssl_media_name_suffix} -C ${temp_dir}
else
   echo "Can not find OpenSSL media"
   exit 1
fi

sudo yum -y install gcc

cd ${temp_dir}/${openssl_media_name}

./config \
--prefix=${openssl_dir} \
--libdir=lib \
--openssldir=${cert_dir}

make -j1 depend
make -j8
make install_sw

rm -rf ${temp_dir}/${openssl_media_name}

cd ${curr_dir}
echo "Completed."

