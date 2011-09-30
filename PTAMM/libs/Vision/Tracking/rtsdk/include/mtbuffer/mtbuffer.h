#pragma once
#include <vector>

//////////////////////////////////////////////////////////////////////////
/// \defgroup Buffer Multi-threaded buffer
//////////////////////////////////////////////////////////////////////////

// disable the boost auto link feature.
#ifndef BOOST_THREAD_NO_LIB
#define BOOST_THREAD_NO_LIB
#endif

#include <boost/thread/thread.hpp>
#include <boost/thread/mutex.hpp>
#include <boost/thread/condition.hpp>
#include <boost/thread/xtime.hpp>

#include <queue>

//////////////////////////////////////////////////////////////////////////
// libraries.
#include "boostthreadlib.h"
//////////////////////////////////////////////////////////////////////////

//////////////////////////////////////////////////////////////////////////
/// \ingroup Buffer
/// \brief Support multi-threaded buffer, with a timestamp to sort the buffered item.
///
/// The buffer uses the boost::thread library to synchronize the buffer read and write.
/// It contains the pointer to the buffered item, and leaves the user to create the item.
/// User can read and write the buffer with get() and put().
/// After an item is get, the data is removed from the buffer.
//////////////////////////////////////////////////////////////////////////
template<class T>
class mtbuffer
{
public:
	
	/// \brief Scoped lock of boost::thread.
	typedef boost::mutex::scoped_lock
        scoped_lock;

	//////////////////////////////////////////////////////////////////////////
	/// \brief Constructor.
	///
	/// Initialize the item count, the get and put heads, and the timestamp.
	///
	/// \param _bufsize [in] initialize the buffer size. 
	//////////////////////////////////////////////////////////////////////////
	mtbuffer(int _bufsize):_size(_bufsize),_puttimestamp(0)
	{
	}

	mtbuffer(const mtbuffer<T> &_buf)
	{
		_size = _buf._size;
		_puttimestamp = _buf._puttimestamp;
	}

	/// \brief destructor.
	~mtbuffer(){};
	
	//////////////////////////////////////////////////////////////////////////
	/// \brief Buffer a new data.
	///
	/// The user created a new data item, and write the pointer to the buffer for later access.
	///
	/// \param m [in] the pointer to the data item.
	/// \param waittime [in] whether wait for the buffer when it's not its time stamp.
	/// \param waitfull [in] whether wait for the buffer when it's full.
	//////////////////////////////////////////////////////////////////////////
	bool put(T* m, bool waitfull = true, bool waittime = true)
	{
		scoped_lock lock(_mutex);

		// if the timestamp is not right, and we wanna not wait, return here.
		if (m->_timestamp != _puttimestamp && !waittime)
		{
			return false;
		}

		// wait while the time is not ready.
		while (m->_timestamp != _puttimestamp)
		{
			_timecond.wait(lock);
		}

		// if the buffer is full and we wanna not wait, return here.
		if (_data.size() == _size && !waitfull)
		{
			return false;
		}
	
		// wait while the buffer is full.
		while (_data.size() == _size)
		{
			_fullcond.wait(lock);
		}

		// put data is ok now.
		_data.push(m);

		++ _puttimestamp;

		// notify the get thread.
		_emptycond.notify_one();

		// notify the put thread which wait for their time.
		_timecond.notify_all();

		// put succeed.
		return true;
	}

	//////////////////////////////////////////////////////////////////////////
	/// \brief Read a buffered data item.
	///
	/// After a data item is get, it's removed from the buffer and the user, which get the pointer, is 
	/// the only one who can manipulate the item.
	///
	/// \param waitempty [in] Whether block for the comming item.
	/// \return the data.
	//////////////////////////////////////////////////////////////////////////
	T* get(bool waitempty = true)
	{
		scoped_lock lock(_mutex);

		if (_data.empty())
		{
			if (!waitempty)
			{
				return NULL;
			}
			else
			{
				++_size;

				//printf("empty\n");

				// notify the thread waiting to put data.
				_fullcond.notify_one();

				// get current time.
				boost::xtime waitduration;
				boost::xtime_get(&waitduration, boost::TIME_UTC);

				// add 2 second.
				waitduration.sec += 2;

				bool newdata = _emptycond.timed_wait(lock, waitduration);

				//////////////////////////////////////////////////////////////////////////
				-- _size;
			
				if (!newdata)
				{
					return NULL;
				}	
			}
		}

		if (_data.size() == 0)
		{
			return NULL;
		}
		
		T* m = _data.front();
		_data.pop();

		_fullcond.notify_one();
		return m;
    }

protected:
	//////////////////////////////////////////////////////////////////////////
	/// \brief Mutex of the put and get operation.
	///
	/// put() and get() operation cannot execute at the same time.
	//////////////////////////////////////////////////////////////////////////
	boost::mutex _mutex;

	//////////////////////////////////////////////////////////////////////////
	/// \brief Conditionally wait for the put() operation while buffer is full.
	///
	/// While the buffer is full, the put() operation must hold until a get()
	/// operation notify them.
	//////////////////////////////////////////////////////////////////////////
	boost::condition _fullcond;

	//////////////////////////////////////////////////////////////////////////
	/// \brief Conditionally wait for the put operation while buffer is empty.
	/// 
	/// While the buffer is empty, the get() operation must hold until a put()
	/// operation notify them.
	//////////////////////////////////////////////////////////////////////////
	boost::condition _emptycond;

	//////////////////////////////////////////////////////////////////////////
	/// \brief Conditionally wait for the put operation.
	///
	/// The put() operation is scheduled according to the timestamp member.
	/// If the time is ready, it must hold until a successful put() notify them.
	//////////////////////////////////////////////////////////////////////////
	boost::condition _timecond;


	//////////////////////////////////////////////////////////////////////////
	/// \brief the size of the buffer.
	//////////////////////////////////////////////////////////////////////////
	unsigned int _size;

	//////////////////////////////////////////////////////////////////////////
	/// \brief The timestamp of put operation.
	///
	/// Only the item with the same timestamp can write the buffer, otherwise,
	/// they hold until #_timecond is notified.
	//////////////////////////////////////////////////////////////////////////
	unsigned int _puttimestamp;


	//////////////////////////////////////////////////////////////////////////
	/// \brief the data buffer.
	//////////////////////////////////////////////////////////////////////////
	std::queue<T*> _data;
};

