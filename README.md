ODCleanStore
============

In short, ODCleanStore is a server application for management of Linked Data - it stores data in RDF, processes them and provides integrated views on the data.

_Note: The project is currently being migration to GitHub, please have patientce until we're done._



## What it can do

* ODCleanStore accepts arbitrary RDF data through a (SOAP) webservice, together with provenance and other metadata. 
* The data is processed by  _transformers_ in one of a set of customizable  _pipelines_ and stored to a persistent store. 
* The whole process can be administered and monitored in the administration web interface. 
* The stored data can be accessed also through a webservice (REST). Linked Data consumers can send queries and custom query policies to this webservice and receive aggregated/integrated RDF data relevant for their query, together with information about provenance and data quality. 

![Overview of ODCleanStore architecture](https://raw.github.com/wiki/ODCleanStore/ODCleanStore/images/odcs-internal-small.png)

More information about how ODCleanStore works can be found on GitHub wiki pages [[About]] and [[How It Works]].

## Downloads

Release packages can be downloaded from [SourceForge](https://sourceforge.net/projects/odcleanstore/files/odcleanstore/) or you can download the latest version directly:

[![Download the latest version](https://raw.github.com/wiki/ODCleanStore/ODCleanStore/images/download.png)](https://sourceforge.net/projects/odcleanstore/files/latest/download?source=files)

The release package contains all required binaries of ODCleanStore and utility scripts, installer, documentation and sample files. Detailed [documentation](http://sourceforge.net/projects/odcleanstore/files/manual/) can also be obtained at SourceForge.



## License

ODCleanStore is published as Open Source under [Apache License Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)

## Credits
ODCleanStore is developed at the Charles University in Prague, Faculty of Mathematics and Physics as part of the OpenData.cz initiative and the LOD2 project.

