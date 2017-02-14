FROM jboss/wildfly
MAINTAINER bdcuyp0

ENV JAVA_OPTS="-Duz.s9server.prodOrPlaySybaseServer=PROD -Duz.env.server=PROD -Xmx1024m"

ADD ./standalone.xml /opt/jboss/wildfly/standalone/configuration/
ADD ./sun/jdk/module.xml /opt/jboss/wildfly/modules/system/layers/base/sun/jdk/main
ADD ./javax/api/module.xml /opt/jboss/wildfly/modules/system/layers/base/javax/api/main

# maak onderstaande warfiles aan door een war te builden met behulp van ivy_ritme.xml
# dit zorgt ervoor dat er geen uz-specifieke code in de image opgenomen wordt
# zorg er voor dat de class-files (WEB-INF/classes) wel aanwezig zijn.
ADD ./addressbookwebservice.war /opt/jboss
ADD ./vitalinkwebservice.war /opt/jboss
ADD ./recipewebservice.war /opt/jboss
ADD ./merge_deploy.sh  /opt/jboss

USER root
RUN mkdir /uz && \
	chmod 777 /uz && \
	mkdir /uz/data && \
	chmod 777 /uz/data && \
	mkdir /uz/ritme && \
	chmod 777 /uz/ritme && \
	mkdir /uz/ritme/modules && \
	chmod 777 /uz/ritme/modules && \
	chmod 777 /opt/jboss/merge_deploy.sh && \
	chmod 777 /opt/jboss/*.war
	
USER jboss

# dit volume is gemaakt voor certificaten. De structuur van dit volume is afhankelijk van 
# de plugin code die het certificaat leest (kan verschillen per ziekenhuis)
VOLUME /uz/data

# alle ziekenhuisspecifieke code komt in dit volume terecht.
# de image gaat op zoek naar code in ${war-name (zonder .war)}/custom
# hij verwacht vervangende code en additionele code te vinden op de plaats waar ze in de war zou moeten staan.
# vb. jar-files in ${war-name (zonder .war)}/custom/WEB-INF/lib
VOLUME /uz/ritme/modules


CMD ["/opt/jboss/merge_deploy.sh"]
