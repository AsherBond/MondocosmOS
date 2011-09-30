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
class mtseqbuffer
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
	mtseqbuffer(int _bufsize):_size(_bufsize)
	{
	}

	mtseqbuffer(const mtseqbuffer<T> &_buf)
	{
		_size = _buf._size;
	}

	/// \brief destructor.
	~mtseqbuffer(){};
	

	//////////////////////////////////////////////////////////////////////////
	/// \brief Buffer a new data.
	///
	/// The user created a new data item, and write the pointer to the buffer for later access.
	///
	/// \param m [in] the pointer to the data item.
	//////////////////////////////////////////////////////////////////////////
	void put(T* m)
	{
		scoped_lock lock(_mutex);

		if (_data.size() == _size)
		{
// 			{
// 				scoped_lock  lock(io_mutex);
// 				std::cout << "Buffer is full. Waiting..." << std::endl;
// 			}
		
			// wait while the buffer is full.
			while (_data.size() == _size)
			{
				_fullcond.wait(lock);
			}
		}
		
// 		{
// 			scoped_lock  lock(io_mutex);
// 			std::cout << "Sending " << m._timestamp<<" for timestamp..." << _puttimestamp<< std::endl;
// 		}

		// put data.
		_data.push(m);

		// notify the get thread.
		_emptycond.notify_one();
	}

	//////////////////////////////////////////////////////////////////////////
	/// \brief Read a buffered data item.
	///
	/// After a data item is get, it's removed from the buffer and the user, which get the pointer, is 
	/// the only one who can manipulate the item.
	///
	/// \param block [in] Whether block for the comming item.
	/// \return the data.
	//////////////////////////////////////////////////////////////////////////
	T* get(bool block = true)
	{
		scoped_lock lock(_mutex);
		if (_data.empty())
		{
// 			{
// 				scoped_lock lock(io_mutex);
// 				std::cout << "Buffer is empty. Waiting..." << std::endl;
// 			}
			if (block)
			{
				while (_data.empty())
				{
	// 				{
	// 					scoped_lock lock(io_mutex);
	// 					std::cout << "full:Buffer is empty. Waiting..." << std::endl;
	// 					
	// 				}
					_emptycond.wait(lock);
				}		
			}
			else
			{
				return NULL;
			}
		}
		T* i = _data.front();
		_data.pop();
// 		{
// 			scoped_lock lock(io_mutex);
// 			std::cout << "get " <<i._value << std::endl;							
//  		}


		// notify the thread waiting to put data.
		_fullcond.notify_one();

		return i;
    }

	bool full()
	{
		scoped_lock lock(_mutex);
//		std::cout<<"buffer is full"<<std::endl;
		return _data.size() == _size;
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
	/// \brief the size of the buffer.
	//////////////////////////////////////////////////////////////////////////
	unsigned int _size;


	//////////////////////////////////////////////////////////////////////////
	/// \brief the data buffer.
	//////////////////////////////////////////////////////////////////////////
	std::queue<T*> _data;
};
