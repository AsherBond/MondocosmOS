#!/bin/bash

script_full_name=$(cd "${0%/*}" && echo $PWD/${0##*/})
script_directory=`dirname "$script_full_name"`
cd ${script_directory}

wget -v -m 'ftp://ftpdatos.aemet.es/datos_observacion/'
