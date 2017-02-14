#!/usr/bin/bash

for warfile in $( ls *.war); 
do
	IFS='.' read -ra parts <<< "$warfile"
	mkdir ${parts[0]}
	cd ${parts[0]}
	jar -xvf ../$warfile 
	cd ..
	if [ -d "/uz/ritme/modules/ritme/${parts[0]}/custom" ];
	then
		cp -R /uz/ritme/modules/ritme/${parts[0]}/custom/* ./${parts[0]}/
		jar -cvf $warfile -C ${parts[0]} .	
	fi	
	cp $warfile /opt/jboss/wildfly/standalone/deployments/
	unset parts
done
#/usr/bin/bash
/opt/jboss/wildfly/bin/standalone.sh -b 0.0.0.0
	