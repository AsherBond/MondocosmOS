#!/bin/sh -x
#
# Run this script from this directory...
#
# TODO: Currently no 64-bit support... its pointless anyway since there is
#	no 64-bit support for jmf, joal, fobs etc...
#
# This script tries to get all the Java extension libs used by Project Wonderland
# and zip them to be checked into the CVS tree...
#
# To update the bundles for one (or more) of the packages do the following
# (for java3d for example)
#   setup-bundles.sh -a j3d
#
tmpdir=/tmp/get-bundles-java-ext-$$

get=0
unpack=0
bundle=0
verbose=0
zopts=-q

wgetf()
{
    echo "    wget  [`basename $1`]"
    if [ $verbose -eq 1 ]; then
	wget -nv -N "$1"
    else
	wget -q -N "$1"
    fi
}

#
# For each OS :
#   Copy jars into the 'jars' directory
#   copy the native libraries to the 'native' directory
#
copy_files()
{
    pkg=$1
    os=$2
    tdir=$3
    bf=`basename $4`


    case $2 in
    lin*) os=linux	;;
    sol*) os=solaris	;;
    win*) os=win32	;;
    mac*) os=macosx	;;
    esac

    pkgdir=$staging_dir/$os/$pkg
    /bin/rm -rf $pkgdir

    mkdir -p $pkgdir/native $pkgdir/jars

    ( echo "Extracted from : $bf"
      echo "On date        : `date`"
    ) > $pkgdir/$pkg-bundle-readme.txt

    # Copy all the license files and prefix them
    for f in `find $tdir -name "*.txt" -o -name COPYING -o -name "README*" -o -name "LICENSE*"`
    do
	mv -f $f $pkgdir/$pkg-`basename $f`
    done

    find $tdir -name "*.jar" -exec cp -f {} $pkgdir/jars \;

    case $os in
    linux)  find $tdir -name "*.so" -exec cp -f {} $pkgdir/native \;	;;
    sol*)   find $tdir -name "*.so" -exec cp -f {} $pkgdir/native \; -o -name amd64 -prune  ;;
    win*)   find $tdir -name "*.dll" -exec cp -f {} $pkgdir/native \;	;;
    mac*)   find $tdir -name "*.jnilib" -exec cp -f {} $pkgdir/native \;	;;
    esac
}

get_j3d()
{
    version=1_5_2-pre1-071115

    /bin/rm -f java3d-*.zip
    wget -qN https://java3d.dev.java.net/binary-builds-pre.html
    files=`grep $version binary-builds-pre.html | grep -v amd64 | grep -v sparc | cut -d\" -f2`
    for f in $files
    do
	wgetf $f
    done
}

unpack_j3d()
{
    for os in linux windows macosx solaris
    do
	/bin/rm -rf $tmpdir
	mkdir -p $tmpdir

	bfile=`find . -name "java3d-*$os*.zip"`
	if [ "$bfile" = "" ]; then
	    echo "    skip  [java3d/$os]"
	    continue
	fi

	echo "    unzip [$bfile]"
	unzip $zopts -od $tmpdir $bfile
	unzip $zopts -od $tmpdir $tmpdir/*/*.zip

	copy_files j3d $os $tmpdir $bfile
    done
}

get_jai()
{
    version=1_1_4
    prefix=pre-dr-b03-lib
    site=http://download.java.net/media/jai/builds/daily/`date +%Y-%m-%d`
    date=`date +%d_%b_%Y`

    find . -name "jai-*.zip" -exec rm -f {} \; -o -name "jai-imageio*.zip" -prune 

    wgetf ${site}/jai-${version}-${prefix}-linux-i586-${date}.zip
    wgetf ${site}/jai-${version}-${prefix}-windows-i586-${date}.zip
    wgetf ${site}/jai-${version}-${prefix}-solaris-i586-${date}.zip
}

unpack_jai()
{
    mac=0

    for os in linux windows solaris
    do
	/bin/rm -rf $tmpdir
	mkdir -p $tmpdir

	bfile=`find . -name "jai-*$os*.zip" -print -o -name "jai-imageio*.zip" -prune`
	if [ "$bfile" = "" ]; then
	    echo "    skip  [jai/$os]"
	    continue
	fi

	echo "    unzip [$bfile]"
	unzip $zopts -od $tmpdir $bfile

	copy_files jai $os $tmpdir $bfile

	# Copy the jars from one of the bundles to the macos directory
	# so that we will have at lease plain java support for JAI
	if [ $mac -eq 0 ]; then
	    copy_files jai macosx $tmpdir $bfile
	    mac=1
	fi
    done
}

get_jai_iio()
{
    version=1_2
    prefix=pre-dr-b04-lib
    site=http://download.java.net/media/jai-imageio/builds/daily/`date +%Y-%m-%d`
    date=`date +%d_%b_%Y`

    /bin/rm -f jai-imageio-*.zip

    wgetf ${site}/jai-imageio-${version}-${prefix}-linux-i586-${date}.zip
    wgetf ${site}/jai-imageio-${version}-${prefix}-windows-i586-${date}.zip
    wgetf ${site}/jai-imageio-${version}-${prefix}-solaris-i586-${date}.zip
}

unpack_jai_iio()
{
    mac=0

    for os in linux windows solaris
    do
	/bin/rm -rf $tmpdir
	mkdir -p $tmpdir

	bfile=`find . -name "jai-imageio*$os*.zip"`
	if [ "$bfile" = "" ]; then
	    echo "    skip  [jai/$os]"
	    continue
	fi

	echo "    unzip [$bfile]"
	unzip $zopts -od $tmpdir $bfile

	copy_files jai_iio $os $tmpdir $bfile

	# Copy the jars from one of the bundles to the macos directory
	# so that we will have at lease plain java support for JAI
	if [ $mac -eq 0 ]; then
	    copy_files jai_iio macosx $tmpdir $bfile
	    mac=1
	fi
    done
}

get_joal()
{
    version=1.1.1
    prefix=pre
    site=http://download.java.net/media/joal/builds/nightly
    date=`date +%Y%m%d`

    /bin/rm -f joal-*.zip

    wgetf ${site}/joal-${version}-${prefix}-${date}-linux-i586.zip
    wgetf ${site}/joal-${version}-${prefix}-${date}-windows-i586.zip
    wgetf ${site}/joal-${version}-${prefix}-${date}-macosx-universal.zip

    # TODO: Build openal & joal for solaris and get it...
}

unpack_joal()
{
    sol=0
    for os in windows linux macosx
    do
	/bin/rm -rf $tmpdir
	mkdir -p $tmpdir

	bfile=`find . -name "joal-*$os*.zip"`
	if [ "$bfile" = "" ]; then
	    echo "    skip  [joal/$os]"
	    continue
	fi

	echo "    unzip [$bfile]"
	unzip $zopts -od $tmpdir $bfile
	copy_files joal $os $tmpdir $bfile

	# Copy the jars from one of the bundles to the solaris directory
	# so that we will have at lease plain java support for JOAL
	if [ $sol -eq 0 ]; then
	    copy_files joal solaris-x86 $tmpdir $bfile
	    sol=1
	fi
    done
}

get_jogl()
{
    version=1.1.1
    prefix=pre

    /bin/rm -f joal-*.zip

    wget -qN 'https://jogl.dev.java.net/servlets/ProjectDocumentList?folderID=9260&expandFolder=9260&folderID=0'
    mv 'ProjectDocumentList?folderID=9260&expandFolder=9260&folderID=0' index.html

    #wget -qN https://jogl.dev.java.net
    files=`grep \.zip index.html | cut -d= -f2 | cut -d\> -f1 | egrep "linux-i586|macosx-universal|windows-i586|solaris-i586"`
    for f in $files
    do
	wgetf $f
    done
}

unpack_jogl()
{
    sol=0
    for os in windows linux macosx solaris
    do
	/bin/rm -rf $tmpdir
	mkdir -p $tmpdir

	bfile=`find . -name "jogl-*$os*.zip"`
	if [ "$bfile" = "" ]; then
	    echo "    skip  [jogl/$os]"
	    continue
	fi

	echo "    unzip [$bfile]"
	unzip $zopts -od $tmpdir $bfile
	copy_files jogl $os $tmpdir $bfile
    done
}

get_jinput()
{
    site=http://www.newdawnsoftware.com/resources/jinput
    date=20061029

    wgetf ${site}/jinput_combined_dist_${date}.zip
}

unpack_jinput()
{
    # TODO: there is no solaris support for JInput, but we wil copy the jars
    #	    anyway atleast so that we can build the workspace.
    for os in linux windows macosx solaris
    do
	/bin/rm -rf $tmpdir
	mkdir -p $tmpdir

	bfile=`find . -name "jinput*.zip"`
	if [ "$bfile" = "" ]; then
	    echo "    skip  [jinput/$os]"
	    continue
	fi

	echo "    unzip [$bfile]"
	unzip $zopts -od $tmpdir $bfile
	copy_files jinput $os $tmpdir $bfile
    done
}

get_fobs()
{
    version=0.4.1
    site=http://downloads.sourceforge.net/fobs

    # wgetf "${site}/fobs4jmf_${version}_win32.zip?use_mirror=easynews"
    # wgetf "${site}/fobs4jmf-${version}-ubuntu-edgy.tar.gz?use_mirror=easynews"
    # wgetf "${site}/Fobs4JMF-${version}-MacOSX_10.4-Bin.dmg.zip?use_mirror=easynews"
    # wgetf "${site}/Fobs4JMF-${version}-OSX_10.4-JMStudio.dmg.zip?use_mirror=easynews"
}

unpack_fobs()
{
    /bin/rm -rf $tmpdir
    mkdir -p $tmpdir

    bfile=`find . -name "fobs*win*.zip"`
    if [ "$bfile" = "" ]; then
	echo "    skip  [fobs/windows]"
    else
	echo "    unzip [$bfile]"
	unzip $zopts -od $tmpdir $bfile
	find $tmpdir -name jmf.jar -exec rm {} \;
	copy_files fobs windows $tmpdir $bfile
	/bin/rm -rf $tmpdir/*
    fi

    bfile=`find . -name "fobs*tar.gz"`
    if [ "$bfile" = "" ]; then
	echo "    skip  [fobs/linux]"
    else
	echo "    unzip [$bfile]"
	gunzip -c $bfile | (cd $tmpdir ; tar xf -)
	find $tmpdir -name jmf.jar -exec rm {} \;
	copy_files fobs linux $tmpdir $bfile
	/bin/rm -rf $tmpdir/*
    fi

    bfile=`find . -name "fobs*macosx*.zip"`
    if [ "$bfile" = "" ]; then
	echo "    skip  [fobs/macosx]"
    else
	echo "    unzip [$bfile]"
	unzip $zopts -od $tmpdir $bfile
	find $tmpdir -name jmf.jar -exec rm {} \;
	copy_files fobs macosx $tmpdir $bfile
	/bin/rm -rf $tmpdir/*
    fi

    ## TODO: How do we unpack the FOBS dmg file for mac
    ## TODO: No solaris support (both FFMPEG and FOBS needs to be built for solaris)
}

do_bundle()
{
    pkg=$1
    for os in linux win32 macosx solaris
    do
	ofile=$os/$pkg.zip
	sdir=$staging_dir/$os/$pkg

	if [ ! -d $sdir ]; then
	    echo "    skip [$ofile]"
	    continue
	fi

	echo "    zip   [$ofile]"
	ofile=$zip_dir/$ofile
	mkdir -p `dirname $ofile`
	/bin/rm -f $ofile

	cd $sdir
	zip $zopts -r -9 $ofile .
    done
}

do_sgs()
{
    pkg=SunGameServer
    zip=$1

    /bin/rm -rf $tmpdir
    mkdir -p $tmpdir

    echo "Repackaging $pkg"

    echo "    unzip [$zip]"
    unzip -d $tmpdir $zopts $zip

    cd $tmpdir
    mv `/bin/ls` darkstar

    ( echo "Extracted from : `basename $zip`"
      echo "On date        : `date`"
    ) > $pkg-bundle-readme.txt

    echo "    zip   [$pkg.zip]"
    zip $zopts -r -9 $zip_dir/$pkg.zip .
    cd $pwd
}

usage()
{
    echo
    echo "Usage: setup-bundles.sh [options] [j3d|jai|jai_iio|joal|jinput|fobs]"
    echo "  -g : get packages"
    echo "  -u : unpack packages"
    echo "  -b : create bundle for each OS"
    echo "  -a : do all of the above"
    echo
    echo "  -sgs <sgs-zip-bundle> : rebundle SGS/Darkstar"
    echo

    exit 0
}

pwd=`pwd`
staging_dir=$pwd/staging
bundles_dir=$staging_dir/bundles
zip_dir=$pwd/zip

while [ $# -gt 0 ]
do
    case "$1" in
    -g)	get=1		;;
    -u) unpack=1	;;
    -b) bundle=1	;;
    -a) get=1 ; unpack=1 ; bundle=1 ;;
    -v) verbose=1	;
	zopts=		;;
    -s*)do_sgs $2	; shift	    ;;
    -*) usage		;;
    *)	break		;;
    esac

    shift
done

mkdir -p $bundles_dir

#
# Setup and create the dirs...
#
for p in $*
do
    cd $bundles_dir

    if [ $get -eq 1 ]; then
	echo "Downloading $p"
	get_$p
    fi

    if [ $unpack -eq 1 ]; then
	echo "Unpacking $p"
	unpack_$p
    fi

    if [ $bundle -eq 1 ]; then
	echo "Bundling $p"
	do_bundle $p
    fi
done

cd $pwd

# /bin/rm -rf $tmpdir
