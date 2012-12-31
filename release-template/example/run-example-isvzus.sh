#!/bin/sh

java -jar ../bin/odcs-simplescraper.jar scraper reparcs example-metadata-isvzus.properties example-data-isvzus.ttl example-provenance-metadata.rdf
java -jar ../bin/utils/regenerateUUID.jar example-metadata-isvzus.properties 