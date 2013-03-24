ODCleanStore
============

In short, ODCleanStore is a server application for management of Linked Data - it stores data in RDF, processes them and provides integrated views on the data.

ODCleanStore accepts arbitrary RDF data through a (SOAP) webservice, together with provenance and other metadata. The data is processed by  _transformers_ in one of a set of customizable  _pipelines_ and stored to a persistent store. The stored data can be accessed also through a webservice (REST). Linked Data consumers can send queries and custom query policies to this webservice and receive aggregated/integrated RDF data relevant for their query, together with information about provenance and data quality. 

[[/images/odcs-internal.png|alt=<br />Overview of ODCleanStore architecture|align=center]]

More information about how ODCleanStore works can be found on GitHub wiki pages [[About]] and [[How It Works]].
