YalePublic-android [![Build Status](https://travis-ci.org/YaleSTC/YalePublic-android.svg?branch=7_maps)](https://travis-ci.org/YaleSTC/YalePublic-android)
==================

Thank you to Mats Hofman for an implementation of a RSS Reader:

	https://github.com/matshofman/Android-RSS-Reader-Library

To build the most recent version of the application:

	chmod +x gradlew install-dummykeys.sh
	./install-dummykeys.sh
	(Replace the keys in the files with your own valid ones)
	./gradlew clean assembleDebug

Then you'll find the application at:

	build/outputs/apk/YalePublic-android-debug-unaligned.apk

##Changelog:

### [Unreleased (v.2.0.1)]

#### Layouts
  - Unified font sizes across all of the lists 
  - Unified spacing across all of the lists
  - Switched the app from ad-hoc style choices to theme-based system
  - Switched all of the styles within the app to "white-on-black" rather than "black-on-white" design
  - Fixed activity titles

#### Crash fixes
  - Fixed the app crash on using software back button within photos.

#### Other
  - Html4 escape sequences are unescaped. The problem remains for Html5 sequences.
