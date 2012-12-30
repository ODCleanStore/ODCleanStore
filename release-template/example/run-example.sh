#!/bin/sh

java -jar ../bin/odcs-simplescraper.jar scraper reparcs example-metadata.properties example-data.rdf example-provenance-metadata.rdf
java -jar ../bin/utils/regenerateUUID.jar example-metadata.properties 