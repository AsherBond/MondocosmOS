/*  Misfit Model 3D
 * 
 *  Copyright (c) 2007-2008 Kevin Worcester
 * 
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, 
 *  USA.
 *
 *  See the COPYING file for full license text.
 */


// This file tests the FileDataSource class.

#include <QtTest/QtTest>

#include <sys/types.h>
#include <unistd.h>
#include <errno.h>
#include <string.h>

#include "filedatasource.h"

#include "test_common.h"

#include "local_array.h"

class FileDataSourceTest : public QObject
{
   Q_OBJECT

private:
   FILE * openFile( const char * filename )
   {
      FILE * fp = fopen( filename, "r" );
      if ( !fp )
      {
         fprintf( stderr, "Could not open test file: %s\n", filename );
         exit( -1 );
      }
      return fp;
   }

   FILE * open0k()
   {
      return openFile( "data/intfile0k" );
   }

   FILE * open192k()
   {
      return openFile( "data/intfile192k" );
   }

private slots:
   void testReadEmpty()
   {
      FileDataSource src( open0k() );
      QVERIFY_EQ( (size_t) 0, src.getFileSize() );
      QVERIFY_TRUE( src.eof() );
      QVERIFY_FALSE( src.unexpectedEof() );

      QVERIFY_TRUE( src.seek( 0 ) );
      QVERIFY_TRUE( src.eof() );
      QVERIFY_FALSE( src.unexpectedEof() );

      uint8_t val = 0;
      QVERIFY_FALSE( src.read( val ) );
      QVERIFY_TRUE( src.eof() );
      QVERIFY_TRUE( src.unexpectedEof() );
   }

   void testReadAllUint32()
   {
      FileDataSource src( open192k() );
      const size_t fileSize = 192 * 1024;

      QVERIFY_EQ( fileSize, src.getFileSize() );

      // Verify data while reading
      uint32_t val = 0;
      for ( size_t i = 0; i < fileSize / 4; ++i )
      {
         QVERIFY_FALSE( src.eof() );
         QVERIFY_TRUE( src.read( val ) );
         QVERIFY_EQ( (uint32_t) i, val );
      }
      QVERIFY_TRUE( src.eof() );
      QVERIFY_FALSE( src.unexpectedEof() );

      QVERIFY_FALSE( src.read( val ) );
      QVERIFY_TRUE( src.eof() );
      QVERIFY_TRUE( src.unexpectedEof() );
   }

   void testReadAllAtOnce()
   {
      FileDataSource src( open192k() );
      const size_t fileSize = 192 * 1024;
      local_array<uint8_t> buf = new uint8_t[ fileSize ];

      QVERIFY_EQ( fileSize, src.getFileSize() );
      QVERIFY_TRUE( src.readBytes( buf.get(), fileSize ) );
      QVERIFY_TRUE( src.eof() );
      QVERIFY_FALSE( src.unexpectedEof() );

      // Verify data read
      uint32_t val = 0;
      for ( size_t i = 0; i < fileSize / 4; ++i )
      {
         uint32_t * ptr = (uint32_t *) buf.get();
         QVERIFY_EQ( (uint32_t) ptr[i], i );
      }

      QVERIFY_FALSE( src.read( val ) );
      QVERIFY_TRUE( src.eof() );
      QVERIFY_TRUE( src.unexpectedEof() );
   }

   void testReadError()
   {
      FILE * fp = open192k();
      FileDataSource src( fp );
      const size_t fileSize = 192 * 1024 / 2;
      local_array<uint8_t> buf = new uint8_t[ fileSize ];

      QVERIFY_EQ( fileSize * 2, src.getFileSize() );

      // Read first half
      QVERIFY_TRUE( src.readBytes( buf.get(), fileSize ) );
      QVERIFY_FALSE( src.eof() );
      QVERIFY_FALSE( src.unexpectedEof() );

      // Read second half
      QVERIFY_TRUE( src.readBytes( buf.get(), fileSize ) );
      QVERIFY_TRUE( src.eof() );
      QVERIFY_FALSE( src.unexpectedEof() );

      // Start over
      QVERIFY_TRUE( src.seek(0) );

      // Read first half
      QVERIFY_TRUE( src.readBytes( buf.get(), fileSize ) );
      QVERIFY_FALSE( src.eof() );
      QVERIFY_FALSE( src.unexpectedEof() );

      // Close the file to force an error
      QVERIFY_EQ( 0, close( fileno(fp) ) );

      // Read second half (and expect error)
      QVERIFY_FALSE( src.readBytes( buf.get(), fileSize ) );
      QVERIFY_EQ( (int) EBADF, src.getErrno() );
      QVERIFY_FALSE( src.unexpectedEof() );

      fclose( fp );
   }

   void testBadSeek()
   {
      FileDataSource src( open192k() );
      const size_t fileSize = 192 * 1024;

      QVERIFY_EQ( fileSize, src.getFileSize() );
      QVERIFY_TRUE( src.seek( 0 ) );
      QVERIFY_FALSE( src.eof() );
      QVERIFY_TRUE( src.seek( fileSize / 2 ) );
      QVERIFY_FALSE( src.eof() );
      QVERIFY_TRUE( src.seek( fileSize ) );
      QVERIFY_TRUE( src.eof() );
      QVERIFY_TRUE( src.seek( fileSize / 2 ) );
      QVERIFY_FALSE( src.eof() );
      QVERIFY_TRUE( src.seek( 0 ) );
      QVERIFY_FALSE( src.eof() );

      // Here is the error
      QVERIFY_FALSE( src.seek( fileSize + 1 ) );
   }

};

QTEST_MAIN(FileDataSourceTest)
#include "filedatasource_test.moc"
