#pragma  once
// disable the boost auto link feature.
#ifndef BOOST_THREAD_NO_LIB
#define BOOST_THREAD_NO_LIB
#endif

#include "others/misc/dllexp.h"

#include <boost/thread/thread.hpp>
#include <boost/thread/mutex.hpp>
#include <iostream>

#include "mtbuffer.h"
#include "objectpool.h"
class CVideoFrame;

class DLL_DLL_EXPORT videowriter
{
public:
	videowriter();

	videowriter(mtbuffer<CVideoFrame> *buf, object_pool<CVideoFrame> *pool);

	videowriter(const videowriter& obj);

	void operator()();
protected:
	bool process( CVideoFrame * nn);

	bool _continue;
	mtbuffer<CVideoFrame> *_buf;
	object_pool<CVideoFrame> *_pool;
};


class DLL_DLL_EXPORT videoreader
{
public:
	videoreader();

	videoreader(mtbuffer<CVideoFrame> *buf, object_pool<CVideoFrame> *pool);

	videoreader(const videoreader& obj);
	
	void operator()();
protected:
	bool process( CVideoFrame * nn);
	bool _continue;

	mtbuffer<CVideoFrame> *_buf;
	object_pool<CVideoFrame> *_pool;
};

class DLL_DLL_EXPORT videoreadwriter
{
public:
	videoreadwriter(mtbuffer<CVideoFrame> *inbuf, mtbuffer<CVideoFrame> *outbuf);
	
	videoreadwriter();
	videoreadwriter(const videoreadwriter& obj);

	void operator()();

protected:
	bool process( CVideoFrame * nn);

	bool _continue;

	mtbuffer<CVideoFrame> *_inbuf;
	mtbuffer<CVideoFrame> *_outbuf;	
};