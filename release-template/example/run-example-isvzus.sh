#!/bin/sh

java -jar ../bin/odcs-simplescraper-1.0.0-jar-with-dependencies.jar scraper reparcs example-metadata-isvzus.properties example-data-isvzus.ttl example-provenance-metadata.rdf
java -jar ../bin/utils/regenerateUUID.jar example-metadata-isvzus.properties 