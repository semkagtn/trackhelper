Trackhelper
===========
Helper tool for working with audio files

### Requirements
* [Maven 3](https://maven.apache.org/download.cgi)
* [Java 8](https://www.oracle.com/technetwork/java/javase/downloads/index.html)

### Installation
* `git clone https://github.com/semkagtn/trackhelper` - Clone repository.
* `cd trackhelper` - Change directory to project.
* `./build.sh 0.1.3` - Run build script. You can build other version (see changelog below).

### Configuration
You can change configuration in the `~/.trackhelper.conf` file.

### Usage
`java -jar trackhelper.jar`

### Changelog
* **0.0.0** - Initial version. Added "set-tags" command.
* **0.1.0** - Added "extract-from-itunes" command.
* **0.1.1** - Some little fixes
* **0.1.2** - Bug fix: now correctly parse artist
* **0.1.3** - ItunesUrlTrackMetadataExtractorSpec - ignored
