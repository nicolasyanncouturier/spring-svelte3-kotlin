#!/bin/zsh

new_version=$1
if [[ "x$new_version" == "x" ]]; then
  echo "Usage: release.sh <x.x.x>";
  exit 1
fi

if ! [[ -e "./com/github/nicolasyanncouturier/spring-svelte3-kotlin/" ]]; then
  mkdir -p "./com/github/nicolasyanncouturier/spring-svelte3-kotlin"
fi

mvn versions:set -DnewVersion="$new_version" || exit 2
mvn clean install -DcreateChecksum=true || exit 3
if [[ -e "./com/github/nicolasyanncouturier/spring-svelte3-kotlin/maven-metadata-local.xml" ]]; then
  last_updated=$(xmlstarlet sel -t -v "/metadata/versioning/lastUpdated" "$HOME/.m2/repository/com/github/nicolasyanncouturier/spring-svelte3-kotlin/maven-metadata-local.xml")
  xmlstarlet ed -P --inplace -u "/metadata/versioning/release" -v "$new_version" "./com/github/nicolasyanncouturier/spring-svelte3-kotlin/maven-metadata-local.xml" || exit 4
  xmlstarlet ed -P --inplace -u "/metadata/versioning/lastUpdated" -v "$last_updated" "./com/github/nicolasyanncouturier/spring-svelte3-kotlin/maven-metadata-local.xml" || exit 5
else
  cp "$HOME/.m2/repository/com/github/nicolasyanncouturier/spring-svelte3-kotlin/maven-metadata-local.xml" "./com/github/nicolasyanncouturier/spring-svelte3-kotlin/maven-metadata-local.xml" || exit 7
  xmlstarlet ed -P --inplace -d "/metadata/versioning/versions/version" "./com/github/nicolasyanncouturier/spring-svelte3-kotlin/maven-metadata-local.xml" || exit 8
fi
xmlstarlet ed -P --inplace -s "/metadata/versioning/versions" -t elem -n "version" -v "$new_version" "./com/github/nicolasyanncouturier/spring-svelte3-kotlin/maven-metadata-local.xml" || exit 9
cp "./com/github/nicolasyanncouturier/spring-svelte3-kotlin/maven-metadata-local.xml" "./com/github/nicolasyanncouturier/spring-svelte3-kotlin/maven-metadata-local.xml.bak" || exit 10
xmlstarlet fo "./com/github/nicolasyanncouturier/spring-svelte3-kotlin/maven-metadata-local.xml.bak" > "./com/github/nicolasyanncouturier/spring-svelte3-kotlin/maven-metadata-local.xml" || exit 11
rm -f "./com/github/nicolasyanncouturier/spring-svelte3-kotlin/maven-metadata-local.xml.bak" || exit 12
cp -Rf "$HOME/.m2/repository/com/github/nicolasyanncouturier/spring-svelte3-kotlin/$new_version" "./com/github/nicolasyanncouturier/spring-svelte3-kotlin/$new_version" || exit 13

echo "Release version $new_version"
rm -rf *.versionsBackup
git add -A .
git commit -am "[release] prepare release $new_version" || exit 14
git push origin master || exit 15
git tag "$new_version" -a -m "[release] $new_version" || exit 16
git push origin "$new_version" || exit 17

M="$(mvn build-helper:parse-version help:evaluate -Dexpression=parsedVersion.majorVersion -q -DforceStdout)"
m="$(mvn build-helper:parse-version help:evaluate -Dexpression=parsedVersion.minorVersion -q -DforceStdout)"
ni="$(mvn build-helper:parse-version help:evaluate -Dexpression=parsedVersion.nextIncrementalVersion -q -DforceStdout)"
dev_version="$M.$m.$ni-SNAPSHOT"
echo "Set new development version $dev_version"
mvn versions:set -DnewVersion="$dev_version" || exit 18
rm -rf *.versionsBackup
git commit -am "[release] prepare for next development iteration" || exit 19
git push origin master || exit 20