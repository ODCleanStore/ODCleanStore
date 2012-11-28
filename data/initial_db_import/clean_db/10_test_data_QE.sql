INSERT INTO DB.ODCLEANSTORE.CR_PROPERTIES (property, multivalueTypeId, aggregationTypeId) VALUES (
	n'http://www.w3.org/2003/01/geo/wgs84_pos#long', 
	(SELECT id FROM DB.ODCLEANSTORE.CR_MULTIVALUE_TYPES WHERE label = 'DEFAULT'),
	(SELECT id FROM DB.ODCLEANSTORE.CR_AGGREGATION_TYPES WHERE label = 'AVG'));

INSERT INTO DB.ODCLEANSTORE.CR_PROPERTIES (property, multivalueTypeId, aggregationTypeId) VALUES (
	n'http://www.w3.org/1999/02/22-rdf-syntax-ns#type',
	(SELECT id FROM DB.ODCLEANSTORE.CR_MULTIVALUE_TYPES WHERE label = 'YES'),
	(SELECT id FROM DB.ODCLEANSTORE.CR_AGGREGATION_TYPES WHERE label = 'DEFAULT'));

SPARQL INSERT INTO <http://odcs.mff.cuni.cz/namedGraph/qe-test/berlin/dbpedia> {
	<http://dbpedia.org/resource/Berlin>	<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>	<http://dbpedia.org/class/yago/Locations>.
	<http://dbpedia.org/resource/Berlin>	<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>	<http://schema.org/City>.
	<http://dbpedia.org/resource/Berlin>	<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>	<http://umbel.org/umbel/rc/Village>.
	<http://dbpedia.org/resource/Berlin>	<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>	<http://schema.org/Place>.
	<http://dbpedia.org/resource/Berlin>	<http://www.w3.org/2000/01/rdf-schema#label>	"Berlin"@en.
	<http://dbpedia.org/resource/Berlin>	<http://www.w3.org/2003/01/geo/wgs84_pos#lat>	"52.50055694580078".
	<http://dbpedia.org/resource/Berlin>	<http://www.w3.org/2003/01/geo/wgs84_pos#long>	"13.39888858795166".
#	<http://dbpedia.org/resource/Berlin>	<http://dbpedia.org/ontology/abstract>	"Berlin is the capital city of Germany and is one of the 16 states of Germany. With a population of 3.45 million people, Berlin is Germany's largest city. It is the second most populous city proper and the seventh most populous urban area in the European Union. Located in northeastern Germany, it is the center of the Berlin/Brandenburg Metropolitan Region, which has 4.4 million residents from over 190 nations. Located in the European Plains, Berlin is influenced by a temperate seasonal climate. Around one third of the city's area is composed of forests, parks, gardens, rivers and lakes. First documented in the 13th century, Berlin was the capital of the Kingdom of Prussia (1701-1918), the German Empire (1871-1918), the Weimar Republic (1919-1933) and the Third Reich (1933-1945). Berlin in the 1920s was the third largest municipality in the world. After World War II, the city became divided into East Berlin-the capital of East Germany-and West Berlin, a West German exclave surrounded by the Berlin Wall (1961-1989). Following German reunification in 1990, the city regained its status as the capital of Germany, hosting 147 foreign embassies. Berlin is a world city of culture, politics, media, and science. Its economy is primarily based on the service sector, encompassing a diverse range of creative industries, media corporations, and convention venues. Berlin also serves as a continental hub for air and rail transport, and is a popular tourist destination. Significant industries include IT, pharmaceuticals, biomedical engineering, biotechnology, electronics, traffic engineering, and renewable energy. Berlin is home to renowned universities, research institutes, orchestras, museums, and celebrities, as well as host of many sporting events. Its urban settings and historical legacy have made it a popular location for international film productions. The city is well renowned for its festivals, diverse architecture, nightlife, contemporary arts, public transportation networks and a high quality of living."@en.
	<http://dbpedia.org/resource/Berlin>	<http://dbpedia.org/ontology/populationTotal>	"3450889".
	<http://dbpedia.org/resource/Berlin>	<http://dbpedia.org/property/name>	"Berlin"@en.
	<http://dbpedia.org/resource/Berlin>	<http://dbpedia.org/property/population>	"3450889"^^<http://www.w3.org/2001/XMLSchema#int>.
	<http://dbpedia.org/resource/Berlin>	<http://dbpedia.org/ontology/country>	<http://dbpedia.org/resource/Germany>.
};
SPARQL INSERT INTO <http://odcs.mff.cuni.cz/namedGraph/qe-test/berlin/freebase> {
	<http://rdf.freebase.com/ns/en.berlin>	<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>	<http://rdf.freebase.com/ns/location.citytown>.
	<http://rdf.freebase.com/ns/en.berlin>	<http://www.w3.org/2002/07/owl#sameAs>	<http://dbpedia.org/resource/Berlin>.
	<http://rdf.freebase.com/ns/en.berlin>	<http://www.w3.org/2002/07/owl#sameAs>	<http://dbpedia.org/resource/CityBerlin>.
	<http://rdf.freebase.com/ns/en.berlin>	<http://rdf.freebase.com/ns/location.geocode.longtitude>	"13.412687".
	<http://rdf.freebase.com/ns/en.berlin>	<http://rdf.freebase.com/ns/common.topic.alias>	"Berlin, Germany"@en.
	<http://rdf.freebase.com/ns/en.berlin>	<http://rdf.freebase.com/ns/common.topic.alias>	"Land Berlin"@en.
	<http://rdf.freebase.com/ns/en.berlin>	<http://rdf.freebase.com/ns/type.object.name>	"Berlin"@en.
	<http://rdf.freebase.com/ns/en.berlin>	<http://rdf.freebase.com/ns/location.geocode.latitude>	"52.52333831787109".
};
SPARQL INSERT INTO <http://odcs.mff.cuni.cz/namedGraph/qe-test/berlin/geonames> {
	<http://sws.geonames.org/2950159/>	<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>	<http://www.geonames.org/ontology#Feature>.
	<http://sws.geonames.org/2950159/>	<http://www.geonames.org/ontology#name>	"Berlin".
	<http://sws.geonames.org/2950159/>	<http://www.geonames.org/ontology#alternateName>	"Berlin"@en.
	<http://sws.geonames.org/2950159/>	<http://www.geonames.org/ontology#population>	"3426354".
	<http://sws.geonames.org/2950159/>	<http://www.w3.org/2003/01/geo/wgs84_pos#lat>	"52.52437".
	<http://sws.geonames.org/2950159/>	<http://www.w3.org/2003/01/geo/wgs84_pos#long>	"13.41053".
};
SPARQL INSERT INTO <http://odcs.mff.cuni.cz/namedGraph/qe-test/berlin/linkedgeodata> {
	<http://linkedgeodata.org/triplify/node240109189>	<http://www.w3.org/2000/01/rdf-schema#label> "Berlino"@it .
	<http://linkedgeodata.org/triplify/node240109189>	<http://www.w3.org/2000/01/rdf-schema#label> "Berlin"@en .
	<http://linkedgeodata.org/triplify/node240109189>	<http://www.w3.org/2003/01/geo/wgs84_pos#long> "13.3888548"^^<http://www.w3.org/2001/XMLSchema#decimal> .
	<http://linkedgeodata.org/triplify/node240109189>	<http://www.w3.org/2003/01/geo/wgs84_pos#lat> "52.5170397"^^<http://www.w3.org/2001/XMLSchema#decimal> .
	<http://linkedgeodata.org/triplify/node240109189>	<http://linkedgeodata.org/ontology/population> "3420768"^^<http://www.w3.org/2001/XMLSchema#integer> .
	<http://linkedgeodata.org/triplify/node240109189>	<http://linkedgeodata.org/property/capital> "yes" .
	<http://linkedgeodata.org/triplify/node240109189>	<http://www.georss.org/georss/point> "52.5170397 13.3888548" .
};
SPARQL INSERT INTO <http://odcs.mff.cuni.cz/namedGraph/qe-test/berlin/error> {
	<http://dbpedia.org/resource/Berlin>	<http://www.w3.org/2003/01/geo/wgs84_pos#lat>	"13.412687".
	<http://dbpedia.org/resource/Berlin>	<http://rdf.freebase.com/ns/location.geocode.latitude>	"13.412687".
};
SPARQL INSERT INTO <http://odcs.mff.cuni.cz/namedGraph/qe-test/germany/dbpedia> {
	<http://dbpedia.org/resource/Germany>	<http://www.w3.org/2000/01/rdf-schema#label>	"Deutschland".
};

SPARQL INSERT INTO <http://odcs.mff.cuni.cz/namedGraph/qe-test/berlin/sameAs> {
	<http://dbpedia.org/resource/Berlin>	<http://www.w3.org/2002/07/owl#sameAs>	<http://dbpedia.org/resource/Berlin>.
	<http://dbpedia.org/resource/Berlin>	<http://www.w3.org/2002/07/owl#sameAs>	<http://sws.geonames.org/2950159/>.
	<http://dbpedia.org/resource/Berlin>	<http://www.w3.org/2002/07/owl#sameAs>	<http://linkedgeodata.org/triplify/node240109189>.
	<http://dbpedia.org/resource/Berlin>	<http://www.w3.org/2002/07/owl#sameAs>	<http://www4.wiwiss.fu-berlin.de/eurostat/resource/regions/Berlin>.

	<http://schema.org/City>	<http://www.w3.org/2002/07/owl#sameAs>	<http://odcs.mff.cuni.cz/resource/qe-test/berlin/City>.
	<http://odcs.mff.cuni.cz/resource/qe-test/berlin/City>	<http://www.w3.org/2002/07/owl#sameAs>	<http://odcs.mff.cuni.cz/resource/qe-test/berlin/City2>.
	<http://odcs.mff.cuni.cz/resource/qe-test/berlin/City2>	<http://www.w3.org/2002/07/owl#sameAs>	<http://rdf.freebase.com/ns/location.citytown>.
};
SPARQL INSERT INTO <http://odcs.mff.cuni.cz/namedGraph/qe-test/property-sameAs> {
	<http://www.w3.org/2003/01/geo/wgs84_pos#lat>	<http://www.w3.org/2002/07/owl#sameAs>	<http://rdf.freebase.com/ns/location.geocode.latitude>.
	<http://www.w3.org/2003/01/geo/wgs84_pos#long>	<http://www.w3.org/2002/07/owl#sameAs>	<http://rdf.freebase.com/ns/location.geocode.longtitude>.
	<http://dbpedia.org/property/name>	<http://www.w3.org/2002/07/owl#sameAs>	<http://www.w3.org/2000/01/rdf-schema#label>.
	<http://dbpedia.org/property/name>	<http://www.w3.org/2002/07/owl#sameAs>	<http://www.geonames.org/ontology#name>.
	<http://dbpedia.org/property/name>	<http://www.w3.org/2002/07/owl#sameAs>	<http://www.geonames.org/ontology#alternateName>.
	<http://dbpedia.org/property/name>	<http://www.w3.org/2002/07/owl#sameAs>	<http://rdf.freebase.com/ns/type.object.name>.
	<http://dbpedia.org/property/population>	<http://www.w3.org/2002/07/owl#sameAs>	<http://dbpedia.org/ontology/populationTotal>.
	<http://dbpedia.org/property/population>	<http://www.w3.org/2002/07/owl#sameAs>	<http://www.geonames.org/ontology#population>.
	<http://dbpedia.org/property/population>	<http://www.w3.org/2002/07/owl#sameAs>	<http://linkedgeodata.org/ontology/population>.
	<http://dbpedia.org/property/population>	<http://www.w3.org/2002/07/owl#sameAs>	<http://www4.wiwiss.fu-berlin.de/eurostat/resource/eurostat/population_total>.
	<http://rdf.freebase.com/ns/common.topic.alias>	<http://www.w3.org/2002/07/owl#sameAs>	<http://www.geonames.org/ontology#alternateName>.
	<http://dbpedia.org/ontology/abstract>	<http://www.w3.org/2002/07/owl#sameAs>	<http://odcs.mff.cuni.cz/resource/qe-test/abstract-syn1>.
	<http://odcs.mff.cuni.cz/resource/qe-test/abstract-syn1>	<http://www.w3.org/2002/07/owl#sameAs>	<http://odcs.mff.cuni.cz/resource/qe-test/abstract-syn2>.
};
SPARQL INSERT INTO <http://odcs.mff.cuni.cz/namedGraph/qe-test/property-labels> {
	<http://www.w3.org/2003/01/geo/wgs84_pos#lat>	<http://www.w3.org/2000/01/rdf-schema#label>	"Latitude".
	<http://www.w3.org/2003/01/geo/wgs84_pos#long>	<http://www.w3.org/2000/01/rdf-schema#label>	"Longtitude".
	<http://dbpedia.org/ontology/abstract>	<http://www.w3.org/2000/01/rdf-schema#label>	"Abstract".
};

SPARQL INSERT INTO <http://odcs.mff.cuni.cz/namedGraph/qe-test/berlin/metadata/dbpedia> {
	<http://odcs.mff.cuni.cz/namedGraph/qe-test/berlin/dbpedia>	<http://opendata.cz/infrastructure/odcleanstore/metadataGraph> <http://odcs.mff.cuni.cz/namedGraph/qe-test/berlin/metadata/dbpedia>.
	<http://odcs.mff.cuni.cz/namedGraph/qe-test/berlin/dbpedia>	<http://opendata.cz/infrastructure/odcleanstore/insertedAt>	"2012-04-01T12:34:56"^^<http://www.w3.org/2001/XMLSchema#dateTime>.
	<http://odcs.mff.cuni.cz/namedGraph/qe-test/berlin/dbpedia>	<http://opendata.cz/infrastructure/odcleanstore/score>	"0.9"^^<http://www.w3.org/2001/XMLSchema#double>.
	<http://odcs.mff.cuni.cz/namedGraph/qe-test/berlin/dbpedia>	<http://opendata.cz/infrastructure/odcleanstore/source>	<http://dbpedia.org/page/Berlin>.
};
SPARQL INSERT INTO <http://odcs.mff.cuni.cz/namedGraph/qe-test/berlin/metadata/freebase> {
	<http://odcs.mff.cuni.cz/namedGraph/qe-test/berlin/freebase>	<http://opendata.cz/infrastructure/odcleanstore/metadataGraph> <http://odcs.mff.cuni.cz/namedGraph/qe-test/berlin/metadata/freebase>.
	<http://odcs.mff.cuni.cz/namedGraph/qe-test/berlin/freebase>	<http://opendata.cz/infrastructure/odcleanstore/insertedAt>	"2012-04-02T12:34:56"^^<http://www.w3.org/2001/XMLSchema#dateTime>.
	<http://odcs.mff.cuni.cz/namedGraph/qe-test/berlin/freebase>	<http://opendata.cz/infrastructure/odcleanstore/score>	"0.8"^^<http://www.w3.org/2001/XMLSchema#double>.
	<http://odcs.mff.cuni.cz/namedGraph/qe-test/berlin/freebase>	<http://opendata.cz/infrastructure/odcleanstore/source>	<http://www.freebase.com/view/en/berlin>.
};
SPARQL INSERT INTO <http://odcs.mff.cuni.cz/namedGraph/qe-test/berlin/metadata/geonames> {
	<http://odcs.mff.cuni.cz/namedGraph/qe-test/berlin/geonames>	<http://opendata.cz/infrastructure/odcleanstore/metadataGraph> <http://odcs.mff.cuni.cz/namedGraph/qe-test/berlin/metadata/geonames>.
	<http://odcs.mff.cuni.cz/namedGraph/qe-test/berlin/geonames>	<http://opendata.cz/infrastructure/odcleanstore/insertedAt>	"2012-04-03T12:34:56"^^<http://www.w3.org/2001/XMLSchema#dateTime>.
	<http://odcs.mff.cuni.cz/namedGraph/qe-test/berlin/geonames>	<http://opendata.cz/infrastructure/odcleanstore/score>	"0.8"^^<http://www.w3.org/2001/XMLSchema#double>.
	<http://odcs.mff.cuni.cz/namedGraph/qe-test/berlin/geonames>	<http://opendata.cz/infrastructure/odcleanstore/source> <http://www.geonames.org/2950159/berlin.html>	.
};
SPARQL INSERT INTO <http://odcs.mff.cuni.cz/namedGraph/qe-test/berlin/metadata/linkedgeodata> {
	<http://odcs.mff.cuni.cz/namedGraph/qe-test/berlin/linkedgeodata>	<http://opendata.cz/infrastructure/odcleanstore/metadataGraph> <http://odcs.mff.cuni.cz/namedGraph/qe-test/berlin/metadata/linkedgeodata>.
	<http://odcs.mff.cuni.cz/namedGraph/qe-test/berlin/linkedgeodata>	<http://opendata.cz/infrastructure/odcleanstore/insertedAt>	"2012-04-04T12:34:56"^^<http://www.w3.org/2001/XMLSchema#dateTime>.
	<http://odcs.mff.cuni.cz/namedGraph/qe-test/berlin/linkedgeodata>	<http://opendata.cz/infrastructure/odcleanstore/score>	"0.8"^^<http://www.w3.org/2001/XMLSchema#double>.
	<http://odcs.mff.cuni.cz/namedGraph/qe-test/berlin/linkedgeodata>	<http://opendata.cz/infrastructure/odcleanstore/source>	<http://linkedgeodata.org/page/node240109189>.
};
SPARQL INSERT INTO <http://odcs.mff.cuni.cz/namedGraph/qe-test/berlin/metadata/error> {
	<http://odcs.mff.cuni.cz/namedGraph/qe-test/berlin/error>	<http://opendata.cz/infrastructure/odcleanstore/metadataGraph> <http://odcs.mff.cuni.cz/namedGraph/qe-test/berlin/metadata/error>.
	<http://odcs.mff.cuni.cz/namedGraph/qe-test/berlin/error>	<http://opendata.cz/infrastructure/odcleanstore/score>	"0.8"^^<http://www.w3.org/2001/XMLSchema#double>.
	<http://odcs.mff.cuni.cz/namedGraph/qe-test/berlin/error>	<http://opendata.cz/infrastructure/odcleanstore/source>		<http://example.com>.
};
SPARQL INSERT INTO <http://odcs.mff.cuni.cz/namedGraph/qe-test/germany/metadata/dbpedia> {
	<http://odcs.mff.cuni.cz/namedGraph/qe-test/germany/dbpedia>	<http://opendata.cz/infrastructure/odcleanstore/metadataGraph> <http://odcs.mff.cuni.cz/namedGraph/qe-test/germany/metadata/dbpedia>.
	<http://odcs.mff.cuni.cz/namedGraph/qe-test/germany/dbpedia>	<http://opendata.cz/infrastructure/odcleanstore/insertedAt>	"2012-04-05T12:34:56"^^<http://www.w3.org/2001/XMLSchema#dateTime>.
	<http://odcs.mff.cuni.cz/namedGraph/qe-test/germany/dbpedia>	<http://opendata.cz/infrastructure/odcleanstore/score>	"0.9"^^<http://www.w3.org/2001/XMLSchema#double>.
	<http://odcs.mff.cuni.cz/namedGraph/qe-test/germany/dbpedia>	<http://opendata.cz/infrastructure/odcleanstore/source>	<http://dbpedia.org/page/Germany>.
};



