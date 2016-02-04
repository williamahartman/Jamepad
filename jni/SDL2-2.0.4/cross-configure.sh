#!/bin/sh

CONFIG_SHELL=/bin/sh
export CONFIG_SHELL
PREFIX=/usr/local/cross-tools
if [ "$TARGET" = "" ]; then
    TARGET=`basename $0 -configure.sh`
fi
if [ "$TARGET" = "" -o "$TARGET" = "cross" ]; then
    TARGET=i686-w64-mingw32
fi
PATH="$PREFIX/bin:$PREFIX/$TARGET/bin:$PATH"
export PATH
PKG_CONFIG_PATH=$PREFIX/$TARGET/lib/pkgconfig
export PKG_CONFIG_PATH
if [ -f "$PREFIX/$TARGET/bin/$TARGET-sdl-config" ]; then
    SDL_CONFIG="$PREFIX/$TARGET/bin/$TARGET-sdl-config"
    export SDL_CONFIG
fi
cache=$TARGET-config.cache
if [ -f configure ]; then
    CONFIGURE=configure
elif [ -f ../configure ]; then
    CONFIGURE=../configure
elif [ -f ../../configure ]; then
    CONFIGURE=../../configure
else
    echo "Couldn't find configure - aborting!"
    exit 2
fi
export CC="$TARGET-gcc -static-libgcc"
export CXX="$TARGET-g++ -static-libgcc -static-libstdc++"
sh $CONFIGURE --cache-file="$cache" \
	--target=$TARGET --host=$TARGET --build=i386-linux \
	--prefix=$PREFIX/$TARGET $*
status=$?
rm -f "$cache"
exit $status
